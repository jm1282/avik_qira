import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import { RootState } from '../store'

/**
 * The slice of the ATM
 */
export const loadingSlice = createSlice({
  name: 'loading',
  initialState: false,
  reducers: {
    setLoading: (_: boolean, action: PayloadAction<boolean>) => {
      return action.payload
    },
  },
})

/**
 * Get the loading process state
 *
 * @param state The root state
 * @returns The loading process state
 */
export const getLoadingState = (state: RootState) => state.loading

export const { setLoading } = loadingSlice.actions

export default loadingSlice.reducer
