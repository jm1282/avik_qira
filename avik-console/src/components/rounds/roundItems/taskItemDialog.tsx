import React, { ChangeEvent, useState } from 'react'
import {
  Autocomplete,
  AutocompleteInputChangeReason,
  FormHelperText,
  MenuItem,
  TextField,
} from '@mui/material'
import { isEmpty } from 'lodash'

import { useAppDispatch, useAppSelector } from '../../../store/hooks'
import DataEditorDialog from '../../dataEditorDialog'
import { TaskType } from '../../../model/Task'
import { listAtms } from '../../../store/reducers/AtmSlice'
import { listProjects } from '../../../store/reducers/RoundSlice'
import {
  updateTaskData,
  initRoundTask,
  isRoundTaskValid,
  updateTask,
  getUpdatedRoundTask,
} from '../../../store/reducers/RoundUpdateSlice'

/**
 * The task item component properties
 */
type TaskItemDialogProps = {
  open: boolean
  onClose?: () => void
}

/**
 * The dialog component to show one task details
 *
 * @param props The component properties
 * @returns The component
 */
const TaskItemDialog = (props: TaskItemDialogProps): JSX.Element => {
  const { open, onClose } = props
  const [tempProject, setTempProject] = useState<string>('')
  const roundTask = useAppSelector(getUpdatedRoundTask)
  const isTaskValid = useAppSelector(isRoundTaskValid)
  const allAtms = useAppSelector(listAtms)
  const allProjects = useAppSelector(listProjects)
  const dispatch = useAppDispatch()

  /**
   * The action to save task
   */
  const saveTask = () => {
    const project = tempProject.trim()
    if (project !== '') {
      addProject(project)
    }
    dispatch(updateTask(roundTask))
    handleCloseDialog()
  }

  /**
   * The action to add a project
   *
   * @param project The project to be added
   */
  const addProject = (project: string) => {
    const projects = roundTask.projects || []
    projects.push(project)
    dispatch(updateTaskData({ projects: projects }))
  }

  /**
   * Handle event to change label
   *
   * @param event The change label event
   */
  const handleChangeLabel = (event: ChangeEvent<HTMLInputElement>) => {
    dispatch(updateTaskData({ label: event.target.value }))
  }

  /**
   * Handle event to change task type
   *
   * @param event The change task type
   */
  const handleChangeType = (event: ChangeEvent<HTMLInputElement>) => {
    dispatch(updateTaskData({ type: Number(event.target.value) }))
  }

  /**
   * Handle event to change atm
   *
   * @param event The change atm
   */
  const handleChangeAtm = (event: ChangeEvent<HTMLInputElement>) => {
    dispatch(updateTaskData({ atm: event.target.value }))
  }

  /**
   * Handle event to change projects selector
   *
   * @param value The new value of projects
   */
  const handleChangeProjects = (_: React.SyntheticEvent<Element, Event>, value: string[]) => {
    dispatch(updateTaskData({ projects: value }))
  }

  /**
   * Handle event to input arbitrary project
   *
   * @param value The arbitrary project
   * @param reason The action reason
   */
  const handleChangeProjectsByInput = (
    _: React.SyntheticEvent<Element, Event>,
    value: string,
    reason: AutocompleteInputChangeReason
  ) => {
    if (reason === 'input') {
      if (value.endsWith(',')) {
        const project = value.substring(0, value.length - 1).trim()
        if (!isEmpty(project)) {
          addProject(project)
        }
        setTempProject('')
        return
      }
    }
    setTempProject(value)
  }

  /**
   * Handle the event to close dialog
   */
  const handleCloseDialog = () => {
    dispatch(initRoundTask())
    onClose && onClose()
  }

  return (
    <DataEditorDialog
      open={open}
      title='Create new task'
      disableSave={!isTaskValid}
      onSave={saveTask}
      onClose={handleCloseDialog}>
      <TextField label='Task Label' variant='outlined' onChange={handleChangeLabel} fullWidth />
      <FormHelperText>
        The task label ONLY can be composed of letters(a-zA-Z), digits(0-9), characters (+-_.\(|)/)
        and space
      </FormHelperText>
      <TextField
        label='Task Type'
        select
        variant='outlined'
        defaultValue={TaskType.REVIEW}
        onChange={handleChangeType}
        fullWidth>
        <MenuItem value={TaskType.REVIEW}>Review Task</MenuItem>
        <MenuItem value={TaskType.CXD}>CXD Task</MenuItem>
        <MenuItem value={TaskType.TRAINING}>Training Task</MenuItem>
      </TextField>
      <TextField label='ATM' select variant='outlined' onChange={handleChangeAtm} fullWidth>
        {allAtms.map((atm: string) => (
          <MenuItem key={atm} value={atm}>
            {atm}
          </MenuItem>
        ))}
      </TextField>
      <Autocomplete
        multiple
        freeSolo
        fullWidth
        options={allProjects}
        inputValue={tempProject}
        getOptionLabel={option => option}
        filterSelectedOptions
        onChange={handleChangeProjects}
        onInputChange={handleChangeProjectsByInput}
        renderInput={params => <TextField {...params} label='Projects' />}
      />
      <FormHelperText>
        The project ONLY can be composed of letters(a-zA-Z), digits(0-9) and underscores(_). Please
        press &quot;Enter&quot; after inputing arbitrary value.
      </FormHelperText>
    </DataEditorDialog>
  )
}

export default TaskItemDialog
