import * as React from 'react'
import Button from '@mui/material/Button'
import Dialog from '@mui/material/Dialog'
import DialogActions from '@mui/material/DialogActions'
import DialogContent from '@mui/material/DialogContent'
import DialogContentText from '@mui/material/DialogContentText'
import DialogTitle from '@mui/material/DialogTitle'

import { buttonStyles } from '../styles/componentStyles'

type ConfirmDialogProps = {
  openStatus?: boolean
  confirmMessage?: string
  onConfirm?: (confirm: boolean) => void
}

/**
 * Component to open a confirm dialog
 *
 * @param props - The property of the confirm dialog
 * @returns - The element of the confirm dialog
 */
const ConfirmDialog = (props: ConfirmDialogProps) => {
  /**
   * Handle the event to close confirm dialog
   *
   * @param confirm - confirm yes or no
   */
  const handleCloseDialog = (confirm: boolean = false) => {
    if (props.onConfirm) {
      props.onConfirm(confirm)
    }
  }

  return (
    <Dialog
      open={props.openStatus || false}
      onClose={() => handleCloseDialog()}
      closeAfterTransition={false}>
      <DialogTitle>Confirmation</DialogTitle>
      <DialogContent>
        <DialogContentText>{props.confirmMessage}</DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button sx={buttonStyles} onClick={() => handleCloseDialog(true)}>
          Yes
        </Button>
        <Button sx={buttonStyles} onClick={() => handleCloseDialog(false)}>
          No
        </Button>
      </DialogActions>
    </Dialog>
  )
}

export default ConfirmDialog
