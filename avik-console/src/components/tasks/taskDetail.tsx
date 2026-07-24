import * as React from 'react'
import { find } from 'lodash'
import { SxProps } from '@mui/system'
import TextField from '@mui/material/TextField'
import Box from '@mui/material/Box'
import Chip from '@mui/material/Chip'
import IconButton from '@mui/material/IconButton'
import List from '@mui/material/List'
import MenuItem from '@mui/material/MenuItem'
import Switch from '@mui/material/Switch'
import Tooltip from '@mui/material/Tooltip'
import Typography from '@mui/material/Typography'
import { AddCircle } from '@mui/icons-material'

import AppItem from './taskItems/appItem'
import ReviewTask from '../../model/ReviewTask'
import { TaskType } from '../../model/Task'
import {
  getUpdatedApps,
  getUpdatedLocales,
  addNewCustomApp,
  setTaskToUpdate,
  updateLocaleStatus,
  updateTask,
  loadRoundsInTask,
} from '../../store/reducers/TaskUpdateSlice'
import { useAppDispatch, useAppSelector } from '../../store/hooks'
import { listAtms } from '../../store/reducers/AtmSlice'

/**
 * The task detail panel properties
 */
export type TaskProps = {
  task: ReviewTask
}

/**
 * The component to show the review task details
 *
 * @param props - The properties of review task detail
 * @returns The review task detail element
 */
const TaskDetail = (props: TaskProps): JSX.Element => {
  const { task } = props
  const { label, stringLink, atm, type } = task
  const marginStyles: SxProps = { m: 0.5 }
  const atms = useAppSelector(listAtms)
  const updatedLocales = useAppSelector(getUpdatedLocales)
  const updatedApps = useAppSelector(getUpdatedApps)
  const dispatch = useAppDispatch()

  React.useEffect(() => {
    dispatch(setTaskToUpdate(task))
    dispatch(loadRoundsInTask(task.id || ''))
  }, [task, dispatch])

  /**
   * The action to change task label
   *
   * @param event - The event to change task label
   */
  const changeLabel = (event: React.ChangeEvent<HTMLInputElement>) => {
    dispatch(updateTask({ label: event.target.value }))
  }

  /**
   * The action to change task ATM
   *
   * @param event - The event to change task ATM
   */
  const changeAtm = (event: React.ChangeEvent<HTMLInputElement>) => {
    dispatch(updateTask({ atm: event.target.value }))
  }

  /**
   * The action to change task locales
   *
   * @param locale - The locale changed
   */
  const changeLocaleStatus = (locale: string) => {
    const showLocale = find(updatedLocales, ['locale', locale])
    dispatch(updateLocaleStatus({ [locale]: !showLocale?.status }))
  }

  /**
   * Handle event to switch the string link
   *
   * @param value The swtich is checked or not
   */
  const handleSwitchStringLink = (_: React.ChangeEvent, value: boolean) => {
    dispatch(updateTask({ stringLink: value }))
  }

  /**
   * Handle event to click add app button
   */
  const handleClickAddAppButton = () => {
    dispatch(addNewCustomApp(''))
  }

  return (
    <Box component='form' sx={{ '& .MuiFormControl-root': marginStyles }}>
      <TextField
        label='Task Label'
        variant='outlined'
        defaultValue={label}
        onChange={changeLabel}
        fullWidth
      />
      <TextField
        label='ATM'
        variant='outlined'
        select
        defaultValue={atm}
        onChange={changeAtm}
        sx={marginStyles}
        fullWidth>
        {atms.map(atmOption => (
          <MenuItem key={atmOption} value={atmOption}>
            {atmOption}
          </MenuItem>
        ))}
      </TextField>
      <Typography sx={marginStyles} variant='overline'>
        Locales
      </Typography>
      <Box sx={marginStyles}>
        <Typography variant='caption' component='div'>
          Tips: Click in the chip to hide/enable the locale, the grayed-out options will be hidden
          for the final user
        </Typography>
        {updatedLocales.map(locale => (
          <Chip
            key={`locale-${locale}`}
            label={locale.locale}
            variant={locale.status ? 'outlined' : 'filled'}
            sx={marginStyles}
            onClick={() => changeLocaleStatus(locale.locale)}
          />
        ))}
      </Box>
      <Typography sx={marginStyles} variant='overline'>
        Apps
        <Tooltip title='Add Custom App'>
          <IconButton onClick={handleClickAddAppButton}>
            <AddCircle />
          </IconButton>
        </Tooltip>
      </Typography>
      <Box sx={marginStyles}>
        <List>
          {updatedApps.map(app => (
            <AppItem key={app.app} name={app.app} status={app.status} />
          ))}
        </List>
      </Box>
      {type === TaskType.REVIEW ? (
        <>
          <Typography sx={marginStyles} variant='overline'>
            String Link Supported
          </Typography>
          <Switch defaultChecked={stringLink} onChange={handleSwitchStringLink} />
        </>
      ) : null}
    </Box>
  )
}

export default TaskDetail
