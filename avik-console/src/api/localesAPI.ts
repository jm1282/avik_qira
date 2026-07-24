import { TypedJSON } from 'typedjson'

import httpclient from './clientInstance'
import Locale from '../model/Locale'
import LocaleAlias from '../model/LocaleAlias'

/**
 * Sync Avik locales with an alias. These locales will be set visible in Avik tools
 */
export const syncLocales = async () => {
  await httpclient.post('syncLocales')
}

/**
 * Get all locales
 *
 * @returns All locales in Avik scope alias
 */
export const getAllLocales = async (): Promise<Locale[]> => {
  const resp = await httpclient.get('locales', { params: { visible: true } })
  return TypedJSON.parseAsArray(resp.data, Locale)
}

/**
 * Get all locale aliases
 *
 * @returns The list of locale groups
 */
export const getAllAliases = async (): Promise<LocaleAlias[]> => {
  const resp = await httpclient.get('aliases')
  return TypedJSON.parseAsArray(resp.data, LocaleAlias)
}

/**
 * Update locale aliases shown in the Workbench
 *
 * @param aliases - The aliases to be set
 * @returns The list of locale aliases shown in the Workbench
 */
export const updateAvikAlias = async (aliases: string[]): Promise<string[]> => {
  const resp = await httpclient.post('aliases', aliases)
  return resp.data
}

/**
 * Update/Create a base locale
 *
 * @param localeId - The locale id
 * @param baseLocale - The base locale to be updated
 * @returns The updated locale
 */
export const upsertBaseLocale = async (
  localeId: string,
  baseLocale: Locale
): Promise<Locale | undefined> => {
  const resp = await httpclient.post('baselocales', TypedJSON.toPlainJson(baseLocale, Locale), {
    params: { localeId: localeId },
  })
  return TypedJSON.parse(resp.data, Locale)
}

/**
 * Remove the base locale
 *
 * @param localeId - The locale id to be removed
 * @return the locale id to be removed
 */
export const removeBaseLocale = async (localeId: string): Promise<string> => {
  await httpclient.delete('baselocales', { params: { localeId: localeId } })
  return localeId
}
