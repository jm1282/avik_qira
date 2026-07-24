import { find, isEmpty, sortBy } from 'lodash'
import Locale from '../model/Locale'

/**
 * Check if the locale is in current locales from server
 *
 * @param motoLocale - The locale to be checked
 * @param allLocales - The locale entity list
 * @returns The locale with label "Invisible" if it is not exists
 */
export const checkLocale = (motoLocale: string, allLocales: Locale[]): string => {
  if (isEmpty(allLocales)) {
    return motoLocale
  }

  const foundLocale = find(allLocales, ['mapping', motoLocale])
  return foundLocale?.mapping || `${motoLocale} (Removed)`
}

/**
 * Sort the locale by the locale mapping
 *
 * @param locales The locales list to be sorted
 * @returns The sorted locales list
 */
export const sortLocalesByMapping = (locales: Locale[]): Locale[] => {
  return sortBy(locales, [locale => locale.mapping])
}
