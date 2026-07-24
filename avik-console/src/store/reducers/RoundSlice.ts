import {
  ActionReducerMapBuilder,
  createAsyncThunk,
  createSlice,
  PayloadAction,
} from '@reduxjs/toolkit'
import { concat } from 'lodash'

import Round, { convertRoundFromObject } from '../../model/Round'
import {
  getRounds as getRoundsApi,
  updateRound as updateRoundApi,
  createRound as createRoundApi,
  removeRound as removeRoundApi,
  getProjects as getProjectsApi,
  getBuildLabels as getBuildLabelsApi,
} from '../../api/roundAPI'
import { RootState } from '../store'

/**
 * Define the round state
 */
export type RoundState = {
  total: number
  rounds: Round[]
  projects: string[]
  buildLabels: string[]
}

/**
 * The initial value of round state
 */
const initRoundsState: RoundState = {
  total: 0,
  rounds: [],
  projects: [],
  buildLabels: [],
}

/**
 * The async thunk to load rounds
 */
export const loadRounds = createAsyncThunk(
  'round/getRounds',
  async (options: {
    filteredRound: string | null
    pageNumber: number
    pageCount: number
  }): Promise<{ rounds: Round[]; total: number }> =>
    await getRoundsApi(options.filteredRound, options.pageNumber, options.pageCount)
)

/**
 * The async thunk to load projects
 */
export const loadProjects = createAsyncThunk(
  'round/getProjects',
  async (): Promise<string[]> => await getProjectsApi()
)

/**
 * The async thunk to load build labels
 */
export const loadBuildLabels = createAsyncThunk(
  'round/getBuildLabels',
  async (): Promise<string[]> => await getBuildLabelsApi()
)

/**
 * The async thunk to update the round
 */
export const updateRound = createAsyncThunk(
  'round/updateRound',
  async (round: any): Promise<Round | undefined> => {
    return await updateRoundApi(convertRoundFromObject(round))
  }
)

/**
 * The async thunk to create the round
 */
export const addRound = createAsyncThunk(
  'round/createRound',
  async (round: Round): Promise<Round | undefined> => {
    return await createRoundApi(convertRoundFromObject(round))
  }
)

/**
 * The async thunk to remove the round
 */
export const removeRound = createAsyncThunk(
  'round/removeRound',
  async (round: Round): Promise<string> => {
    await removeRoundApi(round)
    return round.id || ''
  }
)

/**
 * The slice of the round
 */
export const roundSlice = createSlice({
  name: 'round',
  initialState: initRoundsState,
  reducers: {},
  extraReducers: (builder: ActionReducerMapBuilder<RoundState>) => {
    builder.addCase(
      loadRounds.fulfilled,
      (
        state: RoundState,
        action: PayloadAction<{ rounds: Round[]; total: number }>
      ): RoundState => ({
        ...state,
        ...action.payload,
      })
    )
    builder.addCase(
      loadBuildLabels.fulfilled,
      (state: RoundState, action: PayloadAction<string[]>): RoundState => ({
        ...state,
        buildLabels: action.payload,
      })
    )
    builder.addCase(
      loadProjects.fulfilled,
      (state: RoundState, action: PayloadAction<string[]>): RoundState => ({
        ...state,
        projects: action.payload,
      })
    )
    builder.addCase(
      updateRound.fulfilled,
      (state: RoundState, action: PayloadAction<Round | undefined>): RoundState => {
        const updatedRound = action.payload
        if (updatedRound) {
          return {
            ...state,
            rounds: state.rounds.map(round =>
              round.id === updatedRound.id ? updatedRound : round
            ),
          }
        }
        return state
      }
    )
    builder.addCase(
      addRound.fulfilled,
      (state: RoundState, action: PayloadAction<Round | undefined>): RoundState => {
        const newRound = action.payload
        if (newRound) {
          return {
            ...state,
            rounds: concat(newRound, state.rounds),
            total: state.total + 1,
          }
        }
        return state
      }
    )
    builder.addCase(
      removeRound.fulfilled,
      (state: RoundState, action: PayloadAction<string>): RoundState => {
        return {
          ...state,
          rounds: state.rounds.filter(round => round.id !== action.payload),
          total: state.total - 1,
        }
      }
    )
  },
})

/**
 * List all rounds labels
 *
 * @param state - The root state
 * @returns The round label list
 */
export const allRoundsLabels = (state: RootState): string[] => state.round.buildLabels

/**
 * List rounds
 *
 * @param state - The root state
 * @param filterText - The fiter text for build label
 * @param pageNumber - The page number
 * @param pageCount - The count to show per page
 * @returns The round list and total rounds count
 */
export const listRounds = (state: RootState): [Round[], number] => {
  const roundData = state.round
  return [roundData.rounds, roundData.total]
}

/**
 * List projects of tasks
 *
 * @param state - The root state
 * @returns The project list
 */
export const listProjects = (state: RootState): string[] => state.round.projects

export default roundSlice.reducer
