import httpclient from './clientInstance'
import { config } from '../utils'

/**
 * Get user account
 * If the user has no permission, return "Anonymous"
 *
 * @returns The user account or "Anonymous"
 */
export const getUserAccount = async (): Promise<string | null> => {
  const resp = await httpclient.get('user', { baseURL: config.userServer })
  const user = resp.data
  return user.roles.includes(config.authorizedRole) ? user.name : null
}
