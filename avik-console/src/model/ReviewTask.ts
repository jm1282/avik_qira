import { jsonObject, jsonMember, AnyT, jsonArrayMember } from 'typedjson'

import { TaskType } from './Task'

/**
 * The review task module. It is shown in the review app
 */
@jsonObject
export default class ReviewTask {
  @jsonMember(String)
  id?: string

  @jsonMember(String)
  label?: string

  @jsonMember(Number)
  type?: TaskType

  @jsonMember(String)
  atm?: string

  @jsonMember(Number)
  status?: number

  @jsonArrayMember(String, { serializer: () => null })
  apps?: string[] = []

  @jsonArrayMember(String, { serializer: () => null })
  locales?: string[] = []

  @jsonArrayMember(String, { name: 'hidden_apps', serializer: () => null })
  hiddenApps?: string[] = []

  @jsonArrayMember(String, { name: 'hidden_locales', serializer: () => null })
  hiddenLocales?: string[] = []

  @jsonMember(Boolean, { name: 'string_link' })
  stringLink?: boolean = false

  @jsonMember(String)
  createtime?: string

  @jsonMember(String)
  modtime?: string

  /**
   * To set the options property
   * The options property is for serialization/deserialization only.
   * Please not set/get it.
   *
   * @param value - the option value to be set
   */
  @jsonMember(AnyT)
  set options(value: any) {
    if (value) {
      this.apps = value.apps
      this.locales = value.locales
      this.hiddenApps = value.hidden_apps
      this.hiddenLocales = value.hidden_locales
    }
  }
  get options(): any {
    return {
      apps: this.apps || null,
      locales: this.locales || null,
      hidden_apps: this.hiddenApps || null,
      hidden_locales: this.hiddenLocales || null,
    }
  }
}

/**
 * Convert an object to a ReviewTask
 *
 * @param payload The original object
 * @returns The ReviewTask
 */
export const convertReviewTaskFromObject = (payload: Partial<ReviewTask>): ReviewTask => {
  const instance = new ReviewTask()
  instance.id = payload.id
  instance.label = payload.label
  instance.type = payload.type
  instance.atm = payload.atm
  instance.status = payload.status
  instance.locales = payload.locales
  instance.apps = payload.apps
  instance.hiddenLocales = payload.hiddenLocales
  instance.hiddenApps = payload.hiddenApps
  instance.stringLink = payload.stringLink
  instance.createtime = payload.createtime
  instance.modtime = payload.modtime
  return instance
}
