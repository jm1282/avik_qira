import { jsonObject, jsonMember, jsonArrayMember } from 'typedjson'

import Locale from './Locale'

/**
 * The locale group module
 */
@jsonObject
export default class LocaleAlias {
  @jsonMember(String)
  aliasId: string = ''

  @jsonMember(String)
  description?: string

  @jsonArrayMember(String)
  locales: string[] = []

  @jsonMember(Boolean)
  isVisible: boolean = false
}
