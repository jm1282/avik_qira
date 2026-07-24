import React from 'react'
import type { AppProps } from 'next/app'
import { Provider } from 'react-redux'
import { isUndefined } from 'lodash'

import { getUserAccount } from '../api/userAPI'
import Layout from '../layout/layout'
import { store } from '../store/store'
import styles from '../styles/Home.module.css'
import '../styles/globals.css'

/**
 * The component to show the page
 *
 * @param { Component, pageProps } - The properties of the page
 * @returns The page component
 */
function AvikConsole({ Component, pageProps }: AppProps) {
  const [account, setAccount] = React.useState<string | null>()

  React.useEffect(() => {
    getUserAccount().then(account => {
      Object.assign(pageProps, { account: account })
      setAccount(account)
    })
  }, [pageProps])

  return (
    <Provider store={store}>
      <Layout account={account}>
        {account ? (
          <Component {...pageProps} />
        ) : isUndefined(account) ? (
          <div className={styles.info}>Please wait for checking user permissions</div>
        ) : (
          <div className={styles.error}>You have no permission to access the Avik Console</div>
        )}
      </Layout>
    </Provider>
  )
}

export default AvikConsole
