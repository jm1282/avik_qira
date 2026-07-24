import * as React from 'react'
import Autocomplete from '@mui/material/Autocomplete'
import Chip from '@mui/material/Chip'
import FormControl from '@mui/material/FormControl'
import FormControlLabel from '@mui/material/FormControlLabel'
import IconButton from '@mui/material/IconButton'
import AddIcon from '@mui/icons-material/Add'
import Paper from '@mui/material/Paper'
import Radio from '@mui/material/Radio'
import RadioGroup from '@mui/material/RadioGroup'
import TextField from '@mui/material/TextField'
import Tooltip from '@mui/material/Tooltip'
import Typography from '@mui/material/Typography'

import { listLocales, removeBaseLocale } from '../../store/reducers/LocaleSlice'
import { useAppSelector, useAppDispatch } from '../../store/hooks'
import Locale from '../../model/Locale'
import DataEditorDialog from '../dataEditorDialog'

const validatedLocale = /^[a-zA-Z0-9]+(-[a-zA-Z0-9]+)+$/

enum LocaleSelectType {
  PRESET,
  PSEUDO,
}

type BaseLocalesEditorProps = {
  onSave?: (baseLocale: Locale) => void
}

/**
 * The component to edit the aliases in Avik scope
 *
 * @returns The element to edit the aliases in Avik scope
 */
