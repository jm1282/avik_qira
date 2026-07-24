import Paper from '@mui/material/Paper'
import { styled } from '@mui/material/styles'

/**
 * The style component to show details
 *
 * @returns The element of details
 */
const DetailItem = styled(Paper)(({ theme }) => ({
  ...theme.typography.body2,
  boxShadow: 'unset',
  padding: theme.spacing(1),
  textAlign: 'left',
  color: theme.palette.text.secondary,
}))

export default DetailItem
