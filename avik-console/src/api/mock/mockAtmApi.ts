import MockAdapter from 'axios-mock-adapter'

import { config } from '../../utils'
import data from './data/atm'

/**
 * Mock atm APIs
 *
 * @param mock - The mock adapter used to simulate API calls
 */
const mockAtmApi = (mock: MockAdapter) => {
  const baseUrl = config.atmServer
  mock.onGet(`${baseUrl}/getatmlist`).reply(200, data.atms)
}

export default mockAtmApi
