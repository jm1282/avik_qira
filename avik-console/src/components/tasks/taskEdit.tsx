import React, { useState } from 'react'
import TaskDetail from './taskDetail'
import ReviewTask from '../../model/ReviewTask'
import { useAppDispatch, useAppSelector } from '../../store/hooks'
import {
  getAppsRenameWithoutCustom,
  getUpdateTask,
  getUploadScreensFiles,
  isTaskUpdated,
} from '../../store/reducers/TaskUpdateSlice'
import { waitForReply } from '../../utils'
import { keys } from 'lodash'
import { uploadScreensFiles } from '../../api/taskAPI'
import { renameApps, saveUpdatedTask } from '../../store/reducers/TaskSlice'
import ConfirmDialog from '../confirmDialog'
import DataEditorDialog from '../dataEditorDialog'

/**
 * The component properties
 */
type TaskProps = {
  open: boolean
  task: ReviewTask
  onClose?: () => void
}

/**
 * The component to edit task data
 *
 * @param props - The component properties
 * @returns - The element to edit the task data
 */
const TaskEdit = (props: TaskProps): JSX.Element => {
  const { open, task, onClose } = props
  const [openConfirmDialog, setOpenConfirmDialog] = useState(false)
  const isUpdated = useAppSelector(isTaskUpdated)
  const updatedTask = useAppSelector(getUpdateTask)
  const appsRename = useAppSelector(getAppsRenameWithoutCustom)
  const screensFiles = useAppSelector(getUploadScreensFiles)
  const dispatch = useAppDispatch()

  /**
   * Save the updated task
   */
  const saveTask = () => {
    const updateTaskProcess = async () => {
      if (keys(updatedTask).length > 1) {
        await dispatch(saveUpdatedTask(updatedTask))
      }
      if (keys(appsRename).length > 0) {
        await dispatch(renameApps({ taskId: updatedTask.id, appsRename }))
      }
      if (keys(screensFiles).length > 0 && updatedTask.id) {
        await uploadScreensFiles(updatedTask.id, screensFiles)
      }
    }
    waitForReply(dispatch, updateTaskProcess, { successMessage: 'Success to update the task' })
  }

  /**
   * Handle confirm to save task
   *
   * @param confirm Confirm to save task or not
   */
  const handleConfirm = (confirm: boolean) => {
    if (confirm) {
      saveTask()
    }
    setOpenConfirmDialog(false)
    if (props.onClose) {
      props.onClose()
    }
  }

  return (
    <DataEditorDialog
      open={open}
      title='Update Task'
      disableSave={!isUpdated}
      onSave={() => setOpenConfirmDialog(true)}
      onClose={onClose}>
      <TaskDetail task={task} />
      <ConfirmDialog
        openStatus={openConfirmDialog}
        confirmMessage={`Are you sure to save the task ${task.label}?`}
        onConfirm={handleConfirm}
      />
    </DataEditorDialog>
  )
}

export default TaskEdit
