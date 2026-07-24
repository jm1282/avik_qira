import * as React from 'react'
import Collapse from '@mui/material/Collapse'
import Button from '@mui/material/Button'
import IconButton from '@mui/material/IconButton'
import Stack from '@mui/material/Stack'
import TableCell from '@mui/material/TableCell'
import TableRow from '@mui/material/TableRow'
import Tooltip from '@mui/material/Tooltip'
import EditIcon from '@mui/icons-material/Edit'
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown'
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp'

import DetailItem from './detailItem'
import DeleteButton from './deleteButton'
import { buttonStyles } from '../styles/componentStyles'

type TableRowProps = {
  id: string
  open?: boolean
  tableCells: React.ReactNode[]
  children: JSX.Element
  editButtonText?: string
  onEdit?: () => void
  deleteButtonText?: string
  deleteConfirmText?: string
  onDelete?: () => void
  onCollapseChange?: () => void
}

/**
 * The component to show one task details in one row
 *
 * @returns The element of task row
 */
const CollapseTableRow = (props: TableRowProps): JSX.Element => {
  const open = props.open || false

  /**
   * Handle click the collapse button
   */
  const handleClickCollapse = () => {
    if (props.onCollapseChange) {
      props.onCollapseChange()
    }
  }

  return (
    <>
      <TableRow
        sx={{
          '&:last-child td, &:last-child th': { border: 0 },
          '&:hover': { backgroundColor: '#d3d3d37a' },
        }}>
        {props.tableCells.map((cell: React.ReactNode, index: number) => (
          <TableCell key={`${props.id}_${index}`}>{cell}</TableCell>
        ))}
        <TableCell>
          <IconButton size='small' onClick={handleClickCollapse}>
            {open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
          </IconButton>
        </TableCell>
      </TableRow>
      <TableRow>
        <TableCell style={{ paddingBottom: 0, paddingTop: 0 }} colSpan={6}>
          <Collapse in={open}>
            <Stack sx={{ marginTop: '5px', marginBottom: '5px' }}>
              <DetailItem>{props.children}</DetailItem>
              <DetailItem>
                {props.editButtonText ? (
                  <Tooltip title={props.editButtonText}>
                    <Button
                      color='primary'
                      sx={buttonStyles}
                      startIcon={<EditIcon />}
                      onClick={props.onEdit}>
                      {props.editButtonText}
                    </Button>
                  </Tooltip>
                ) : null}
                {props.deleteButtonText ? (
                  <DeleteButton
                    deleteButtonText={props.deleteButtonText}
                    deleteConfirmText={props.deleteConfirmText}
                    onDelete={props.onDelete}
                  />
                ) : null}
              </DetailItem>
            </Stack>
          </Collapse>
        </TableCell>
      </TableRow>
    </>
  )
}

export default CollapseTableRow
