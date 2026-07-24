import * as React from 'react'
import Switch from '@mui/material/Switch'
import Tooltip from '@mui/material/Tooltip'

export type SwtichWithTooltipProps = {
  tooltip: string
  checked: boolean
  onChange: (checked: boolean) => void
}

/**
 * Component of switch with tooltip
 *
 * @param props The properties of the component
 * @returns The component elements
 */
const SwtichWithTooltip = (props: SwtichWithTooltipProps): JSX.Element => {
  const { tooltip, checked, onChange } = props

  /**
   * Handle the event to click on switch component
   */
  const handleClickOnSwitch = () => {
    if (onChange) {
      onChange(!checked)
    }
  }

  return (
    <Tooltip title={tooltip}>
      <Switch checked={checked} onClick={handleClickOnSwitch} />
    </Tooltip>
  )
}

export default SwtichWithTooltip
