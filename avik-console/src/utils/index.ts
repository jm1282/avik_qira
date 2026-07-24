import lodash from 'lodash'
import configFile from '../config/config.json'
import { setLoading } from '../store/reducers/LoadingSlice'
import { setSnackbarMessage } from '../store/reducers/SnackbarSlice'
import { AppDispatch } from '../store/store'
import { AxiosResponse } from 'axios'

export const config = {
  basePath: configFile.base_path,
  serverBaseUrl: configFile.server_base_url,
  atmServer: configFile.atm_server,
  userServer: configFile.user_server,
  defaultBaseLocale: 'en-XM',
  authorizedRole: 'avik-admin',
  avikLocaleMappingKey: 'android_avik',
}

/**
 * Validate the property to be created
 *
 * @param property The property value
 * @param options The options to validate the property.
 * Regular expression reg is for validating the property value.
 * Function validate is the custom validation function on the property value
 * @returns The property is valid or not
 */
export const validateProperty = (
  property?: any,
  options?: {
    reg?: RegExp
    validate?: (property: any) => boolean
  }
): boolean => {
  const reg = options?.reg
  const defaultValidate = reg ? (property: any) => reg.test(property) : undefined
  const validatePropertyValue = options?.validate || defaultValidate
  if (validatePropertyValue) {
    const value = lodash.isString(property) ? property.trim() : property
    return validatePropertyValue(value)
  }

  const nil = lodash.isNil(property)
  if (!nil) {
    if (lodash.isArray(property) && !lodash.isEmpty(property)) {
      return property.reduce(
        (result: boolean, prop: any) => result && validateProperty(prop, options),
        true
      )
    }
    if (lodash.isObject(property)) {
      if (lodash.isEmpty(property)) {
        return false
      }
    }
  }
  return !nil
}

/**
 * Validate the object to be created
 *
 * @param object The object to be validated
 * @param requireProperties The required propertys and options to validate the property
 * @returns The object is valid or not
 */
export const validateObject = (
  object: { [propertyName: string]: any },
  requireProperties: {
    propertyName: string
    options?: { reg?: RegExp; validate?: (property: any) => boolean }
  }[]
): boolean =>
  requireProperties.reduce(
    (
      result: boolean,
      current: {
        propertyName: string
        options?: { reg?: RegExp; validate?: (property: any) => boolean }
      }
    ): boolean =>
      validateProperty(lodash.get(object, current.propertyName), current.options) && result,
    true
  )

/**
 * Check whether the object is updated and valid
 *
 * @param object The object to be checked
 * @param minSize The minimum size of the object
 * @param checkedProperties The key properties to be checked and validated
 * @returns The object is updated and valid or not
 */
export const checkObjectUpdatedAndValid = (
  object: any,
  minSize: number = 1,
  checkedProperties?: {
    propertyName: string
    options?: { reg?: RegExp; validate?: (property: any) => boolean }
  }[]
): boolean | null => {
  if (lodash.size(object) < minSize) {
    return null
  }

  const countOfValidValues = Object.values(object).reduce((result: number, itemValue: any) => {
    let value = itemValue
    if (lodash.isString(value)) {
      value = value.trim()
    }

    if (
      !lodash.isEmpty(value) ||
      lodash.isNumber(value) ||
      lodash.isBoolean(value) ||
      lodash.isBuffer(value) ||
      lodash.isArray(value)
    ) {
      return result + 1
    }
    return result
  }, 0)
  const isValid = countOfValidValues === lodash.size(object)

  if (checkedProperties) {
    return (
      isValid &&
      checkedProperties.reduce(
        (
          result: boolean,
          current: {
            propertyName: string
            options?: { reg?: RegExp; validate?: (property: any) => boolean }
          }
        ): boolean => {
          const property = lodash.get(object, current.propertyName)
          const isPropertyUpdated = property ? property.trim() !== '' : false
          return isPropertyUpdated ? validateProperty(property, current.options) && result : result
        },
        true
      )
    )
  }
  return isValid
}

/**
 * Merge the update validation result as the form validation results for updating
 * If there is no field updated, return false
 * If any fields are updated, return their conjunction result
 *
 * @param updateResults The update validation results
 * @returns The form validation result
 */
export const mergeUpdateResults = (...updateResults: (boolean | null)[]): boolean => {
  const finalResult = updateResults.reduce((result, currentValue): boolean | null => {
    if (lodash.isNull(result) && lodash.isNull(currentValue)) {
      return null
    } else if (lodash.isNull(result)) {
      return currentValue
    } else if (lodash.isNull(currentValue)) {
      return result
    } else {
      return result && currentValue
    }
  }, null)
  return finalResult || false
}

/**
 * Wait an async process and reply with snack message
 *
 * @param dispatch The user dispatch function
 * @param process The async process to be run
 * @param options The options of the function
 */
export const waitForReply = async (
  dispatch: AppDispatch,
  process: () => Promise<any | void>,
  options?: {
    successMessage?: string
    errorMessage?: string
  }
) => {
  dispatch(setLoading(true))
  try {
    await process()
    if (options?.successMessage) {
      dispatch(
        setSnackbarMessage({
          severity: 'success',
          message: options?.successMessage,
        })
      )
    }
  } catch (err: any) {
    dispatch(
      setSnackbarMessage({
        severity: 'error',
        message: options?.errorMessage || err.message || err.toString(),
      })
    )
  } finally {
    dispatch(setLoading(false))
  }
}

/**
 * Get items total count from response
 *
 * @param resp The response from server
 * @return The items list total count
 */
export const getItemsTotal = (resp: AxiosResponse<any, any>): number => {
  const contentRange = resp.headers['content-range']
  const total = contentRange?.substring(contentRange.indexOf('/') + 1) || 0
  return Number(total)
}