const BaseLocalesEdit = (props: BaseLocalesEditorProps): JSX.Element => {
  const allLocales = useAppSelector(listLocales)
  const [locales, setLocales] = React.useState<Locale[]>([])
  const [baseLocales, setBaseLocales] = React.useState<Locale[]>([])
  const [presetLocale, setPresetLocale] = React.useState<Locale>()
  const [localeId, setLocaleId] = React.useState<string>('')
  const [displayName, setDisplayName] = React.useState<string>('')
  const [localeSelectedType, setLocaleSelectedType] = React.useState<LocaleSelectType>(
    LocaleSelectType.PRESET
  )
  const [isBaseLocaleValid, setBaseLocaleValid] = React.useState<boolean>(false)
  const [openModal, setOpenModal] = React.useState<boolean>(false)
  const dispatch = useAppDispatch()

  React.useEffect(() => {
    setLocales(allLocales)
    setBaseLocales(allLocales.filter(locale => locale.isBaseLocale))
  }, [allLocales])

  React.useEffect(() => {
    if (localeSelectedType === LocaleSelectType.PSEUDO) {
      setBaseLocaleValid(validatedLocale.test(localeId) && localeId !== '' && displayName !== '')
    } else {
      setBaseLocaleValid(presetLocale !== undefined)
    }
  }, [localeId, displayName, localeSelectedType, presetLocale])

  /**
   * Handler to click save button to create/update a base locale
   */
  const clickSave = () => {
    const baseLocale: Locale = new Locale()
    if (localeSelectedType === LocaleSelectType.PSEUDO) {
      baseLocale.localeId = localeId
      baseLocale.displayName = displayName
      baseLocale.pseudo = true
      baseLocale.mapping = localeId
    } else {
      baseLocale.localeId = presetLocale!!.localeId
      baseLocale.displayName = presetLocale!!.displayName
      baseLocale.pseudo = false
      baseLocale.mapping = presetLocale!!.mapping
    }

    if (props.onSave) {
      props.onSave(baseLocale)
    }
    closeBaseLocaleEdit()
  }

  /**
   * Open base locale edit dialog
   *
   * @param baseLocale - The data of base locale
   */
  const openBaseLocaleEdit = (baseLocale: Locale | null = null) => {
    if (baseLocale) {
      if (!baseLocale.pseudo) {
        setPresetLocale(baseLocale)
      }
      setLocaleId(baseLocale.localeId)
      setDisplayName(baseLocale.displayName)
      setLocaleSelectedType(baseLocale.pseudo ? LocaleSelectType.PSEUDO : LocaleSelectType.PRESET)
    }
    setOpenModal(true)
  }

  /**
   * Cloase base locale edit dialog
   */
  const closeBaseLocaleEdit = () => {
    setPresetLocale(undefined)
    setLocaleId('')
    setDisplayName('')
    setLocaleSelectedType(LocaleSelectType.PRESET)
    setOpenModal(false)
  }

  /**
   * Handle the locales change event
   *
   * @param event - the locales change event
   */
  const handleChangeLocale = (
    event: React.SyntheticEvent<Element, Event>,
    value: Locale | null
  ) => {
    if (value) {
      setLocaleId(value.localeId)
      setDisplayName(value.displayName)
      setPresetLocale(value)
    } else {
      setLocaleId('')
      setDisplayName('')
      setPresetLocale(undefined)
    }
  }

  /**
   * Delete the base locale
   *
   * @param baseLocale - The base locale to be deleted
   */
  const deleteBaseLocale = (baseLocale: Locale) => {
    dispatch(removeBaseLocale(baseLocale))
  }

  /**
   * Handle to change the pseudo property
   *
   * @param event  The event to change the pseudo property
   */
  const handleChangeLocaleSelectedType = (
    event: React.ChangeEvent<HTMLInputElement>,
    value: string
  ) => {
    setLocaleSelectedType(value === '0' ? LocaleSelectType.PRESET : LocaleSelectType.PSEUDO)
  }

  /**
   * Handle to change the locale id property
   *
   * @param event The event to change the locale id property
   */
  const handleChangeLocaleId = (event: React.ChangeEvent<HTMLInputElement>) => {
    setLocaleId(event.target.value)
  }

  /**
   * Handle to change the display name property
   *
   * @param event The event to change the display name property
   */
  const handleChangeDisplayName = (event: React.ChangeEvent<HTMLInputElement>) => {
    setDisplayName(event.target.value)
  }

  return (
    <Paper elevation={0} sx={{ m: 5 }}>
      <Typography variant='caption' component='div'>
        Base Locales
        <Tooltip title='Add Base Locales'>
          <IconButton onClick={() => openBaseLocaleEdit()}>
            <AddIcon />
          </IconButton>
        </Tooltip>
      </Typography>
      {baseLocales.map((baseLocale: Locale) => (
        <Tooltip key={baseLocale.localeId} title={baseLocale.label}>
          <Chip
            label={baseLocale.mapping}
            onDoubleClick={() => openBaseLocaleEdit(baseLocale)}
            onDelete={() => deleteBaseLocale(baseLocale)}
            sx={{ m: 1 }}
          />
        </Tooltip>
      ))}
      <DataEditorDialog
        open={openModal}
        title={'Select Base Locale'}
        disableSave={!isBaseLocaleValid}
        onSave={clickSave}
        onClose={closeBaseLocaleEdit}>
        <Typography variant='caption' component='div'>
          Base Locale
        </Typography>
        <Typography variant='body2' component='div'>
          Select the locale from the preset list or enter the pesudo locale in following text field
        </Typography>
        <FormControl>
          <RadioGroup value={localeSelectedType} onChange={handleChangeLocaleSelectedType} row>
            <FormControlLabel
              value={LocaleSelectType.PRESET}
              control={<Radio />}
              label='Preset Locale'
            />
            <FormControlLabel
              value={LocaleSelectType.PSEUDO}
              control={<Radio />}
              label='Pseudo Locale'
            />
          </RadioGroup>
        </FormControl>
        {localeSelectedType ? (
          <>
            <FormControl fullWidth>
              <TextField label='Locale Tag' value={localeId} onChange={handleChangeLocaleId} />
            </FormControl>
            <FormControl fullWidth>
              <TextField
                label='Display Name'
                value={displayName}
                onChange={handleChangeDisplayName}
              />
            </FormControl>
          </>
        ) : (
          <FormControl fullWidth sx={{ marginTop: 2 }}>
            <Autocomplete
              options={locales}
              value={presetLocale}
              getOptionLabel={option => option.label}
              renderInput={params => <TextField {...params} label='Locales' />}
              onChange={handleChangeLocale}
            />
          </FormControl>
        )}
      </DataEditorDialog>
    </Paper>
  )
}

export default BaseLocalesEdit
