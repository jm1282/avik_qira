import { TypedJSON } from 'typedjson'

import httpclient from './clientInstance'
import Round from '../model/Round'
import { getItemsTotal } from '../utils'
import { isEmpty } from 'lodash'

/**
 * Get rounds
 *
 * @param filteredRound - The filtered build label
 * @param pageNumber - The page number
 * @param pageCount - The page count
 * @returns The rounds list and total round count
 */
export const getRounds = async (
  filteredRound: string | null,
  pageNumber: number,
  pageCount: number
): Promise<{ rounds: Round[]; total: number }> => {
  const params = {
    page_num: pageNumber <= 1 ? 1 : pageNumber,
    page_size: pageCount,
  }
  if (filteredRound && !isEmpty(filteredRound)) {
    Object.assign(params, { build_label: filteredRound })
  }
  const resp = await httpclient.get('round', { params })
  const total = getItemsTotal(resp)
  return { rounds: TypedJSON.parseAsArray(resp.data, Round), total }
}

/**
 * Update the round
 *
 * @param round - The round to be updated
 * @returns The updated round
 */
export const updateRound = async (round: Round): Promise<Round | undefined> => {
  const resp = await httpclient.post('round', TypedJSON.toPlainJson(round, Round), {
    params: { id: round.id },
  })
  return TypedJSON.parse(resp.data, Round)
}

/**
 * Create new round
 *
 * @param round - The round to be created
 * @returns The created round
 */
export const createRound = async (round: Round): Promise<Round | undefined> => {
  const resp = await httpclient.put('round', TypedJSON.toPlainJson(round, Round))
  return TypedJSON.parse(resp.data, Round)
}

/**
 * Remove the round
 *
 * @param round - The round to be removed
 */
export const removeRound = async (round: Round) => {
  await httpclient.delete('round', { params: { id: round.id } })
}

/**
 * Get all projects
 *
 * @returns The projects list
 */
export const getProjects = async (): Promise<string[]> => {
  const resp = await httpclient.get('round/projects')
  return resp.data
}

/**
 * Get all build labels
 *
 * @returns The projects list
 */
export const getBuildLabels = async (): Promise<string[]> => {
  const resp = await httpclient.get('round/buildLabels')
  return resp.data
}
