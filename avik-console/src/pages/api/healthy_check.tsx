import type { NextApiRequest, NextApiResponse } from 'next'

/**
 * Healthy check for Google load balance
 *
 * @param req - The Http request
 * @param res - The Http response
 */
const healthyCheck = (req: NextApiRequest, res: NextApiResponse) => {
  res.status(200).end()
}

export default healthyCheck
