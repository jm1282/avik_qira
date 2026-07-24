import {
  ActionReducerMapBuilder,
  createAsyncThunk,
  createSlice,
  PayloadAction,
} from '@reduxjs/toolkit'
import lodash from 'lodash'

import Locale from '../../model/Locale'
import {
  getAllLocales as getAllLocalesApi,
  syncLocales as syncLocalesApi,
  getAllAliases as getAllAliasesApi,
  updateAvikAlias as updateAvikAliasApi,
  upsertBaseLocale as upsertBaseLocaleApi,
  removeBaseLocale as removeBaseLocaleApi,
} from '../../api/localesAPI'
import { RootState } from '../store'
import LocaleAlias from '../../model/LocaleAlias'

/**
 * Define the locale group state
 */
export type LocaleState = {
  isLoadedAllLocales: boolean
  isLoadedAllAliases: boolean
  allLocales: Locale[]
  allAliases: LocaleAlias[]
}

/**
 * The initial value of locale group state
 */
const initLocaleState: LocaleState = {
  isLoadedAllLocales: false,
  isLoadedAllAliases: false,
  allLocales: [],
  allAliases: [],
}

/**
 * The async thunk to load all locales
 */
export const loadAllLocales = createAsyncThunk(
  'locale/getAllLocales',
  async (): Promise<Locale[]> => await getAllLocalesApi(),
  {
    condition: (args: void, { getState }) => {
      const state = getState() as RootState
      return !state.locale.isLoadedAllLocales
    },
  }
)

/**
 * The async thunk to load avik aliases
 */
export const loadAllAliases = createAsyncThunk(
  'locale/getAllAliases',
  async (): Promise<LocaleAlias[]> => {
    return await getAllAliasesApi()
  },
  {
    condition: (args: void, { getState }) => {
      const state = getState() as RootState
      return !state.locale.isLoadedAllAliases
    },
  }
)

/**
 * The async thunk to update avik aliases
 */
export const updateAvikAliases = createAsyncThunk(
  'locale/updateAvikAliases',
  async (aliases: string[]): Promise<string[]> => await updateAvikAliasApi(aliases)
)

/**
 * The async thunk to update/create a base locale
 */
export const upsertBaseLocale = createAsyncThunk(
  'locale/upsertBaseLocale',
  async (baseLocale: Locale): Promise<Locale | undefined> =>
    await upsertBaseLocaleApi(baseLocale.localeId, baseLocale)
)

/**
 * The async thunk to remove a base locale
 */
export const removeBaseLocale = createAsyncThunk(
  'locale/removeBaseLocale',
  async (baseLocale: Locale): Promise<string> => await removeBaseLocaleApi(baseLocale.localeId)
)

/**
 * The async thunk to sync and refresh locales and aliases from Locale Mapper
 */
export const syncAvikLocales = createAsyncThunk(
  'locale/syncLocales',
  async (): Promise<{ locales: Locale[]; aliases: LocaleAlias[] }> => {
    await syncLocalesApi()
    return {
      locales: await getAllLocalesApi(),
      aliases: await getAllAliasesApi(),
    }
  }
)

/**
 * The slice of the locale group
 */
export const localeSlice = createSlice({
  name: 'locale',
  initialState: initLocaleState,
  reducers: {},
  extraReducers: (builder: ActionReducerMapBuilder<LocaleState>) => {
    builder.addCase(
      loadAllLocales.fulfilled,
      (state: LocaleState, action: PayloadAction<Locale[]>) => {
        action.payload.forEach(locale => state.allLocales.push(locale))
        state.isLoadedAllLocales = true
      }
    )
    builder.addCase(
      loadAllAliases.fulfilled,
      (state: LocaleState, action: PayloadAction<LocaleAlias[]>) => {
        action.payload.forEach(alias => state.allAliases.push(alias))
        state.isLoadedAllAliases = true
      }
    )
    builder.addCase(
      updateAvikAliases.fulfilled,
      (state: LocaleState, action: PayloadAction<string[]>) => {
        const avikAliases = action.payload
        state.allAliases.forEach(alias => (alias.isVisible = avikAliases.includes(alias.aliasId)))
      }
    )
    builder.addCase(
      upsertBaseLocale.fulfilled,
      (state: LocaleState, action: PayloadAction<Locale | undefined>) => {
        const newBaseLocale = action.payload
        if (newBaseLocale) {
          const allLocales = state.allLocales
          const index = lodash.findIndex(
            allLocales,
            locale => locale.localeId === newBaseLocale.localeId
          )
          if (index < 0) {
            allLocales.splice(
              lodash.sortedIndexBy(
                allLocales,
                newBaseLocale,
                locale => locale.localeId === newBaseLocale.localeId
              ),
              0,
              newBaseLocale
            )
          } else {
            allLocales[index] = newBaseLocale
          }
        }
      }
    )
    builder.addCase(
      removeBaseLocale.fulfilled,
      (state: LocaleState, action: PayloadAction<string>) => {
        state.allLocales = state.allLocales.map(locale => {
          if (locale.localeId === action.payload) {
            locale.isBaseLocale = false
          }
          return locale
        })
      }
    )
    builder.addCase(
      syncAvikLocales.fulfilled,
      (
        state: LocaleState,
        action: PayloadAction<{
          locales: Locale[]
          aliases: LocaleAlias[]
        }>
      ) => {
        const response = action.payload
        if (response) {
          state.allLocales = response.locales
          state.allAliases = response.aliases
        }
      }
    )
  },
})

/**
 * List locales in Avik scope
 *
 * @param state - The root state
 * @returns The locale list
 */
export const listLocales = (state: RootState): Locale[] => state.locale.allLocales

/**
 * List aliases
 *
 * @param state - The root state
 * @param onlyInAvikScope - Is the alias in the Avik scope only
 * @returns The aliases list
 */
export const listAliases = (state: RootState, onlyInAvikScope: boolean = false): LocaleAlias[] =>
  state.locale.allAliases.filter(alias => !onlyInAvikScope || alias.isVisible)

/**
 * List all base locales
 *
 * @param state - The root state
 * @returns The base locale list
 */
export const listBaseLocales = (state: RootState): Locale[] =>
  state.locale.allLocales.filter(locale => locale.isBaseLocale)

export default localeSlice.reducer
