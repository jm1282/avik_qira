const lodash = require('lodash')

const DeployConfig = {
  staging: {
    server: 'https://sher.blurdev.com',
    project: 'alk-staging-ca101',
    zone: 'us-central1-b',
    instance: 'avik',
    port: 2022,
    files: [
      'public',
      'src',
      '.eslintrc.json',
      'next-env.d.ts',
      'next.config.js',
      'package.json',
      'package-lock.json',
      'tsconfig.json',
    ],
    sourceDir: 'src',
    destDir: './avik-console',
    deployDir: '/opt/app/avik-console',
    deployService: 'avik-console',
    deployer: 'motoapp',
    deployerGroup: 'deployers',
  },
  production: {
    server: 'https://lion.motorola.com',
    project: 'mm-alk',
  },
}

/**
 * Get the deployment configuration by environment
 *
 * @param {string} environment - The environment
 * @returns The configuration
 */
exports.getDeployConfig = environment => {
  return lodash.merge(DeployConfig.staging, DeployConfig[environment])
}

/**
 * Get the config file by environment
 *
 * @param {string} environment - The environment
 * @returns The config file
 */
exports.getConfigFile = environment => {
  const server = DeployConfig[environment].server
  return {
    base_path: '/avik-console',
    server_base_url: `${server}/avik3-api`,
    atm_server: `${server}/atm`,
    user_server: `${server}/user-table-api`,
  }
}
