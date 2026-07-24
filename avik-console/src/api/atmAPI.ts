import httpclient from './clientInstance'
import { config } from '../utils'

/**
 * Get all ATMs
 *
 * @returns All ATMs
 */
export const getAllATMs = async (): Promise<string[]> => {
  const resp = await httpclient.get('getatmlist', { baseURL: config.atmServer })
  return resp.data.map((atm: any) => atm.atmname)
}
