import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import { RootState } from '../store'
import { AlertColor } from '@mui/material'

export const SnackbarType: { [name: string]: AlertColor } = {
  success: 'success',
  error: 'error',
  info: 'info',
  warning: 'warning',
}

interface SnackbarState {
  open: boolean
  severity: string
  message: string
}

/**
 * The slice of the ATM
 */
export const snackbarSlice = createSlice({
  name: 'snackbar',
  initialState: { open: false, severity: 'success', message: '' },
  reducers: {
    setSnackbarMessage: (
      _: SnackbarState,
      action: PayloadAction<{ severity: string; message: string }>
    ) => {
      return { ...action.payload, open: true }
    },
    setSnackbarOpenState: (state: SnackbarState, action: PayloadAction<boolean>) => {
      return { ...state, open: action.payload }
    },
  },
})

/**
 * Get snack bar state
 *
 * @param state The root state
 * @returns snack bar state
 */
export const getSnackbarSliceState = (state: RootState) => state.snackbar

export const { setSnackbarMessage, setSnackbarOpenState } = snackbarSlice.actions

export default snackbarSlice.reducer
