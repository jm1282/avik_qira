import React, { useCallback, useMemo } from 'react'
import { SxProps } from '@mui/system'
import Checkbox from '@mui/material/Checkbox'
import Button from '@mui/material/Button'
import Grid from '@mui/material/Grid'
import List from '@mui/material/List'
import ListItem from '@mui/material/ListItem'
import ListItemButton from '@mui/material/ListItemButton'
import ListItemIcon from '@mui/material/ListItemIcon'
import ListItemText from '@mui/material/ListItemText'
import Paper from '@mui/material/Paper'
import Tab from '@mui/material/Tab'
import Tabs from '@mui/material/Tabs'
import ArrowLeft from '@mui/icons-material/KeyboardArrowLeft'
import DoubleArrowLeft from '@mui/icons-material/KeyboardDoubleArrowLeft'
import ArrowRight from '@mui/icons-material/KeyboardArrowRight'
import DoubleArrowRight from '@mui/icons-material/KeyboardDoubleArrowRight'
import lodash from 'lodash'

import Locale from '../model/Locale'
import LocaleAlias from '../model/LocaleAlias'
import { sortLocalesByMapping } from '../utils/localeUtils'

type LocaleSelectorProps = {
  defaultLocales?: string[]
  locales: Locale[]
  localeAliases: LocaleAlias[]
  onChange?: (data: string[]) => void
}

enum OptionType {
  Locale,
  Alias,
}

const itemSx: SxProps = {
  '&.MuiListItemButton-root span': {
    fontSize: 12,
    p: 0,
  },
}

const operationButtonSx: SxProps = {
  minWidth: 125,
}

/**
 * The component to edit locale data
 *
 * @param props - The locale properties
 * @returns - The element to edit the locale data
 */
