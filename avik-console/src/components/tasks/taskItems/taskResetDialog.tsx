import React, { useEffect, useState } from 'react'
import {
  Checkbox,
  Chip,
  FormControl,
  InputLabel,
  ListItemText,
  MenuItem,
  OutlinedInput,
  Select,
  SelectChangeEvent,
} from '@mui/material'
import { clone, isEmpty, size } from 'lodash'

import { waitForReply } from '../../../utils'
import { resetTrainingTask } from '../../../api/taskAPI'
import { useAppDispatch } from '../../../store/hooks'
import ReviewTask from '../../../model/ReviewTask'
import DataEditorDialog from '../../dataEditorDialog'

/**
 * The task reset dialog properties
 */
type TaskResetDialogProps = {
  open: boolean
  task: ReviewTask
  onClose?: () => void
}

const ALL_LOCALES = 'ALL'

/**
 * The dialog component to reset training task
 *
 * @param props The component properties
 * @returns The component
 */
const TaskResetDialog = (props: TaskResetDialogProps): JSX.Element => {
  const { open, task, onClose } = props
  const locales = task.locales
  const [selectedLocales, setSelectedLocales] = useState<string[]>(locales || [])
  const [selectAll, setSelectAll] = useState<boolean>()
  const [selectSomeLocales, setSelectSomeLocales] = useState<boolean>()
  const dispatch = useAppDispatch()

  useEffect(() => {
    setSelectAll(!isEmpty(locales) && size(selectedLocales) === size(locales))
    setSelectSomeLocales(!isEmpty(selectedLocales) && size(selectedLocales) < size(locales))
  }, [locales, selectedLocales])

  /**
   * Handle the locales selected change
   *
   * @param event The event to change locales
   */
  const handleLocalesChange = (event: SelectChangeEvent<string[]>) => {
    const selected = event.target.value as string[]
    if (selected.includes(ALL_LOCALES)) {
      setSelectedLocales(size(selectedLocales) === size(locales) ? [] : locales || [])
    } else {
      setSelectedLocales(selected)
    }
  }

  /**
   * Handle confirm reset the task
   */
  const handleConfirmReset = () => {
    const confirmLocales = clone(selectedLocales.filter(selected => selected !== ALL_LOCALES))
    if (!isEmpty(confirmLocales)) {
      waitForReply(dispatch, () => resetTrainingTask(task, confirmLocales), {
        successMessage: `Success to reset task "${task.label}" review status`,
      })
    }
    handleCloseDialog()
  }

  /**
   * Handle close the dialog
   */
  const handleCloseDialog = () => {
    setSelectedLocales(locales || [])
    setSelectAll(true)
    setSelectSomeLocales(false)
    onClose && onClose()
  }

  return (
    <DataEditorDialog
      open={open}
      title='Reset the training task'
      disableSave={isEmpty(selectedLocales)}
      saveText='Reset'
      onSave={handleConfirmReset}
      onClose={handleCloseDialog}>
      <FormControl fullWidth>
        <InputLabel>Select Locales</InputLabel>
        <Select
          multiple
          input={<OutlinedInput label='Select Locales' />}
          value={selectedLocales}
          onChange={handleLocalesChange}
          renderValue={selected => selected.map(locale => <Chip key={locale} label={locale} />)}>
          <MenuItem value={ALL_LOCALES}>
            <Checkbox checked={selectAll} indeterminate={selectSomeLocales} />
            <ListItemText primary='All Locales' />
          </MenuItem>
          {locales?.map(locale => (
            <MenuItem key={locale} value={locale}>
              <Checkbox checked={selectedLocales.includes(locale)} />
              <ListItemText primary={locale} />
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </DataEditorDialog>
  )
}

export default TaskResetDialog
