import { ActionReducerMapBuilder, createAsyncThunk } from '@reduxjs/toolkit'
import { createSlice, PayloadAction } from '@reduxjs/toolkit'

import { RootState } from '../store'
import { getAllATMs } from '../../api/atmAPI'

/**
 * Define the ATM state
 */
export type AtmState = {
  isInitialState: boolean
  value: string[]
}

/**
 * Initial value of the ATM state
 */
const initialAtmsState: AtmState = {
  isInitialState: true,
  value: [],
}

/**
 * The async thunk to load rounds
 */
export const loadAtms = createAsyncThunk('atm/loadAtms', async () => await getAllATMs(), {
  condition: (_: void, { getState }) => {
    const state = getState() as RootState
    return state.atm.isInitialState
  },
})

/**
 * The slice of the ATM
 */
export const atmSlice = createSlice({
  name: 'atm',
  initialState: initialAtmsState,
  reducers: {},
  extraReducers: (builder: ActionReducerMapBuilder<AtmState>) => {
    builder.addCase(loadAtms.pending, (_: AtmState) => {
      return { value: [], isInitialState: true }
    })
    builder.addCase(loadAtms.fulfilled, (state: AtmState, action: PayloadAction<string[]>) => {
      return {
        value: action.payload,
        isInitialState: false,
      }
    })
  },
})

/**
 * List ATMs
 *
 * @param state - The root state
 * @returns The ATM list
 */
export const listAtms = (state: RootState): string[] => state.atm.value

export default atmSlice.reducer
