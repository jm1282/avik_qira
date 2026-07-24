import * as React from 'react'
import Container from '@mui/material/Container'
import Toolbar from '@mui/material/Toolbar'
import Card from '@mui/material/Card'
import Button from '@mui/material/Button'
import Tabs from '@mui/material/Tabs'
import Tab from '@mui/material/Tab'
import Tooltip from '@mui/material/Tooltip'
import AddTask from '@mui/icons-material/AddTask'
import querystring from 'querystring'

import Search from '../layout/search'
import DataTable from '../components/dataTable'
import { buttonStyles } from '../styles/componentStyles'
import { usePathname, useRouter, useSearchParams } from 'next/navigation'

/**
 * The data page properties
 */
type DataPageProps = {
  columns: string[]
  typeFilters?: { [typeName: string]: any }
  selectedTypeFilter?: any
  children: JSX.Element[]
  total: number
  createItemText?: string
  defaultTextInSearch?: string
  onCreate?: () => void
  onFilteredTextChange?: (filterText: string) => any
  onFilteredTypeChange?: (typeFilterValue: any) => any
}

/**
 * The component to show page round
 *
 * @param props The component properties
 * @returns The element of page round
 */
const DataPage = (props: DataPageProps) => {
  const pathName = usePathname()
  const params = useSearchParams()
  const router = useRouter()
  const {
    columns,
    typeFilters,
    selectedTypeFilter,
    children,
    total,
    createItemText,
    defaultTextInSearch,
    onCreate,
    onFilteredTextChange,
    onFilteredTypeChange,
  } = props
  const [typeFilter, setTypeFilter] = React.useState(selectedTypeFilter)

  /**
   * Renderer the item type in Tab
   *
   * @returns The renderer item types
   */
  const redererTabFilters = () => {
    if (typeFilters) {
      const typeNames = Object.keys(typeFilters)
      return (
        <Tabs value={typeFilter} onChange={handleTypeFilterChange}>
          {typeNames.map(typeName => (
            <Tab key={typeName} label={typeName} value={typeFilters[typeName]} />
          ))}
        </Tabs>
      )
    }
    return null
  }

  /**
   * Handle event to change page count and number
   *
   * @param pageNumber The new page number
   * @param pageCount The new page count
   */
  const handleChangePage = (pageNumber: number, pageCount: number) => {
    handleQueryParamsChange({ pageNumber, pageCount })
  }

  /**
   * Handle event to change filtered text
   *
   * @param text The new filtered text
   */
  const handleFilteredTextChange = (text: string) => {
    if (onFilteredTextChange) {
      const newParams = onFilteredTextChange(text)
      if (newParams) {
        handleQueryParamsChange({ ...newParams, pageNumber: 1 })
      }
    }
  }

  /**
   * Handle event to change the item type filter
   *
   * @param event - The event to change the item type
   * @param selectedOption - The selected option of type
   */
  const handleTypeFilterChange = (event: React.SyntheticEvent, selectedOption: any) => {
    if (onFilteredTypeChange) {
      const newParams = onFilteredTypeChange(selectedOption)
      if (newParams) {
        handleQueryParamsChange({ ...newParams, pageNumber: 1 })
      }
    }
    setTypeFilter(selectedOption)
  }

  /**
   * Handle event to change URL query parameters
   *
   * @param query The parameters changed
   */
  const handleQueryParamsChange = (parameters: any) => {
    const forwardParams: any = {}
    params.forEach((value: string, key: string) => {
      Object.assign(forwardParams, { [key]: value })
    })
    Object.assign(forwardParams, parameters)
    const url = `${pathName}?${querystring.encode(forwardParams)}`
    router.push(url)
    router.forward()
  }

  return (
    <Container maxWidth='xl'>
      <Card>
        <Toolbar sx={{ display: 'flex', justifyContent: 'space-between', flex: 1 }}>
          <Search defaultText={defaultTextInSearch} onChange={handleFilteredTextChange} />
        </Toolbar>
      </Card>
      {createItemText ? (
        <Tooltip title={createItemText}>
          <Button color='primary' sx={buttonStyles} startIcon={<AddTask />} onClick={onCreate}>
            {createItemText}
          </Button>
        </Tooltip>
      ) : null}
      {redererTabFilters()}
      <DataTable columns={columns} total={total} onPageCountChange={handleChangePage}>
        {children}
      </DataTable>
    </Container>
  )
}

export default DataPage
