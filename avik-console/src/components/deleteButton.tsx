import * as React from 'react'
import Button from '@mui/material/Button'
import Dialog from '@mui/material/Dialog'
import DialogActions from '@mui/material/DialogActions'
import DialogTitle from '@mui/material/DialogTitle'
import DialogContent from '@mui/material/DialogContent'
import DialogContentText from '@mui/material/DialogContentText'
import Tooltip from '@mui/material/Tooltip'
import Delete from '@mui/icons-material/Delete'

import { buttonStyles } from '../styles/componentStyles'

type DeleteButtonProps = {
  deleteButtonText?: string
  deleteConfirmText?: string
  onDelete?: () => void
}

/**
 * The component to show delete button
 *
 * @returns The element of delete button
 */
const DeleteButton = (props: DeleteButtonProps): JSX.Element => {
  const deleteButtonText = props.deleteButtonText || 'Delete Item'
  const deleteConfirmText = props.deleteConfirmText
  const [open, setOpen] = React.useState(false)

  /**
   * Handle click delete button event
   */
  const handleDelete = () => {
    if (deleteConfirmText) {
      setOpen(true)
    } else {
      props.onDelete && props.onDelete()
    }
  }

  /**
   * Handle the dialog close event
   *
   * @param isDelete - Delete the item or not
   */
  const handleClose = (isDelete: boolean = false) => {
    if (isDelete) {
      props.onDelete && props.onDelete()
    }
    setOpen(false)
  }

  return (
    <>
      <Tooltip title={deleteButtonText}>
        <Button color='primary' sx={buttonStyles} startIcon={<Delete />} onClick={handleDelete}>
          {deleteButtonText}
        </Button>
      </Tooltip>
      {deleteConfirmText ? (
        <Dialog open={open} onClose={() => handleClose()} closeAfterTransition={false}>
          <DialogTitle>Alert</DialogTitle>
          <DialogContent>
            <DialogContentText>{deleteConfirmText}</DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => handleClose(true)}>Yes</Button>
            <Button onClick={() => handleClose(false)}>No</Button>
          </DialogActions>
        </Dialog>
      ) : null}
    </>
  )
}

export default DeleteButton
