import styles from '../styles/Home.module.css'

/**
 * The footer component of the page
 *
 * @returns The element of the footer
 */
export default function Footer() {
  return (
    <footer className={styles.footer}>
      <a href='mailto: l10ntools@motorola.com' target='_blank' rel='noreferrer'>
        Powered by Motorola G11n PROME Team
      </a>
    </footer>
  )
}
