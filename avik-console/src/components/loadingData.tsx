import Box from '@mui/material/Box'
import LinearProgress from '@mui/material/LinearProgress'

/**
 * The component to show process of loading data
 *
 * @returns - The element to show process of loading data
 */
const LoadingData = ({ open }: { open?: boolean }): JSX.Element => {
  return open ? (
    <Box sx={{ width: '100%' }}>
      <LinearProgress />
    </Box>
  ) : (
    <></>
  )
}

export default LoadingData
