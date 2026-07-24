import * as React from 'react'
import FormControl from '@mui/material/FormControl'
import Paper from '@mui/material/Paper'

import { Button } from '@mui/material'

/**
 * The properties of the component SyncLocalesEdit
 *
 * @property onSync - The function to run when syncing locales
 */
type SyncLocalesEditProps = {
  onSync?: () => void
}

/**
 * The component to sync locales in Avik scope
 *
 * @returns The element to sync locales in Avik scope
 */
const SyncLocalesEdit = (props: SyncLocalesEditProps): JSX.Element => {
  /**
   * Handle the sync locales event
   *
   */
  const handleSyncLocales = () => {
    if (props.onSync) {
      props.onSync()
    }
  }

  return (
    <Paper elevation={0} sx={{ m: 5 }}>
      <FormControl fullWidth component='div'>
        <Button fullWidth variant='contained' onClick={handleSyncLocales}>
          Sync Locales with Locale Mapper
        </Button>
      </FormControl>
    </Paper>
  )
}

export default SyncLocalesEdit
