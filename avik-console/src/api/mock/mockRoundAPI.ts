import MockAdapter from 'axios-mock-adapter'
import { flatMapDeep, uniq } from 'lodash'

import data from './data/round'

/**
 * Mock Round APIs
 *
 * @param mock - The mock adapter used to simulate API calls
 */
const mockRoundApi = (mock: MockAdapter) => {
  mock.onGet('round').reply(200, data.rounds, {
    'Content-Range': `items 1-${data.rounds.length}/${data.rounds.length}`,
  })
  mock
    .onGet('round/projects')
    .reply(
      200,
      uniq(flatMapDeep(data.rounds, round => round.tasks.map(task => task.projects))).sort()
    )
  mock.onGet('round/buildLabels').reply(
    200,
    data.rounds.map(round => round.build_label)
  )
  mock.onPost('round').reply(200, data.rounds[0])
  mock.onPut('round').reply(200, data.rounds[0])
  mock.onDelete('round').reply(200, data.deleteRoundResponse)
  mock.onGet(/task\/[\w]+\/rounds/).reply(200, data.rounds)
}

export default mockRoundApi
