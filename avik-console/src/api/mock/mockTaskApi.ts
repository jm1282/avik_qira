import MockAdapter from 'axios-mock-adapter'

import data from './data/task'

/**
 * Mock task APIs
 *
 * @param mock - The mock adapter used to simulate API calls
 */
const mockTaskApi = (mock: MockAdapter) => {
  mock.onGet('tasks').reply(200, data.tasks, {
    'Content-Range': `items 1-${data.tasks.length}/${data.tasks.length}`,
  })
  mock.onPost('task').reply(200, data.tasks[0])
  mock.onPut('task').reply(200, data.tasks[0])
  mock.onDelete('task').reply(200, data.deleteTaskResponse)
}

export default mockTaskApi
