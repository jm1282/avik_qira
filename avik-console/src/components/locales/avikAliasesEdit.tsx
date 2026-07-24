import * as React from 'react'
import Box from '@mui/material/Box'
import Chip from '@mui/material/Chip'
import FormControl from '@mui/material/FormControl'
import MenuItem from '@mui/material/MenuItem'
import Paper from '@mui/material/Paper'
import Select, { SelectChangeEvent } from '@mui/material/Select'
import Typography from '@mui/material/Typography'

import { listAliases } from '../../store/reducers/LocaleSlice'
import { useAppSelector } from '../../store/hooks'
import { chipGroupStyles, selectedItem } from '../../styles/componentStyles'

type AvikAliasesEditProps = {
  onSave?: (aliases: string[]) => void
}

/**
 * The component to edit the aliases in Avik scope
 *
 * @returns The element to edit the aliases in Avik scope
 */
const AvikAliasesEdit = (props: AvikAliasesEditProps): JSX.Element => {
  const [avikAliases, setAvikAliases] = React.useState<string[]>([])
  const allAliases = useAppSelector(listAliases)

  React.useEffect(() => {
    const aliasIds = allAliases.filter(alias => alias.isVisible).map(alias => alias.aliasId)
    setAvikAliases(aliasIds)
  }, [allAliases])

  /**
   * Handle the aliases change event
   *
   * @param event - the aliases change event
   */
  const handleChangeAlias = (event: SelectChangeEvent<string[]>) => {
    const selectedAliases = event.target.value as string[]
    setAvikAliases(selectedAliases)
    if (props.onSave) {
      props.onSave(selectedAliases)
    }
  }

  return (
    <Paper elevation={0} sx={{ m: 5 }}>
      <Typography variant='caption' component='div'>
        Aliases in Avik Scope
      </Typography>
      <FormControl fullWidth component='div'>
        <Select
          multiple
          value={avikAliases}
          onChange={handleChangeAlias}
          renderValue={selected => (
            <Box sx={chipGroupStyles}>
              {selected.map(value => (
                <Chip key={value} label={value} />
              ))}
            </Box>
          )}>
          {allAliases.map(alias => (
            <MenuItem key={alias.aliasId} value={alias.aliasId} sx={selectedItem}>
              {alias.aliasId} - {alias.description}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </Paper>
  )
}

export default AvikAliasesEdit
