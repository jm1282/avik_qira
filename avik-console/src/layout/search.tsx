import * as React from 'react'
import { styled } from '@mui/material/styles'
import SearchIcon from '@mui/icons-material/Search'
import { Button, TextField } from '@mui/material'
import { get } from 'lodash'

const SearchField = styled('div')(() => ({
  display: 'flex',
  width: '100vw',
  alignContent: 'center',
  alignItems: 'center',
}))

type SearchProps = {
  defaultText?: string
  onChange?: (value: string) => void
}

/**
 * The search component of the page
 *
 * @returns The element of the search
 */
export default function Search(props: SearchProps) {
  const textRef = React.useRef<HTMLInputElement>()

  /**
   * Handle press 'Enter' after input text value
   *
   * @param event The key event
   */
  const handleEnterChange = (event: React.KeyboardEvent | React.MouseEvent) => {
    const key = get(event, 'key')
    if (props.onChange && (!key || key === 'Enter')) {
      props.onChange(textRef.current?.value || '')
    }
  }
  return (
    <SearchField>
      <SearchIcon />
      <TextField
        inputRef={textRef}
        placeholder='Search...'
        defaultValue={props.defaultText}
        fullWidth
        onKeyUp={handleEnterChange}
      />
      <Button onClick={handleEnterChange}>Search</Button>
    </SearchField>
  )
}
