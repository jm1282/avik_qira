import MockAdapter from 'axios-mock-adapter'

import data from './data/locales'

/**
 * Mock Avik3 mapper APIs
 *
 * @param mock - The mock adapter used to simulate API calls
 */
const mockLocalesApi = (mock: MockAdapter) => {
  mock.onGet('locales').reply(200, data.locales)
  mock.onGet('aliases').reply(200, data.aliases)
  mock.onPost('aliases').reply(200, data.avikAliases)
  mock.onPost('baselocales').reply(200, data.addedBaseLocaleResponse)
  mock.onDelete('baselocales').reply(200, data.deletedBaseLocaleResponse)
}

export default mockLocalesApi
