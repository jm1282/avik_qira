import {
  ActionReducerMapBuilder,
  createAsyncThunk,
  createSlice,
  isAnyOf,
  PayloadAction,
} from '@reduxjs/toolkit'

import ReviewTask, { convertReviewTaskFromObject } from '../../model/ReviewTask'
import {
  getAllReviewTasks as getAllReviewTasksApi,
  updateReviewTask as updateReviewTaskApi,
  removeReviewTask as removeReviewTaskApi,
  renameAppsInTask,
} from '../../api/taskAPI'
import { RootState } from '../store'
import { TaskType } from '../../model/Task'

/**
 * Define the review task state
 */
export type ReviewTaskState = {
  total: number
  tasks: ReviewTask[]
}

/**
 * Initial value of the review task state
 */
const initReviewTaskState: ReviewTaskState = {
  total: 0,
  tasks: [],
}

/**
 * The async thunk to load tasks
 */
export const loadTasks = createAsyncThunk(
  'task/getAllTasks',
  async (options: {
    filteredTask: string | null
    filteredType: TaskType
    pageNumber: number
    pageCount: number
  }) =>
    await getAllReviewTasksApi(
      options.filteredTask,
      options.filteredType,
      options.pageNumber,
      options.pageCount
    )
)

/**
 * The async thunk to update the task
 */
export const saveUpdatedTask = createAsyncThunk(
  'task/saveUpdatedTask',
  async (updatedTask: any): Promise<ReviewTask | undefined> => {
    return await updateReviewTaskApi(convertReviewTaskFromObject(updatedTask))
  }
)

/**
 * The async thunk to rename apps in the task
 */
export const renameApps = createAsyncThunk(
  'task/renameApps',
  async (args: {
    taskId: string
    appsRename: { [oldName: string]: string }
  }): Promise<ReviewTask | undefined> => {
    const { taskId, appsRename } = args
    return await renameAppsInTask(taskId, appsRename)
  }
)

/**
 * The async thunk to remove the task
 */
export const removeTask = createAsyncThunk('round/removeTask', async (task: ReviewTask) => {
  await removeReviewTaskApi(task)
  return task.id || ''
})

/**
 * The slice of the task
 */
export const taskSlice = createSlice({
  name: 'task',
  initialState: initReviewTaskState,
  reducers: {},
  extraReducers: (builder: ActionReducerMapBuilder<ReviewTaskState>) => {
    builder.addCase(
      loadTasks.fulfilled,
      (
        _: ReviewTaskState,
        action: PayloadAction<{ tasks: ReviewTask[]; total: number }>
      ): ReviewTaskState => action.payload
    )
    builder.addCase(
      removeTask.fulfilled,
      (state: ReviewTaskState, action: PayloadAction<string>): ReviewTaskState => ({
        tasks: state.tasks.filter(task => task.id !== action.payload),
        total: state.total - 1,
      })
    )
    builder.addMatcher(
      isAnyOf(saveUpdatedTask.fulfilled, renameApps.fulfilled),
      (state: ReviewTaskState, action: PayloadAction<ReviewTask | undefined>): ReviewTaskState => {
        const updatedTask = action.payload
        if (updatedTask) {
          return {
            total: state.total,
            tasks: state.tasks.map(task => (task.id === updatedTask.id ? updatedTask : task)),
          }
        }
        return state
      }
    )
  },
})

/**
 * List review tasks
 *
 * @param state - The root state
 * @param filterText - The filtered text for task label
 * @param filterType - The filtered type
 * @param pageNumber - The page number
 * @param pageCount - The count to show per page
 * @returns The review task list and total tasks count
 */
export const listTasks = (state: RootState): [ReviewTask[], number] => {
  const tasksData = state.task
  return [tasksData.tasks, tasksData.total]
}

export default taskSlice.reducer
