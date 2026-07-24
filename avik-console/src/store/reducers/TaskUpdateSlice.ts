import {
  ActionReducerMapBuilder,
  createAsyncThunk,
  createSlice,
  PayloadAction,
} from '@reduxjs/toolkit'
import lodash from 'lodash'

import Round from '../../model/Round'
import ReviewTask from '../../model/ReviewTask'
import { RootState } from '../store'
import { checkObjectUpdatedAndValid, mergeUpdateResults, validateProperty } from '../../utils'
import { getRoundsInTask } from '../../api/taskAPI'
import { appNameReg, taskLabelReg } from './RoundUpdateSlice'

const CUSTOM_APP = '/CUSTOM_APP'

/**
 * Define the review task state
 */
export type TaskUpdateState = {
  oldTask?: ReviewTask
  newTask: any
  appsRename: { [app: string]: string }
  screensFiles: { roundId: string; app: string; file: File }[]
  rounds: Round[]
}

/**
 * Initial value of the review task state
 */
const initReviewTaskState: TaskUpdateState = {
  newTask: {},
  appsRename: {},
  screensFiles: [],
  rounds: [],
}

/**
 * Load all rounds in the task
 */
export const loadRoundsInTask = createAsyncThunk(
  'task/getAllRoundsInTask',
  async (taskId: string): Promise<Round[]> => await getRoundsInTask(taskId)
)

/**
 * Update a task state
 *
 * @param state The updated state of the task
 * @param updateData The updat data to be set
 * @returns The task updated state
 */
const updateNewTask = (state: TaskUpdateState, updateData: any) => {
  let newTask = lodash.clone(state.newTask)
  for (const propertyName in updateData) {
    const oldPrperty = lodash.get(state.oldTask, propertyName)
    if (lodash.isEqual(updateData[propertyName], oldPrperty)) {
      // Remove the recovered property
      newTask = lodash.omit(newTask, propertyName)
    } else {
      // Update property
      Object.assign(newTask, { [propertyName]: updateData[propertyName] })
    }
  }
  return { ...state, newTask }
}

/**
 * The slice for updating task
 */
export const taskUpdatingSlice = createSlice({
  name: 'taskUpdate',
  initialState: initReviewTaskState,
  reducers: {
    setTaskToUpdate: (_: TaskUpdateState, action: PayloadAction<ReviewTask>) => {
      const newTask = { id: action.payload.id }
      return { ...initReviewTaskState, newTask, oldTask: action.payload }
    },
    updateTask: (state: TaskUpdateState, action: PayloadAction<any>) => {
      return updateNewTask(state, action.payload)
    },
    updateAppStatus: (
      state: TaskUpdateState,
      action: PayloadAction<{ [app: string]: boolean }>
    ) => {
      const oldTask = state.oldTask
      const newTask = state.newTask
      const appsRename = lodash.clone(state.appsRename)
      let apps = lodash.clone(newTask?.apps || oldTask?.apps)
      let hiddenApps = lodash.clone(newTask?.hiddenApps || oldTask?.hiddenApps)
      for (const app in action.payload) {
        if (action.payload[app]) {
          apps = [...apps, app].sort()
          hiddenApps = hiddenApps.filter((a: string) => a !== app)
        } else {
          apps = apps.filter((a: string) => a !== app)
          if (!app.startsWith(CUSTOM_APP)) {
            hiddenApps = [...hiddenApps, app].sort()
          } else {
            delete appsRename[app]
          }
        }
      }
      return { ...updateNewTask(state, { apps, hiddenApps }), appsRename }
    },
    updateLocaleStatus: (
      state: TaskUpdateState,
      action: PayloadAction<{ [locale: string]: boolean }>
    ) => {
      const oldTask = state.oldTask
      const newTask = state.newTask
      let locales = lodash.clone(newTask?.locales || oldTask?.locales)
      let hiddenLocales = lodash.clone(newTask?.hiddenLocales || oldTask?.hiddenLocales)
      for (const locale in action.payload) {
        if (action.payload[locale]) {
          locales = [...locales, locale].sort()
          hiddenLocales = hiddenLocales.filter((l: string) => l !== locale)
        } else {
          locales = locales.filter((l: string) => l !== locale)
          hiddenLocales = [...hiddenLocales, locale].sort()
        }
      }
      return updateNewTask(state, { locales, hiddenLocales })
    },
    renameAppInTask: (state: TaskUpdateState, action: PayloadAction<{ [app: string]: string }>) => {
      const newAppNames = { ...state.appsRename, ...action.payload }
      Object.keys(newAppNames).forEach((key: string) => {
        if (newAppNames[key] === key) {
          delete newAppNames[key]
        }
      })
      return {
        ...state,
        appsRename: newAppNames,
      }
    },
    addNewCustomApp: (state: TaskUpdateState, _: PayloadAction<string>) => {
      const apps = lodash.clone(state.newTask.apps || state.oldTask?.apps || [])
      const randNumber = new Date().getTime()
      const customNameFlag = `${CUSTOM_APP}_${randNumber}`
      return {
        ...state,
        newTask: {
          ...state.newTask,
          apps: [...apps, customNameFlag].sort(),
        },
        appsRename: { ...state.appsRename, [customNameFlag]: '' },
      }
    },
    uploadFile: (
      state: TaskUpdateState,
      action: PayloadAction<{ roundId: string; app: string; file: File }>
    ) => {
      return {
        ...state,
        screensFiles: lodash.concat(state.screensFiles, action.payload),
      }
    },
  },
  extraReducers: (builder: ActionReducerMapBuilder<TaskUpdateState>) => {
    builder.addCase(
      loadRoundsInTask.fulfilled,
      (state: TaskUpdateState, action: PayloadAction<Round[]>) => {
        return { ...state, rounds: action.payload }
      }
    )
  },
})

