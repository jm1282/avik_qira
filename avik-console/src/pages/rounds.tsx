import type { NextPage } from 'next'
import * as React from 'react'
import { useSearchParams } from 'next/navigation'

import Round from '../model/Round'
import DataPage from '../components/dataPage'
import RoundEdit from '../components/rounds/roundEdit'
import RoundRow from '../components/rounds/roundRow'
import { loadBuildLabels, loadRounds, listRounds, loadProjects } from '../store/reducers/RoundSlice'
import { loadAllLocales, loadAllAliases } from '../store/reducers/LocaleSlice'
import { loadAtms } from '../store/reducers/AtmSlice'
import { useAppSelector, useAppDispatch } from '../store/hooks'
import { waitForReply } from '../utils'

const columns = ['Build Label', 'Base Locale', 'Tasks', 'Create Time', 'Status']

/**
 * The component to show page round
 *
 * @returns The element of page round
 */
const Rounds: NextPage<void> = () => {
  const params = useSearchParams()
  const pageNumber = Number(params.get('pageNumber') || 1)
  const pageCount = Number(params.get('pageCount') || 10)
  const filteredRound = params.get('round')
  const [openRow, setOpenRow] = React.useState<string>()
  const [openCreateRound, setOpenCreateRound] = React.useState<boolean>(false)
  const [rounds, roundCount] = useAppSelector(listRounds)
  const dispatch = useAppDispatch()

  React.useEffect(() => {
    const loadingData = async () => {
      await dispatch(loadRounds({ filteredRound, pageNumber, pageCount }))
    }
    waitForReply(dispatch, loadingData)
  }, [dispatch, filteredRound, pageNumber, pageCount])

  React.useEffect(() => {
    const loadingData = async () => {
      await Promise.all([
        dispatch(loadAtms()),
        dispatch(loadBuildLabels()),
        dispatch(loadProjects()),
        dispatch(loadAllLocales()),
        dispatch(loadAllAliases()),
      ])
    }
    waitForReply(dispatch, loadingData)
  }, [dispatch])

  /**
   * Handle event to change the text filter
   *
   * @param value The round build label to search
   * @return The updated query params
   */
  const handleFilteredTextChange = (value: string) => ({ round: value })

  return (
    <>
      <DataPage
        columns={columns}
        createItemText='Create Round'
        defaultTextInSearch={filteredRound || ''}
        onCreate={() => setOpenCreateRound(true)}
        total={roundCount}
        onFilteredTextChange={handleFilteredTextChange}>
        {rounds.map((round: Round) => (
          <RoundRow
            key={round.id || ''}
            round={round}
            open={openRow === round.id}
            onOpenStateChange={(id?: string) => setOpenRow(id)}
          />
        ))}
      </DataPage>
      <RoundEdit open={openCreateRound} onClose={() => setOpenCreateRound(false)} />
    </>
  )
}

export default Rounds
