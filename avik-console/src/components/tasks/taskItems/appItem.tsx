import React, { useRef, useState } from 'react'
import IconButton from '@mui/material/IconButton'
import ListItem from '@mui/material/ListItem'
import ListItemText from '@mui/material/ListItemText'
import TextField from '@mui/material/TextField'
import Tooltip from '@mui/material/Tooltip'
import { DriveFileRenameOutline, UploadFile } from '@mui/icons-material'
import { get } from 'lodash'

import {
  getAppsRename,
  renameAppInTask,
  updateAppStatus,
} from '../../../store/reducers/TaskUpdateSlice'
import { useAppDispatch, useAppSelector } from '../../../store/hooks'
import ScreensFileUpload from './screensFileUpload'
import SwtichWithTooltip from '../../switchWithTooltip'

/**
 * The app item component properties
 */
type AppItemProps = {
  name: string
  status: boolean
}

/**
 * The component to show details of app in task
 *
 * @param props The component properties
 * @returns The component
 */
const AppItem = (props: AppItemProps): JSX.Element => {
  const { name, status } = props
  const appNameRef = useRef<HTMLInputElement>()
  const appsRename = useAppSelector<{ [app: string]: string }>(getAppsRename)
  const [showEditAppName, setShowEditAppName] = useState<boolean>(appsRename[name] === '')
  const [openUploadDialog, setOpenUploadDialog] = useState<boolean>(false)
  const [showName, setShowName] = useState<string>()
  const dispatch = useAppDispatch()

  React.useEffect(() => {
    if (!appsRename[name] && appsRename[name] !== '') {
      setShowName(name)
    } else {
      setShowName(appsRename[name])
    }
  }, [appsRename, name])

  /**
   * Handle event to hide app
   *
   * @param newStatus New status of the app
   */
  const handleHideApp = (newStatus: boolean) => {
    dispatch(updateAppStatus({ [name]: newStatus }))
  }

  /**
   * Handle event to click button to rename app
   */
  const handleClickRenameButton = () => {
    setShowEditAppName(true)
  }

  /**
   * Handle event to enter the new name of the app
   */
  const handleRename = () => {
    const newName = appNameRef.current?.value || ''
    dispatch(renameAppInTask({ [name]: newName }))
  }

  /**
   * Handle event to press enter after inputing new name or lose focus on editting the new name
   *
   * @param event The event to press key or lose focus
   */
  const handleFinishRename = (event: React.KeyboardEvent | React.FocusEvent) => {
    const newName = appNameRef.current?.value
    const keyValue = get(event, 'key')
    const isOffEditMode = !keyValue || keyValue === 'Enter'
    if (newName && newName !== '' && isOffEditMode) {
      setShowEditAppName(false)
    }
  }

  return (
    <ListItem>
      {showEditAppName ? (
        <TextField
          autoFocus
          fullWidth
          defaultValue={showName}
          inputRef={appNameRef}
          placeholder='Please Enter The App Name'
          onBlur={handleFinishRename}
          onChange={handleRename}
          onKeyUp={handleFinishRename}
        />
      ) : (
        <>
          <Tooltip title='Rename App'>
            <IconButton onClick={handleClickRenameButton}>
              <DriveFileRenameOutline />
            </IconButton>
          </Tooltip>
          <ListItemText primary={showName} />
        </>
      )}
      <Tooltip title='Upload Screens'>
        <IconButton sx={{ marginLeft: 'auto' }} onClick={() => setOpenUploadDialog(true)}>
          <UploadFile />
        </IconButton>
      </Tooltip>
      <SwtichWithTooltip tooltip='Switch App Status' checked={status} onChange={handleHideApp} />
      <ScreensFileUpload
        open={openUploadDialog}
        app={name}
        onClose={() => setOpenUploadDialog(false)}
      />
    </ListItem>
  )
}

export default AppItem
