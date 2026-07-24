import * as React from 'react'
import Button from '@mui/material/Button'
import Chip from '@mui/material/Chip'
import Stack from '@mui/material/Stack'
import Switch from '@mui/material/Switch'
import { concat } from 'lodash'
import moment from 'moment'

import { TaskType } from '../../model/Task'
import ReviewTask from '../../model/ReviewTask'
import CollapseTableRow from '../collapseTableRow'
import ConfirmDialog from '../confirmDialog'
import DetailItem from '../detailItem'
import TaskEdit from './taskEdit'
import TaskResetDialog from './taskItems/taskResetDialog'
import { useAppDispatch } from '../../store/hooks'
import { saveUpdatedTask } from '../../store/reducers/TaskSlice'
import { waitForReply } from '../../utils'
import SwtichWithTooltip from '../switchWithTooltip'

/**
 * The component properties
 */
type TaskRowProps = {
  key: string
  open?: boolean
  task: ReviewTask
  onOpenStateChange: (id?: string) => void
}

/**
 * The component to show one task details in one row
 *
 * @param props - The component properties
 * @returns The element of task row
 */
const TaskRow = (props: TaskRowProps): JSX.Element => {
  const openTaskDetail = props.open || false
  const task = props.task
  const [openEditTask, setOpenEditTask] = React.useState(false)
  const [openConfirmDialog, setOpenConfirmDialog] = React.useState(false)
  const [openResetConfirm, setOpenResetConfirm] = React.useState(false)
  const allLocales = concat(task.locales || [], task.hiddenLocales || []).sort()
  const allApps = concat(task.apps || [], task.hiddenApps || []).sort()
  const dispatch = useAppDispatch()

  /**
   * Renderer the row of the task table
   *
   * @param task - The review task
   */
  const rendererTaskRow = (task: ReviewTask): React.ReactNode[] => {
    const row = [
      task.label,
      TaskType[task.type || TaskType.REVIEW],
      moment(task.createtime).format('YYYY-MM-DD HH:mm'),
      <SwtichWithTooltip
        key={`${task.id}_status`}
        tooltip='Switch Task Status'
        checked={task.status === 1}
        onChange={() => setOpenConfirmDialog(true)}
      />,
    ]
    if (task.type == TaskType.TRAINING) {
      row.push(<Button onClick={() => setOpenResetConfirm(true)}>Reset</Button>)
    }
    return row
  }

  /**
   * Handle the event to confirm dialog
   *
   * @param confirm - Change status or not
   */
  const handleConfirmDialog = (confirm: boolean) => {
    if (confirm) {
      waitForReply(
        dispatch,
        () =>
          dispatch(
            saveUpdatedTask({
              id: task.id,
              status: task.status === 1 ? 0 : 1,
            })
          ),
        { successMessage: 'Success to update the task' }
      )
    }
    setOpenConfirmDialog(false)
  }

  /**
   * Handle click the collapse button
   */
  const handleClickCollapse = () => {
    if (!openTaskDetail) {
      props.onOpenStateChange(task.id || '')
    } else {
      props.onOpenStateChange()
    }
  }

  return (
    <>
      <CollapseTableRow
        id={task.id || ''}
        tableCells={rendererTaskRow(task)}
        open={openTaskDetail}
        onCollapseChange={handleClickCollapse}
        editButtonText='Edit Task'
        onEdit={() => setOpenEditTask(true)}>
        <Stack>
          <DetailItem>Locales</DetailItem>
          <DetailItem>
            {allLocales.map(locale => (
              <Chip
                key={locale}
                label={locale}
                variant='outlined'
                sx={{ m: 0.25 }}
                disabled={!task.locales?.includes(locale)}
              />
            ))}
          </DetailItem>
          <DetailItem>Apps</DetailItem>
          <DetailItem>
            {allApps.map(app => (
              <Chip
                key={app}
                label={app}
                variant='outlined'
                sx={{ m: 0.25 }}
                disabled={!task.apps?.includes(app)}
              />
            ))}
          </DetailItem>
          {task.type === TaskType.REVIEW ? (
            <DetailItem>
              String Link Supported
              <Switch disabled checked={task.stringLink} />
            </DetailItem>
          ) : null}
        </Stack>
      </CollapseTableRow>
      <TaskEdit open={openEditTask} task={task} onClose={() => setOpenEditTask(false)} />
      <ConfirmDialog
        openStatus={openConfirmDialog}
        confirmMessage={`Are you sure to change the status of the task ${task.label}?`}
        onConfirm={handleConfirmDialog}
      />
      <TaskResetDialog
        open={openResetConfirm}
        task={task}
        onClose={() => setOpenResetConfirm(false)}
      />
    </>
  )
}

export default TaskRow
