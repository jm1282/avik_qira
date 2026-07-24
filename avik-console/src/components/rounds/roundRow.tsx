import * as React from 'react'
import Chip from '@mui/material/Chip'
import Stack from '@mui/material/Stack'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import moment from 'moment'
import { isEmpty } from 'lodash'

import Task, { TaskType } from '../../model/Task'
import Round from '../../model/Round'
import CollapseTableRow from '../collapseTableRow'
import ConfirmDialog from '../confirmDialog'
import DetailItem from '../detailItem'
import RoundEdit from './roundEdit'
import { checkLocale } from '../../utils/localeUtils'
import { useAppDispatch, useAppSelector } from '../../store/hooks'
import { listLocales } from '../../store/reducers/LocaleSlice'
import { removeRound, updateRound } from '../../store/reducers/RoundSlice'
import { config, waitForReply } from '../../utils'
import SwtichWithTooltip from '../switchWithTooltip'

/**
 * The component to show task in round details
 *
 * @returns The element of task
 */
const TaskArea = ({ task }: { task: Task; key: string }): JSX.Element => {
  return (
    <TableRow sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
      <TableCell>{task.label}</TableCell>
      <TableCell>{TaskType[task.type || TaskType.REVIEW]}</TableCell>
      <TableCell>{task.atm}</TableCell>
      <TableCell>{task.projects?.join(', ')}</TableCell>
    </TableRow>
  )
}

/**
 * The component properties
 */
type RoundRowProps = {
  round: Round
  open: boolean
  onOpenStateChange: (id?: string) => void
}

/**
 * The component to show one round details in one row
 *
 * @returns The element of round row
 */
const RoundRow = (props: RoundRowProps): JSX.Element => {
  const { open, round, onOpenStateChange } = props
  const { id, buildLabel, locales, tasks, status, referenceRounds, referenceLocale } = round
  const [openRoundEdit, setOpenRoundEdit] = React.useState(false)
  const [openConfirmDialog, setOpenConfirmDialog] = React.useState(false)
  const hasReferenceRounds = referenceRounds && !isEmpty(referenceRounds)
  const allLocales = useAppSelector(listLocales)
  const dispatch = useAppDispatch()

  /**
   * Delete the round
   */
  const deleteRound = () => {
    waitForReply(dispatch, () => dispatch(removeRound(round)), {
      successMessage: `Delete round ${buildLabel} successfully`,
    })
  }

  /**
   * Renderer the row of the round table
   *
   * @param round - The round
   */
  const rendererRoundRow = (round: Round): React.ReactNode[] => {
    return [
      round.buildLabel,
      checkLocale(round.baseLocale || config.defaultBaseLocale, allLocales),
      round.tasks?.map((task: Task) => <div key={task.label}>{task.label}</div>),
      moment(round.createTime).format('YYYY-MM-DD HH:mm'),
      <SwtichWithTooltip
        key={`${round.id}_status`}
        tooltip='Switch Round Status'
        checked={round.status === 1}
        onChange={() => setOpenConfirmDialog(true)}
      />,
    ]
  }

  /**
   * Handle click the collapse button
   */
  const handleClickCollapse = () => {
    if (!open) {
      onOpenStateChange(round.id || '')
    } else {
      onOpenStateChange()
    }
  }

  /**
   * Handle the event to confirm dialog
   *
   * @param confirm - Change status or not
   */
  const handleConfirmDialog = (confirm: boolean) => {
    if (confirm) {
      waitForReply(dispatch, () => dispatch(updateRound({ id: id, status: (status || 0) ^ 1 })), {
        successMessage: `Save round ${buildLabel} successfully`,
      })
    }
    setOpenConfirmDialog(false)
  }

  return (
    <>
      <CollapseTableRow
        id={round.id || ''}
        tableCells={rendererRoundRow(round)}
        open={open}
        onCollapseChange={handleClickCollapse}
        editButtonText='Update Round'
        onEdit={() => setOpenRoundEdit(true)}
        deleteButtonText='Delete Round'
        deleteConfirmText='Are you sure to delete the round?'
        onDelete={deleteRound}>
        <Stack>
          <DetailItem>Locales</DetailItem>
          <DetailItem>
            {locales?.map((locale: string) => (
              <Chip
                key={locale}
                label={checkLocale(locale, allLocales)}
                variant='outlined'
                sx={{ m: 0.25 }}
              />
            ))}
          </DetailItem>
          {referenceLocale ? (
            <DetailItem>
              Reference Locale <Chip label={referenceLocale} variant='outlined' />
            </DetailItem>
          ) : null}
          <DetailItem>Tasks Details</DetailItem>
          <DetailItem>
            <Table size='small'>
              <TableHead>
                <TableRow sx={{ '&.MuiTableRow-root th': { fontWeight: 600 } }}>
                  <TableCell>Label</TableCell>
                  <TableCell>Type</TableCell>
                  <TableCell>ATM</TableCell>
                  <TableCell>Projects</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {tasks?.map((task: Task) => <TaskArea key={task.label || ''} task={task} />)}
              </TableBody>
            </Table>
          </DetailItem>
          {hasReferenceRounds && (
            <>
              <DetailItem>Reference rounds</DetailItem>
              <DetailItem>
                {referenceRounds?.map((referenceRound: string) => (
                  <Chip
                    key={referenceRound}
                    label={referenceRound}
                    variant='outlined'
                    sx={{ m: 0.25 }}
                  />
                ))}
              </DetailItem>
            </>
          )}
        </Stack>
      </CollapseTableRow>
      <RoundEdit open={openRoundEdit} round={round} onClose={() => setOpenRoundEdit(false)} />
      <ConfirmDialog
        openStatus={openConfirmDialog}
        confirmMessage={`Are you sure to change the status of the round ${round.buildLabel}?`}
        onConfirm={handleConfirmDialog}
      />
    </>
  )
}

export default RoundRow
