import * as React from 'react'
import { useRouter } from 'next/router'
import { SxProps } from '@mui/system'
import Alert from '@mui/material/Alert'
import AppBar from '@mui/material/AppBar'
import Toolbar from '@mui/material/Toolbar'
import Avatar from '@mui/material/Avatar'
import Snackbar from '@mui/material/Snackbar'
import Typography from '@mui/material/Typography'
import Button from '@mui/material/Button'
import AccountCircleIcon from '@mui/icons-material/AccountCircle'

import { buttonPressed } from '../styles/componentStyles'
import LoadingData from '../components/loadingData'
import { config } from '../utils'
import { getLoadingState } from '../store/reducers/LoadingSlice'
import { useAppSelector, useAppDispatch } from '../store/hooks'
import {
  getSnackbarSliceState,
  setSnackbarOpenState,
  SnackbarType,
} from '../store/reducers/SnackbarSlice'

type Props = {
  account?: string | null
}

/**
 * The navigation component of the page
 *
 * @returns The element of the navigation
 */
export default function NavigationBar({ account }: Props) {
  const router = useRouter()
  const loading = useAppSelector(getLoadingState)
  const snackbarState = useAppSelector(getSnackbarSliceState)
  const dispatch = useAppDispatch()

  const buttonStyle = (path: string): SxProps | undefined => {
    if (path == router.route) {
      return buttonPressed
    }
  }

  return (
    <AppBar position='fixed'>
      <Toolbar>
        <Avatar src={`${config.basePath}/logo.a024876.png`} />
        <Typography variant='h5' component='div' sx={{ flexGrow: 1 }}>
          AViK Console
        </Typography>
        <Button
          color='inherit'
          size='large'
          sx={buttonStyle('/rounds')}
          href={`${config.basePath}/rounds`}>
          Rounds
        </Button>
        <Button
          color='inherit'
          size='large'
          sx={buttonStyle('/tasks')}
          href={`${config.basePath}/tasks`}>
          Tasks
        </Button>
        <Button
          color='inherit'
          size='large'
          sx={buttonStyle('/locales')}
          href={`${config.basePath}/locales`}>
          Locales
        </Button>
        <AccountCircleIcon sx={{ m: 1 }} />
        {account || 'Anonymous'}
      </Toolbar>
      <LoadingData open={loading} />
      <Snackbar
        open={snackbarState.open}
        autoHideDuration={3000}
        onClose={() => dispatch(setSnackbarOpenState(false))}>
        <Alert severity={SnackbarType[snackbarState.severity]}>{snackbarState.message}</Alert>
      </Snackbar>
    </AppBar>
  )
}
