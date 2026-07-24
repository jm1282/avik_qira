import type { NextPage } from 'next'
import { useSearchParams } from 'next/navigation'
import * as React from 'react'

import ReviewTask from '../model/ReviewTask'
import DataPage from '../components/dataPage'
import TaskRow from '../components/tasks/taskRow'
import { loadTasks, listTasks } from '../store/reducers/TaskSlice'
import { useAppSelector, useAppDispatch } from '../store/hooks'
import { TaskType, getTaskTypeDict } from '../model/Task'
import { loadAtms } from '../store/reducers/AtmSlice'
import { waitForReply } from '../utils'

const columns = ['Task Label', 'Type', 'Create Time', 'Status']

/**
 * The component to show page task
 *
 * @returns The element of page task
 */
const Tasks: NextPage<void> = () => {
  const params = useSearchParams()
  const pageNumber = Number(params.get('pageNumber') || 1)
  const pageCount = Number(params.get('pageCount') || 10)
  const filteredTask = params.get('task')
  const filteredType = Number(params.get('type') || TaskType.REVIEW)
  const [openRow, setOpenRow] = React.useState<string>()
  const [reviewTasks, reviewTaskCount] = useAppSelector(listTasks)
  const dispatch = useAppDispatch()

  React.useEffect(() => {
    const loadingData = async () => {
      await dispatch(loadTasks({ filteredTask, filteredType, pageNumber, pageCount }))
    }
    waitForReply(dispatch, loadingData)
  }, [dispatch, pageNumber, pageCount, filteredTask, filteredType])

  React.useEffect(() => {
    const loadingData = async () => {
      await dispatch(loadAtms())
    }
    waitForReply(dispatch, loadingData)
  }, [dispatch])

  /**
   * Handle event to change the type filter
   *
   * @param value The task type to change to
   * @return The updated query params
   */
  const handleFilteredTypeChang = (value: any) => ({ type: value })

  /**
   * Handle event to change the text filter
   *
   * @param value The task label to search
   * @return The updated query params
   */
  const handleFilteredTextChange = (value: string) => ({ task: value })

  return (
    <DataPage
      columns={columns}
      defaultTextInSearch={filteredTask || ''}
      total={reviewTaskCount}
      onFilteredTextChange={handleFilteredTextChange}
      typeFilters={getTaskTypeDict()}
      selectedTypeFilter={TaskType.REVIEW}
      onFilteredTypeChange={handleFilteredTypeChang}>
      {reviewTasks.map((task: ReviewTask) => (
        <TaskRow
          key={task.id || ''}
          task={task}
          open={openRow === task.id}
          onOpenStateChange={(id?: string) => setOpenRow(id)}
        />
      ))}
    </DataPage>
  )
}

export default Tasks
