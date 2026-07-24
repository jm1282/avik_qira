import * as React from 'react'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableContainer from '@mui/material/TableContainer'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import TablePagination from '@mui/material/TablePagination'
import { useSearchParams } from 'next/navigation'
import { isEmpty } from 'lodash'

type DataTableProps = {
  columns: string[]
  children: JSX.Element[]
  total?: number
  onPageCountChange?: (pageNumber: number, pageCount: number) => void
}

/**
 * The component to show data table with pagination
 *
 * @returns The element of data table
 */
const DataTable = (props: DataTableProps) => {
  const { columns, children, total, onPageCountChange } = props
  const params = useSearchParams()
  const pageNumber = Number(params.get('pageNumber') || 1)
  const pageCount = Number(params.get('pageCount') || 10)

  /**
   * Handle the event to change the page number
   *
   * @param newPage - The new page number
   */
  const handleChangePageNumber = (
    _: React.MouseEvent<HTMLButtonElement> | null,
    newPage: number
  ) => {
    const showPage = newPage + 1
    if (onPageCountChange) {
      onPageCountChange(showPage, pageCount)
    }
  }

  /**
   * Handle the event to change the page count
   *
   * @param event - The event to change the page count
   */
  const handleChangePageCount = (
    event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const newPageCount = parseInt(event.target.value, 10)
    if (onPageCountChange) {
      onPageCountChange(1, newPageCount)
    }
  }

  return (
    <>
      <TableContainer sx={{ marginTop: '5px' }}>
        <Table stickyHeader>
          <TableHead>
            <TableRow>
              {columns.map(column => (
                <TableCell key={column} variant='head' sx={{ fontWeight: 600 }}>
                  {column}
                </TableCell>
              ))}
              <TableCell />
            </TableRow>
          </TableHead>
          <TableBody>
            {isEmpty(children) ? (
              <TableRow>
                <TableCell colSpan={columns.length + 1}>No items found in the server</TableCell>
              </TableRow>
            ) : (
              children
            )}
          </TableBody>
        </Table>
      </TableContainer>
      <TablePagination
        component='div'
        count={total || pageNumber * pageCount}
        page={pageNumber - 1}
        onPageChange={handleChangePageNumber}
        rowsPerPage={pageCount}
        onRowsPerPageChange={handleChangePageCount}
      />
    </>
  )
}

export default DataTable
