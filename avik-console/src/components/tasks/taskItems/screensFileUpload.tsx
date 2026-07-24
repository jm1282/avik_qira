import React, { useRef, useState } from 'react'
import MenuItem from '@mui/material/MenuItem'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'

import { getRounds, uploadFile } from '../../../store/reducers/TaskUpdateSlice'
import { useAppDispatch, useAppSelector } from '../../../store/hooks'
import Round from '../../../model/Round'
import DataEditorDialog from '../../dataEditorDialog'

/**
 * The screen file upload component properties
 */
type ScreensFileUploadProps = {
  open: boolean
  app: string
  onClose?: () => void
}

/**
 * The component to upload screens file
 *
 * @param props The component properties
 * @returns The component
 */
const ScreensFileUpload = (props: ScreensFileUploadProps): JSX.Element => {
  const { open, app, onClose } = props
  const [roundId, setRoundId] = useState<string>('')
  const [disableUpload, setDisableUpload] = useState<boolean>(true)
  const screensFileInput = useRef<HTMLInputElement>()
  const dispatch = useAppDispatch()
  const rounds = useAppSelector(getRounds)

  /**
   * Handle event to select round
   *
   * @param event The event to select round
   */
  const handleSelectRound = (event: React.ChangeEvent<HTMLInputElement>) => {
    setRoundId(event.target.value)
  }

  /**
   * Handle event to select file to be uploaded
   */
  const handleSelectFile = () => {
    const selectedFile = screensFileInput.current?.value || ''
    setDisableUpload(!selectedFile.endsWith('.zip'))
  }

  /**
   * Upload screens file
   */
  const uploadScreensFile = () => {
    const files = screensFileInput.current?.files
    if (files) {
      dispatch(uploadFile({ roundId, app, file: files[0] }))
    }
    handleCloseDialog()
  }

  /**
   * Handle event to close the dialog
   */
  const handleCloseDialog = () => {
    setDisableUpload(true)
    onClose && onClose()
  }

  return (
    <DataEditorDialog
      open={open}
      title='Upload Screens File'
      disableSave={disableUpload}
      saveText='Upload'
      onSave={uploadScreensFile}
      onClose={handleCloseDialog}>
      <TextField fullWidth label='Build Label' select onChange={handleSelectRound}>
        {rounds.map((round: Round) => (
          <MenuItem key={round.id} value={round.id}>
            {round.buildLabel}
          </MenuItem>
        ))}
      </TextField>
      <TextField fullWidth inputRef={screensFileInput} type='file' onChange={handleSelectFile} />
      <Typography variant='caption' component='div'>
        Note: Select screens packed in *.zip file
      </Typography>
    </DataEditorDialog>
  )
}

export default ScreensFileUpload
