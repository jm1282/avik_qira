import React, { useState } from 'react'

import RoundDetail from './roundDetail'
import Round from '../../model/Round'
import DataEditorDialog from '../dataEditorDialog'
import { useAppDispatch, useAppSelector } from '../../store/hooks'
import { canSaveRound, getUpdatedRound } from '../../store/reducers/RoundUpdateSlice'
import ConfirmDialog from '../confirmDialog'
import { addRound, updateRound } from '../../store/reducers/RoundSlice'
import { waitForReply } from '../../utils'

/**
 * The component properties
 */
type RoundEditProps = {
  open: boolean
  round?: Round
  onClose?: () => void
}

/**
 * The component to edit round data
 *
 * @param props - The round properties
 * @returns - The element to edit the round data
 */
const RoundEdit = (props: RoundEditProps): JSX.Element => {
  const { open, round, onClose } = props
  const [openConfirmDialog, setOpenConfirmDialog] = useState<boolean>(false)
  const updatedRound = useAppSelector(getUpdatedRound)
  const canRoundBeSaved = useAppSelector(canSaveRound)
  const dispatch = useAppDispatch()

  /**
   * Handle confirmation to save round
   *
   * @param confirm Confirm to save the round
   */
  const handleConfirm = (confirm: boolean) => {
    if (confirm) {
      waitForReply(
        dispatch,
        async () => {
          if (updatedRound.id) {
            await dispatch(updateRound(updatedRound))
          } else {
            await dispatch(addRound(updatedRound))
          }
        },
        {
          successMessage: `Save round ${round?.buildLabel || updatedRound.buildLabel} successfully`,
        }
      )
    }
    setOpenConfirmDialog(false)
    closeDialog()
  }

  /**
   * Handle event to close the dialog
   */
  const closeDialog = () => {
    onClose && onClose()
  }

  return (
    <DataEditorDialog
      open={open}
      title={round ? 'Update round' : 'Create new round'}
      disableSave={!canRoundBeSaved}
      onSave={() => setOpenConfirmDialog(true)}
      onClose={closeDialog}>
      <RoundDetail round={round} />
      <ConfirmDialog
        openStatus={openConfirmDialog}
        confirmMessage={`Are you sure to ${round ? `update the round ${round.buildLabel}` : 'create new round'}?`}
        onConfirm={handleConfirm}
      />
    </DataEditorDialog>
  )
}

export default RoundEdit
