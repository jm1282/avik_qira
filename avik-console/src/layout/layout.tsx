import * as React from 'react'
import Head from 'next/dist/shared/lib/head'

import NavigationBar from './navigation'
import Footer from './footer'
import styles from '../styles/Home.module.css'
import { config } from '../utils'

export interface Props {
  children: any
  account?: string | null
}

/**
 * The page layout component
 *
 * @param { children } - The properties of the page
 * @returns The element of the page layout
 */
export default function Layout({ children, account }: Props) {
  return (
    <>
      <Head>
        <title>Avik Console</title>
        <meta name='description' content='Avik Console' />
        <link rel='stylesheet' href='https://fonts.googleapis.com/icon?family=Material+Icons' />
        <link rel='icon' href={`${config.basePath}/favicon.ico`} />
      </Head>
      <main className={styles.main}>
        <NavigationBar account={account} />
        <div className={styles.content} style={{ width: '100%' }}>
          {children}
        </div>
        <Footer />
      </main>
    </>
  )
}
