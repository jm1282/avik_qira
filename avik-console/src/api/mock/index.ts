import { AxiosStatic } from 'axios'
import MockAdapter from 'axios-mock-adapter'

import mockAtmApi from './mockAtmApi'
import mockLocalesApi from './mockLocalesApi'
import mockRoundAPI from './mockRoundAPI'
import mockTaskApi from './mockTaskApi'
import mockUsersApi from './mockUserApi'

/**
 * Mock APIs in order to run project locally
 *
 * @param axios - Axios instance used to send requests
 */
const mainPage = (axios: AxiosStatic) => {
  const mock = new MockAdapter(axios)
  mockUsersApi(mock)
  mockAtmApi(mock)
  mockLocalesApi(mock)
  mockRoundAPI(mock)
  mockTaskApi(mock)
}
export default mainPage
