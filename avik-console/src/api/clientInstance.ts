import axios, { AxiosInstance } from 'axios'
import https from 'https'

import { config } from '../utils'
import mockServer from './mock'

if (process.env.NODE_ENV === 'development') {
  mockServer(axios)
}

/**
 * Create the default http client
 */
const httpclient: AxiosInstance = axios.create({
  baseURL: config.serverBaseUrl,
  httpsAgent: new https.Agent({
    rejectUnauthorized: false,
  }),
  timeout: 600000,
  withCredentials: true,
  headers: { Accept: 'application/json' },
})

export default httpclient