/**
 * Get fields of a task to be updated
 *
 * @param state The root state
 * @returns The task fields to be updated
 */
export const getUpdateTask = (state: RootState): any => {
  const appsRename = state.taskUpdate.appsRename
  const updateTask = lodash.clone(state.taskUpdate.newTask)

  const updateApps = updateTask?.apps?.map((app: string) =>
    app.startsWith(CUSTOM_APP) ? appsRename[app] : app
  )
  if (!lodash.isEmpty(updateApps)) {
    Object.assign(updateTask, { apps: updateApps })
  }

  return updateTask
}

/**
 * Get updated apps show/hide state in the task
 *
 * @param state The root state
 * @returns The updated apps state in the task
 */
export const getUpdatedApps = (state: RootState): { app: string; status: boolean }[] => {
  const taskUpdate: any = state.taskUpdate
  const apps = taskUpdate.newTask.apps || taskUpdate.oldTask?.apps || []
  const hiddenApps = taskUpdate.newTask.hiddenApps || taskUpdate.oldTask?.hiddenApps || []
  const allApps = lodash.concat(
    apps.map((app: string) => ({ app, status: true })),
    hiddenApps.map((app: string) => ({ app, status: false }))
  )
  return lodash.sortBy(allApps, 'app')
}

/**
 * Get updated locales show/hide state in the task
 *
 * @param state The root state
 * @returns The updated locales state in the task
 */
export const getUpdatedLocales = (state: RootState): { locale: string; status: boolean }[] => {
  const taskUpdate: any = state.taskUpdate
  const locales = taskUpdate.newTask.locales || taskUpdate.oldTask?.locales || []
  const hiddenLocales = taskUpdate.newTask.hiddenLocales || taskUpdate.oldTask?.hiddenLocales || []
  const allLocales = lodash.concat(
    locales.map((locale: string) => ({ locale, status: true })),
    hiddenLocales.map((locale: string) => ({ locale, status: false }))
  )
  return lodash.sortBy(allLocales, 'locale')
}

/**
 * Get updated apps names in the task
 * The function is for show apps' name in UI
 *
 * @param state The root state
 * @returns The updated apps names
 */
export const getAppsRename = (state: RootState): { [app: string]: string } => {
  return state.taskUpdate.appsRename
}

/**
 * Get updated apps names in the task without new custom apps
 * The function is for getting final rename results to call the server API
 *
 * @param state The root state
 * @returns The updated apps names without new custom apps
 */
export const getAppsRenameWithoutCustom = (state: RootState): { [app: string]: string } => {
  return lodash.pickBy(getAppsRename(state), (_, app: string) => !app.startsWith(CUSTOM_APP))
}

/**
 * Get uploaded files
 *
 * @param state The root state
 * @returns The uploaded files including round id, app name and the file data
 */
export const getUploadScreensFiles = (
  state: RootState
): { roundId: string; app: string; file: File }[] => {
  const taskUpdate = state.taskUpdate
  const apps = taskUpdate.newTask.apps || taskUpdate.oldTask?.apps || []
  const hiddenApps = taskUpdate.newTask.hiddenApps || taskUpdate.oldTask?.hiddenApps || []
  const appsRename = taskUpdate.appsRename
  return taskUpdate.screensFiles
    .filter(uploadFile => apps.includes(uploadFile.app) || hiddenApps.includes(uploadFile.app))
    .map(uploadFile => ({
      ...uploadFile,
      app: appsRename[uploadFile.app] || uploadFile.app,
    }))
}

/**
 * Check the task is updated or not
 *
 * @param state - The root state
 * @returns The task is updated or not
 */
export const isTaskUpdated = (state: RootState): boolean => {
  const taskUpdateState = state.taskUpdate
  const updatedTask = taskUpdateState.newTask
  const appsRename = taskUpdateState.appsRename
  const screensFiles = taskUpdateState.screensFiles

  const taskValid = checkObjectUpdatedAndValid(updatedTask, 2, [
    { propertyName: 'label', options: { reg: taskLabelReg } },
  ])

  const renameValid = !lodash.isEmpty(appsRename)
    ? validateProperty(Object.values(appsRename), { reg: appNameReg })
    : null

  const screensFileValid = checkObjectUpdatedAndValid(screensFiles)

  return mergeUpdateResults(taskValid, renameValid, screensFileValid)
}

/**
 * Get round list in the task
 *
 * @param state The root state
 * @returns The rounds in the task
 */
export const getRounds = (state: RootState): Round[] => state.taskUpdate.rounds

export const {
  addNewCustomApp,
  renameAppInTask,
  setTaskToUpdate,
  updateAppStatus,
  updateLocaleStatus,
  updateTask,
  uploadFile,
} = taskUpdatingSlice.actions

export default taskUpdatingSlice.reducer
