import React, { useEffect } from 'react'
import AddCircle from '@mui/icons-material/AddCircle'
import Autocomplete from '@mui/material/Autocomplete'
import Box from '@mui/material/Box'
import Chip from '@mui/material/Chip'
import FormControl from '@mui/material/FormControl'
import FormHelperText from '@mui/material/FormHelperText'
import InputLabel from '@mui/material/InputLabel'
import InputAdornment from '@mui/material/InputAdornment'
import MenuItem from '@mui/material/MenuItem'
import Select from '@mui/material/Select'
import TextField from '@mui/material/TextField'
import Tooltip from '@mui/material/Tooltip'

import { allRoundsLabels } from '../../store/reducers/RoundSlice'
import { listAliases, listLocales, listBaseLocales } from '../../store/reducers/LocaleSlice'
import { useAppDispatch, useAppSelector } from '../../store/hooks'
import { chipGroupStyles } from '../../styles/componentStyles'
import LocaleSelector from '../localeSelector'
import Locale from '../../model/Locale'
import Task from '../../model/Task'
import Round from '../../model/Round'
import TaskItemDialog from './roundItems/taskItemDialog'
import {
  getRoundTasks,
  setRoundToUpdate,
  updateRound,
  updateTask,
} from '../../store/reducers/RoundUpdateSlice'

/**
 * The component properties
 */
type RoundProps = {
  round?: Round
}

/**
 * The component to show the round details
 *
 * @param props - The round details properties
 * @returns The round detail element
 */
const RoundDetail = (props: RoundProps): JSX.Element => {
  const { round } = props
  const [showTaskDetail, setShowTaskDetail] = React.useState<boolean>(false)
  const allLocales = useAppSelector(listLocales)
  const allAliases = useAppSelector(state => listAliases(state, true))
  const allBaseLocales = useAppSelector(listBaseLocales)
  const allBuildLabels = useAppSelector(allRoundsLabels)
  const showTasks = useAppSelector(getRoundTasks)
  const dispatch = useAppDispatch()

  useEffect(() => {
    dispatch(setRoundToUpdate(round))
  }, [dispatch, round])

  /**
   * The action to change round locales
   *
   * @param locales - The event to change round locales
   */
  const changeLocales = (locales: string[]) => {
    dispatch(updateRound({ locales: locales }))
  }

  /**
   * The action to change round build label
   *
   * @param event - The event to change round build label
   */
  const changeBuildLabel = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
    dispatch(updateRound({ buildLabel: event.target.value }))
  }

  /**
   * The action to change round base locale
   *
   * @param event - The event to change round base locale
   */
  const changeBaseLocale = (event: React.ChangeEvent<HTMLInputElement>) => {
    dispatch(updateRound({ baseLocale: event.target.value }))
  }

  /**
   * The action to change round reference locale
   *
   * @param event - The event to change round reference locale
   */
  const changeReferenceLocale = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.value === '') {
      dispatch(updateRound({ referenceLocale: undefined }))
    } else {
      dispatch(updateRound({ referenceLocale: event.target.value }))
    }
  }

  /**
   * The action to change round reference rounds
   *
   * @param event - The reference round change event
   * @param roundLabels - The new reference rounds labels
   */
  const changeReferenceRounds = (_: React.SyntheticEvent, roundLabels: string[]) => {
    dispatch(updateRound({ referenceRounds: roundLabels }))
  }

  /**
   * Action to remove the task
   *
   * @param label - The task label to be removed
   */
  const removeTask = (label: string) => {
    const task = new Task()
    task.label = label
    dispatch(updateTask(task))
  }

  return (
    <Box
      component='form'
      sx={{
        '& .MuiFormControl-root': { m: 1 },
        minHeight: 500,
      }}>
      <TextField
        id='build_label'
        label='Build Label'
        variant='outlined'
        defaultValue={round?.buildLabel}
        onChange={changeBuildLabel}
        fullWidth
      />
      <FormHelperText>
        The build label ONLY can be composed of letters(a-zA-Z), digits(0-9) and underscores(_)
      </FormHelperText>
      <TextField
        label='Base Locale'
        variant='outlined'
        select
        fullWidth
        defaultValue={round?.baseLocale}
        onChange={changeBaseLocale}>
        {allBaseLocales.map((baseLocale: Locale) => (
          <MenuItem key={baseLocale.mapping} value={baseLocale.mapping}>
            {baseLocale.label}
          </MenuItem>
        ))}
      </TextField>
      <TextField
        label='Reference Locale'
        variant='outlined'
        select
        fullWidth
        defaultValue={round?.referenceLocale}
        onChange={changeReferenceLocale}>
        <MenuItem value=''>No Reference Locale</MenuItem>
        {/* The reference locales are selected from the base locales */}
        {allBaseLocales.map((baseLocale: Locale) => (
          <MenuItem key={baseLocale.mapping} value={baseLocale.mapping}>
            {baseLocale.label}
          </MenuItem>
        ))}
      </TextField>
      <FormControl fullWidth sx={{ minHeight: '40px' }}>
        <InputLabel>Locales</InputLabel>
      </FormControl>
      <FormControl fullWidth>
        <LocaleSelector
          defaultLocales={round?.locales}
          locales={allLocales}
          localeAliases={allAliases}
          onChange={changeLocales}
        />
      </FormControl>
      <FormControl fullWidth>
        <InputLabel>Tasks</InputLabel>
        <Select
          label='Tasks'
          value={showTasks}
          variant='outlined'
          multiple
          fullWidth
          renderValue={tasks => (
            <Box sx={chipGroupStyles}>
              {tasks.map(task => (
                <Chip
                  key={task.label}
                  label={task.label}
                  onDelete={() => removeTask(task.label || '')}
                />
              ))}
            </Box>
          )}
          open={false}
          onOpen={event => event.preventDefault()}
          IconComponent={() => null}
          endAdornment={
            <InputAdornment
              position='end'
              sx={{ cursor: 'pointer' }}
              onClick={() => setShowTaskDetail(true)}>
              <Tooltip title='Add New Task'>
                <AddCircle />
              </Tooltip>
            </InputAdornment>
          }
        />
        <FormHelperText>
          Tips: Select a task at least or create a new Task by click the right button
        </FormHelperText>
      </FormControl>
      <Autocomplete
        fullWidth
        multiple
        options={allBuildLabels}
        defaultValue={round?.referenceRounds}
        renderInput={params => (
          <TextField {...params} variant='outlined' label='Reference Rounds' />
        )}
        onChange={changeReferenceRounds}
      />
      <TaskItemDialog open={showTaskDetail} onClose={() => setShowTaskDetail(false)} />
    </Box>
  )
}

export default RoundDetail
