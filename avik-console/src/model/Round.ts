import { jsonObject, jsonMember, jsonArrayMember } from 'typedjson'

import Task, { convertTaskFromObject } from './Task'
import { uniq } from 'lodash'

/**
 * To filter duplicated languages. It is because there are duplicated locales in staging database.
 *
 * @param languages - The languages in the round
 * @returns The fixed languages list
 */
const filterLanguages = (languages: string[]): string[] => {
  return uniq(languages)
}

/**
 * The round module
 */
@jsonObject
export default class Round {
  @jsonMember(String)
  id?: string

  @jsonMember(String, { name: 'build_label' })
  buildLabel?: string

  @jsonMember(String, { name: 'base_locale' })
  baseLocale?: string

  @jsonMember(String, { name: 'reference_locale' })
  referenceLocale?: string

  @jsonArrayMember(String, { deserializer: filterLanguages })
  locales?: string[]

  @jsonArrayMember(String, { name: 'reference_rounds' })
  referenceRounds?: string[]

  @jsonArrayMember(Task)
  tasks?: Task[]

  @jsonMember(Number)
  status?: number

  @jsonMember(String, { name: 'creation_time' })
  createTime?: string
}

/**
 * Convert an object to a Round
 *
 * @param payload The original object
 * @returns The Round
 */
export const convertRoundFromObject = (payload: Partial<Round>): Round => {
  const instance = new Round()
  instance.id = payload.id
  instance.baseLocale = payload.baseLocale
  instance.buildLabel = payload.buildLabel
  instance.referenceLocale = payload.referenceLocale
  instance.locales = payload.locales
  instance.referenceRounds = payload.referenceRounds
  instance.status = payload.status
  instance.createTime = payload.createTime
  instance.tasks = payload.tasks?.map(task => convertTaskFromObject(task))
  return instance
}
