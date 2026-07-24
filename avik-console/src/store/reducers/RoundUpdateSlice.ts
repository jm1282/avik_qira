import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import lodash from 'lodash'

import Round from '../../model/Round'
import { RootState } from '../store'
import { validateObject, checkObjectUpdatedAndValid } from '../../utils'
import Task, { TaskType } from '../../model/Task'
import { allRoundsLabels } from './RoundSlice'

export const roundLabelReg = /^[\da-zA-Z_]+$/
export const taskLabelReg = /^[\da-zA-Z_\-/().\u0020]+$/
export const appNameReg = /^[\da-zA-Z_\-/().\u0020]+$/
export const projectReg = /^[\da-zA-Z_]+$/

/**
 * Define the round state for updating
 */
export type RoundUpdateState = {
  oldRound?: Round
  newRound: Partial<Round>
  updatedTasks: Partial<Task>[]
  roundTask: Partial<Task>
}

/**
 * Initial value of the round state
 */
const initReviewTaskState: RoundUpdateState = {
  newRound: {},
  updatedTasks: [],
  roundTask: { type: TaskType.REVIEW },
}

/**
 * The slice for updating round
 */
export const roundUpdatingSlice = createSlice({
  name: 'roundUpdate',
  initialState: initReviewTaskState,
  reducers: {
    setRoundToUpdate: (_: RoundUpdateState, action: PayloadAction<Round | undefined>) => {
      const oldRound = action.payload
      if (oldRound) {
        return { ...initReviewTaskState, oldRound, newRound: { id: oldRound.id } }
      }
      return { ...initReviewTaskState, oldRound }
    },
    updateRound: (state: RoundUpdateState, action: PayloadAction<any>) => {
      const oldRound = state.oldRound
      let newRound = state.newRound
      const updateData = action.payload
      if (!oldRound) {
        newRound = { ...newRound, ...updateData }
      } else {
        for (const propertyName in updateData) {
          const oldProperty = lodash.get(oldRound, propertyName)
          if (lodash.isEqual(oldProperty, updateData[propertyName])) {
            newRound = lodash.omit(newRound, propertyName)
          } else {
            newRound = { ...newRound, ...updateData }
          }
        }
      }
      return { ...state, newRound }
    },
    updateTask: (state: RoundUpdateState, action: PayloadAction<any>) => {
      const updatedTask = action.payload
      const tasks = state.updatedTasks
      const updatedTasks = lodash.find(tasks, ['label', updatedTask.label])
        ? tasks.filter(task => task.label !== updatedTask.label)
        : [...tasks, updatedTask]
      return { ...state, updatedTasks }
    },
    initRoundTask: (state: RoundUpdateState) => {
      return { ...state, roundTask: { type: TaskType.REVIEW } }
    },
    updateTaskData: (state: RoundUpdateState, action: PayloadAction<any>) => {
      const roundTask = lodash.clone(state.roundTask)
      Object.assign(roundTask, action.payload)
      return { ...state, roundTask }
    },
  },
})

/**
 * Check the round is updated and valid or not
 *
 * @param state - The root state
 * @returns The round is updated and valid or not
 */
const isRoundUpdatedValid = (state: RootState): boolean => {
  const roundUpdate = state.roundUpdate.newRound
  const tasks = state.roundUpdate.updatedTasks
  const isRoundUpdatedAndValid = checkObjectUpdatedAndValid(roundUpdate, 2, [
    { propertyName: 'buildLabel', options: { reg: roundLabelReg } },
  ])
  return isRoundUpdatedAndValid || !lodash.isEmpty(tasks)
}

/**
 * Check the round is valid to be created or not
 *
 * @param state - The root state
 * @returns The round is valid or not be created
 */
const isRoundCreatedValid = (state: RootState): boolean => {
  const allBuildLabels = allRoundsLabels(state)
  const roundUpdate = state.roundUpdate.newRound
  const tasks = state.roundUpdate.updatedTasks
  return (
    validateObject(roundUpdate, [
      { propertyName: 'buildLabel', options: { reg: roundLabelReg } },
      { propertyName: 'baseLocale' },
      { propertyName: 'locales' },
    ]) &&
    !allBuildLabels.includes(roundUpdate.buildLabel || '') &&
    !lodash.isEmpty(tasks)
  )
}

/**
 * Check the task in round is valid or not to be created
 *
 * @param state - The root state
 * @returns The task in round is valid or not to be created
 */
export const isRoundTaskValid = (state: RootState): boolean => {
  const roundTask = state.roundUpdate.roundTask
  const oldTasks = getRoundTasks(state)
  return (
    validateObject(roundTask, [
      { propertyName: 'label', options: { reg: taskLabelReg } },
      { propertyName: 'atm' },
      { propertyName: 'type' },
      { propertyName: 'projects', options: { reg: projectReg } },
    ]) && !lodash.find(oldTasks, ['label', roundTask.label])
  )
}

/**
 * The round to be created or updated can be saved or not
 *
 * @param state The root state
 * @returns The round can be saved or not
 */
export const canSaveRound = (state: RootState): boolean => {
  return state.roundUpdate.oldRound ? isRoundUpdatedValid(state) : isRoundCreatedValid(state)
}

/**
 * Get the updated round
 *
 * @param state The root state
 * @returns The round to be updated
 */
export const getUpdatedRound = (state: RootState): any => {
  const tasks = state.roundUpdate.updatedTasks
  const newRound = state.roundUpdate.newRound
  if (!lodash.isEmpty(tasks)) {
    return { ...newRound, tasks: getRoundTasks(state) }
  }
  return newRound
}

/**
 * Get the current task in round that is being edited
 *
 * @param state The root state
 * @returns The current task in round that is being edited
 */
export const getUpdatedRoundTask = (state: RootState): any => {
  return state.roundUpdate.roundTask
}

/**
 * Get all tasks in the round
 *
 * @param state The root state
 * @returns All tasks in the round
 */
export const getRoundTasks = (state: RootState): Task[] => {
  const tasks = lodash.clone(state.roundUpdate.oldRound?.tasks) || []
  const updatedTasks = lodash.clone(state.roundUpdate.updatedTasks)
  for (const task of updatedTasks) {
    if (lodash.find(tasks, ['label', task.label])) {
      lodash.remove(tasks, ['label', task.label])
    } else {
      tasks.push(task)
    }
  }
  return lodash.sortBy(tasks, 'label')
}

export const { setRoundToUpdate, updateRound, updateTask, initRoundTask, updateTaskData } =
  roundUpdatingSlice.actions

export default roundUpdatingSlice.reducer
