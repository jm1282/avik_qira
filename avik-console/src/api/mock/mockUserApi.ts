import MockAdapter from 'axios-mock-adapter'

import { config } from '../../utils'
import data from './data/user'

/**
 * Mock users APIs
 *
 * @param mock - The mock adapter used to simulate API calls
 */
const mockUserApi = (mock: MockAdapter) => {
  const baseUrl = config.userServer
  mock.onGet(`${baseUrl}/user`).reply(200, data.mocked_user)
}

export default mockUserApi
