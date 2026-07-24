import { jsonObject, jsonMember } from 'typedjson'

/**
 * The locale module
 */
@jsonObject
export default class Locale {
  @jsonMember(String)
  localeId: string = ''

  @jsonMember(String)
  displayName: string = ''

  @jsonMember(String)
  mapping: string = this.localeId

  @jsonMember(Boolean)
  pseudo: boolean = false

  @jsonMember(Boolean)
  isBaseLocale: boolean = false

  isVisible: boolean = true

  /**
   * Getter of label
   *
   * @returns The locale label
   */
  get label(): string {
    return `${this.mapping} - ${this.displayName}`
  }
}
