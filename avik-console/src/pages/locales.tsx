import type { NextPage } from 'next'
import * as React from 'react'
import Container from '@mui/material/Container'

import AvikAliasesEdit from '../components/locales/avikAliasesEdit'
import BaseLocalesEdit from '../components/locales/baseLocalesEdit'
import SyncLocalesEdit from '../components/locales/syncLocalesEdit'
import {
  loadAllLocales,
  loadAllAliases,
  upsertBaseLocale,
  updateAvikAliases,
  syncAvikLocales,
} from '../store/reducers/LocaleSlice'
import { useAppDispatch } from '../store/hooks'
import Locale from '../model/Locale'
import { waitForReply } from '../utils'

/**
 * The component to show page locale
 *
 * @returns The element of page locale
 */
const Locales: NextPage<void> = () => {
  const dispatch = useAppDispatch()

  React.useEffect(() => {
    const loadingData = async () => {
      await Promise.all([dispatch(loadAllLocales()), dispatch(loadAllAliases())])
    }
    waitForReply(dispatch, loadingData)
  }, [dispatch])

  /**
   * Save the aliases in Avik scope
   *
   * @param aliases - The aliases in Avik scope
   */
  const saveAvikAliases = (aliases: string[]) => {
    waitForReply(dispatch, () => dispatch(updateAvikAliases(aliases)))
  }

  /**
   * Save the base locale after editing
   *
   * @param baseLocale - The base locale edited
   */
  const saveBaseLocale = (baseLocale: Locale) => {
    waitForReply(dispatch, () => dispatch(upsertBaseLocale(baseLocale)))
  }

  /**
   * Sync locales with alias
   */
  const syncLocales = () => {
    waitForReply(dispatch, () => dispatch(syncAvikLocales()))
  }

  return (
    <Container maxWidth='xl'>
      <SyncLocalesEdit onSync={syncLocales} />
      <AvikAliasesEdit onSave={saveAvikAliases} />
      <BaseLocalesEdit onSave={saveBaseLocale} />
    </Container>
  )
}

export default Locales