const LocaleSelector = (props: LocaleSelectorProps): JSX.Element => {
  const { defaultLocales, locales, localeAliases, onChange } = props
  const [localeOptionsChecked, setLocaleOptionsChecked] = React.useState<Locale[]>([])
  const [aliasChecked, setAliasChecked] = React.useState<LocaleAlias[]>([])
  const [localeSelectedChecked, setLocaleSelectedChecked] = React.useState<Locale[]>([])
  const [localeOptions, setLocaleOptions] = React.useState<Locale[]>(sortLocalesByMapping(locales))
  const [optionsType, setOptionsType] = React.useState<OptionType>(OptionType.Alias)

  const defaultSelectedLocales =
    defaultLocales?.map(localeTag => {
      const locale = locales.find(locale => locale.mapping === localeTag)
      if (locale) {
        return locale
      } else {
        const invisibleLocale = new Locale()
        invisibleLocale.localeId = localeTag
        invisibleLocale.mapping = localeTag
        invisibleLocale.displayName = `${localeTag} (Removed)`
        invisibleLocale.isVisible = false
        return invisibleLocale
      }
    }) || []
  const [localeSelected, setLocaleSelected] = React.useState<Locale[]>(defaultSelectedLocales)

  React.useEffect(() => {
    setLocaleOptionsChecked([])
    setAliasChecked([])
  }, [optionsType])

  /**
   * Handle the event to check locale from locale option list
   *
   * @param locale - The locale checked in locale option list
   * @returns The function to run when check the locale
   */
  const handleCheckedLocales = (locale: Locale) => () => {
    const checkedIndex = lodash.findIndex(localeOptionsChecked, ['mapping', locale.mapping])
    const newChecked = [...localeOptionsChecked]
    if (checkedIndex === -1) {
      newChecked.push(locale)
    } else {
      newChecked.splice(checkedIndex, 1)
    }
    setLocaleOptionsChecked(newChecked)
  }

  /**
   * Handle the event to check locale alias from locale alias option list
   *
   * @param localeAlias - The locale alias checked in locale alias option list
   * @returns The function to run when check the locale alias
   */
  const handleCheckedLocaleAliases = (localeAlias: LocaleAlias) => () => {
    const checkedIndex = lodash.findIndex(aliasChecked, ['aliasId', localeAlias.aliasId])
    const newChecked = [...aliasChecked]
    if (checkedIndex === -1) {
      newChecked.push(localeAlias)
    } else {
      newChecked.splice(checkedIndex, 1)
    }
    setAliasChecked(newChecked)
  }

  /**
   * Handle the event to check locale from locale selected list
   *
   * @param locale - The locale checked in locale selected list
   * @returns The function to run when check the locale
   */
  const handleCheckedSelectedLocales = (locale: Locale) => () => {
    const checkedIndex = lodash.findIndex(localeSelectedChecked, ['mapping', locale.mapping])
    const newChecked = [...localeSelectedChecked]
    if (checkedIndex === -1) {
      newChecked.push(locale)
    } else {
      newChecked.splice(checkedIndex, 1)
    }
    setLocaleSelectedChecked(newChecked)
  }

  /**
   * Click to add locales from locale options list
   */
  const addLocales = () => {
    const newSelectedLocales = sortLocalesByMapping(
      lodash.unionBy(localeOptionsChecked, localeSelected, 'mapping')
    )
    changeLocales(newSelectedLocales)
  }

  /**
   * Click to add all locales from locale options list
   */
  const addAllLocales = () => {
    const newSelectedLocales = sortLocalesByMapping(
      lodash.unionBy(localeOptions, localeSelected, 'mapping')
    )
    changeLocales(newSelectedLocales)
  }

  /**
   * Click to add locales by locale alias from locale alias options list
   */
  const addLocaleAliases = () => {
    const selectLocalesFromAliases = lodash.uniq(
      lodash.flatten(aliasChecked.map(alias => alias.locales))
    )
    const selectedLocales = locales.filter(locale =>
      selectLocalesFromAliases.includes(locale.mapping)
    )
    const newSelectedLocales = sortLocalesByMapping(
      lodash.unionBy(selectedLocales, localeSelected, 'mapping')
    )
    changeLocales(newSelectedLocales)
  }

  /**
   * Remove locales from locale selected list
   */
  const removeLocales = () => {
    const newSelectLocales = lodash.differenceBy(localeSelected, localeSelectedChecked, 'mapping')
    changeLocales(newSelectLocales)
  }

  /**
   * Remove locales from locale selected list
   */
  const removeAllLocales = () => {
    const newSelectLocales: Locale[] = []
    changeLocales(newSelectLocales)
  }

  /**
   * Update the component options after click add locales, locale aliases or remove locales
   *
   * @param selectedLocales - The new locales selected
   */
  const changeLocales = (selectedLocales: Locale[]) => {
    const newLocaleOptions = lodash.differenceBy(locales, selectedLocales, 'mapping')
    setLocaleOptions(newLocaleOptions)
    setLocaleOptionsChecked([])
    setAliasChecked([])
    setLocaleSelectedChecked([])
    setLocaleSelected(selectedLocales)
    if (onChange) {
      onChange(selectedLocales.map(locale => locale.mapping))
    }
  }

  /**
   * Create the locale selected list
   *
   * @param items - The locale array selected
   * @returns The element of the list
   */
  const localeSelectedList = (items: Locale[] = []) => (
    <Paper sx={{ width: 300, height: 400, overflow: 'auto' }}>
      <List dense component='div' role='list'>
        {items.map((option: Locale) => {
          return (
            <ListItem key={option.mapping}>
              <ListItemButton
                role='listitem'
                disabled={!option.isVisible}
                onClick={handleCheckedSelectedLocales(option)}
                sx={itemSx}>
                <ListItemIcon>
                  <Checkbox
                    checked={
                      lodash.findIndex(localeSelectedChecked, ['mapping', option.mapping]) !== -1
                    }
                    tabIndex={-1}
                    size='small'
                    disableRipple
                  />
                </ListItemIcon>
                <ListItemText primary={option.label} />
              </ListItemButton>
            </ListItem>
          )
        })}
      </List>
    </Paper>
  )

  /**
   * Create locales or locale aliases option
   *
   * @param option - The option in locale or locale alias
   * @param optionType - The option flag Locale or Locale alias
   * @returns - The option shown in the list
   */
  const createSelectOption = (option: Locale | LocaleAlias, optionType: OptionType) => {
    let keyValue: string
    let label: string
    let handleChecked: Function
    let checked: boolean
    if (optionType === OptionType.Locale) {
      const locale = option as Locale
      keyValue = locale.mapping
      label = locale.label
      handleChecked = handleCheckedLocales
      checked = lodash.findIndex(localeOptionsChecked, ['mapping', keyValue]) !== -1
    } else {
      const localeAlias = option as LocaleAlias
      keyValue = localeAlias.aliasId
      label = localeAlias.aliasId
      handleChecked = handleCheckedLocaleAliases
      checked = lodash.findIndex(aliasChecked, ['aliasId', keyValue]) !== -1
    }
    return (
      <ListItem>
        <ListItemButton key={keyValue} role='listitem' onClick={handleChecked(option)} sx={itemSx}>
          <ListItemIcon>
            <Checkbox checked={checked} tabIndex={-1} size='small' disableRipple />
          </ListItemIcon>
          <ListItemText primary={label} />
        </ListItemButton>
      </ListItem>
    )
  }

  /**
   * Create the buttons group
   *
   * @returns The button group elements
   */
  const createButtonsGroup = () => (
    <Grid
      item
      container
      direction='column'
      xs={3}
      sx={{
        justifyContent: 'space-around',
        alignItems: 'center',
        height: 400,
      }}>
      <Grid item>
        <Button
          variant='outlined'
          size='small'
          startIcon={<DoubleArrowLeft />}
          sx={operationButtonSx}
          disabled={optionsType === OptionType.Alias}
          onClick={addAllLocales}>
          Add All
        </Button>
      </Grid>
      <Grid item>
        <Button
          variant='outlined'
          size='small'
          startIcon={<ArrowLeft />}
          sx={operationButtonSx}
          disabled={lodash.isEmpty(localeOptionsChecked) && lodash.isEmpty(aliasChecked)}
          onClick={optionsType === OptionType.Locale ? addLocales : addLocaleAliases}>
          Add
        </Button>
      </Grid>
      <Grid item>
        <Button
          variant='outlined'
          size='small'
          startIcon={<ArrowRight />}
          sx={operationButtonSx}
          disabled={lodash.isEmpty(localeSelectedChecked)}
          onClick={removeLocales}>
          Delete
        </Button>
      </Grid>
      <Grid item>
        <Button
          variant='outlined'
          size='small'
          startIcon={<DoubleArrowRight />}
          sx={operationButtonSx}
          disabled={lodash.isEmpty(localeSelected)}
          onClick={removeAllLocales}>
          Delete All
        </Button>
      </Grid>
    </Grid>
  )

  /**
   * Create the option list for all locales or locale aliases
   *
   * @param aliases - The locale aliases to be selected
   * @param locales - The locales to be selected
   * @param flag - The option type
   * @returns The element of the list
   */
  const localesAndAliasesList = (aliases: LocaleAlias[], locales: Locale[], flag: OptionType) => (
    <>
      <Tabs value={flag} onChange={(_, value: OptionType) => setOptionsType(value)}>
        <Tab label='Aliases' value={OptionType.Alias} sx={{ fontSize: 12 }} />
        <Tab label='Locales' value={OptionType.Locale} sx={{ fontSize: 12 }} />
      </Tabs>
      <Paper sx={{ width: 300, height: 400, overflow: 'auto' }}>
        {flag === OptionType.Alias ? (
          <List dense component='div' role='list'>
            {aliases.map((alias: LocaleAlias) => createSelectOption(alias, flag))}
          </List>
        ) : (
          <List dense component='div' role='list'>
            {locales.map((locale: Locale) => createSelectOption(locale, flag))}
          </List>
        )}
      </Paper>
    </>
  )

  return (
    <Grid
      container
      justifyContent='center'
      alignItems='flex-end'
      sx={{
        '&.MuiListItemButton-root div': {
          p: 0,
          m: 0,
        },
        marginTop: -7,
      }}>
      <Grid item>{localeSelectedList(localeSelected)}</Grid>
      {createButtonsGroup()}
      <Grid item alignItems='left'>
        {localesAndAliasesList(localeAliases, localeOptions, optionsType)}
      </Grid>
    </Grid>
  )
}

export default LocaleSelector
