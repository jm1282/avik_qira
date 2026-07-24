import FormData from 'form-data'
import { TypedJSON } from 'typedjson'
import { isEmpty } from 'lodash'

import httpclient from './clientInstance'
import ReviewTask from '../model/ReviewTask'
import { TaskType, getTaskTypeValues } from '../model/Task'
import Round from '../model/Round'
import { getItemsTotal } from '../utils'

/**
 * Get review tasks
 *
 * @param filteredTask The filtered task label
 * @param filteredType The filtered task type
 * @param pageNumber The page number
 * @param pageCount The page count
 * @returns All review tasks
 */
export const getAllReviewTasks = async (
  filteredTask: string | null,
  filteredType: TaskType,
  pageNumber: number,
  pageCount: number
): Promise<{ tasks: ReviewTask[]; total: number }> => {
  const params = {
    types: filteredType || getTaskTypeValues(),
    page_num: pageNumber <= 1 ? 1 : pageNumber,
    page_size: pageCount,
  }
  if (filteredTask && !isEmpty(filteredTask)) {
    Object.assign(params, { label: filteredTask })
  }
  const resp = await httpclient.get('tasks', { params })
  const total = getItemsTotal(resp)
  return { tasks: TypedJSON.parseAsArray(resp.data, ReviewTask), total }
}

/**
 * Get all rounds in the task
 *
 * @param id The task id
 * @returns All rounds in the task
 */
export const getRoundsInTask = async (id: string): Promise<Round[]> => {
  const resp = await httpclient.get(`task/${id}/rounds`)
  return TypedJSON.parseAsArray(resp.data, Round)
}

/**
 * Update the review task
 *
 * @param task - The review task to be updated
 * @returns The updated review task
 */
export const updateReviewTask = async (task: ReviewTask): Promise<ReviewTask | undefined> => {
  const resp = await httpclient.post('task', TypedJSON.toPlainJson(task, ReviewTask), {
    params: { id: task.id },
  })
  return TypedJSON.parse(resp.data, ReviewTask)
}

/**
 * Rename apps in the task
 *
 * @param id - The task id
 * @param appsRename - The mapping for renaming apps
 * @returns The updated review task
 */
export const renameAppsInTask = async (
  id: string,
  appsRename: { [name: string]: string }
): Promise<ReviewTask | undefined> => {
  const resp = await httpclient.post(`app/rename`, appsRename, { params: { taskid: id } })
  return TypedJSON.parse(resp.data, ReviewTask)
}

/**
 * Upload file into the app in task
 *
 * @param id - The review task id
 * @param screensFile - The screen files
 */
export const uploadScreensFiles = async (
  id: string,
  screensFile: { roundId: string; app: string; file: File }[]
) => {
  for (const uploadFile of screensFile) {
    const file = uploadFile.file
    const data = new FormData()
    data.append('roundId', uploadFile.roundId)
    data.append('app', uploadFile.app)
    data.append('screenshots', file)
    await httpclient.put(`task/${id}/screens`, data)
  }
}

/**
 * Remove the review task
 *
 * @param task - The review task to be removed
 */
export const removeReviewTask = async (task: ReviewTask) => {
  await httpclient.delete('task', { params: { id: task.id } })
}

/**
 * Reset the training task review status
 *
 * @param task - The task to be reset
 * @param locales The locales to be reset
 */
export const resetTrainingTask = async (task: ReviewTask, locales: string[]) => {
  if (task.type === TaskType.TRAINING) {
    await httpclient.post(`/task/${task.id}/resetTraining`, locales)
  } else {
    throw new Error('Support reseting training task only.')
  }
}
