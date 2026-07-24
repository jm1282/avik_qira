import { SxProps } from '@mui/system'

export const dialogStyles: SxProps = {
  '&.MuiFormControl-root': { m: 1 },
}

export const chipGroupStyles: SxProps = {
  display: 'flex',
  flexWrap: 'wrap',
  gap: 0.5,
}

export const buttonStyles: SxProps = {
  m: 2,
}

export const buttonPressed: SxProps = {
  borderBottomStyle: 'solid',
  borderBottomWidth: 1,
}

export const selectedItem: SxProps = {
  '&.Mui-selected': {
    backgroundColor: '#add8e6',
    '&:hover': {
      backgroundColor: '#00bfff',
    },
  },
}
