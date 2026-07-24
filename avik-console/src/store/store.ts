import { configureStore, ThunkAction, Action } from '@reduxjs/toolkit'

import roundReducer from './reducers/RoundSlice'
import localeReducer from './reducers/LocaleSlice'
import taskReducer from './reducers/TaskSlice'
import atmReducer from './reducers/AtmSlice'
import loadingReducer from './reducers/LoadingSlice'
import roundUpdateReducer from './reducers/RoundUpdateSlice'
import taskUpdateReducer from './reducers/TaskUpdateSlice'
import snackbarReducer from './reducers/SnackbarSlice'

/**
 * The store to keep the round, locale, task data
 */
export const store = configureStore({
  reducer: {
    round: roundReducer,
    locale: localeReducer,
    task: taskReducer,
    atm: atmReducer,
    loading: loadingReducer,
    roundUpdate: roundUpdateReducer,
    taskUpdate: taskUpdateReducer,
    snackbar: snackbarReducer,
  },
  middleware: getDefaultMiddleware =>
    getDefaultMiddleware({
      serializableCheck: false,
    }),
})

export type AppDispatch = typeof store.dispatch
export type RootState = ReturnType<typeof store.getState>
export type AppThunk<ReturnType = void> = ThunkAction<
  ReturnType,
  RootState,
  unknown,
  Action<string>
>
