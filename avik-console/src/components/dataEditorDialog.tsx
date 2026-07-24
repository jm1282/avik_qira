import React from 'react'
import Button from '@mui/material/Button'
import Dialog from '@mui/material/Dialog'
import DialogActions from '@mui/material/DialogActions'
import DialogContent from '@mui/material/DialogContent'
import DialogTitle from '@mui/material/DialogTitle'

import { buttonStyles, dialogStyles } from '../styles/componentStyles'

/**
 * The data edit dialog properties
 */
type DataEditorDialogProps = {
  open: boolean
  title: string
  children: JSX.Element | JSX.Element[]
  disableSave: boolean
  saveText?: string
  onSave?: () => void
  onClose?: () => void
}

/**
 * The component to show data editor in dialog
 *
 * @param props The dialog properties
 * @returns The element to edit data
 */
const DataEditorDialog = (props: DataEditorDialogProps): JSX.Element => {
  const { open, title, children, disableSave, saveText, onSave, onClose } = props

  return (
    <Dialog open={open} maxWidth='md' onClose={onClose} closeAfterTransition={false}>
      <DialogTitle>{title}</DialogTitle>
      <DialogContent sx={dialogStyles}>{children}</DialogContent>
      <DialogActions>
        <Button sx={buttonStyles} onClick={onSave} disabled={disableSave}>
          {saveText || 'Save'}
        </Button>
        <Button sx={buttonStyles} onClick={onClose}>
          Cancel
        </Button>
      </DialogActions>
    </Dialog>
  )
}

export default DataEditorDialog
