const promisify = require('bluebird').promisify
const exec = require('child_process').exec
const { mkdirSync } = require('fs')
const writeFile = promisify(require('fs').writeFile)
const logger = require('fancy-log')
const { create } = require('tar')
const { getConfigFile, getDeployConfig } = require('./environment')
const avikTarFile = 'avik_console.tgz'

/**
 * Get the config file and the deployment config
 *
 * @param {string} environment - The environment, production or staging
 * @returns The deployment config
 */
async function getConfig(environment) {
  logger.info(`Get configuration of ${environment}...`)
  const configFile = getConfigFile(environment)
  const deployConfig = getDeployConfig(environment)
  const configDir = `./${deployConfig.sourceDir}/config`
  mkdirSync(configDir, { recursive: true })
  await writeFile(`${configDir}/config.json`, JSON.stringify(configFile))
  return deployConfig
}

/**
 * Initialize the destination folder to upload source codes
 *
 * @param {Any} deployConfig - The deployment config
 */
async function initialDestFolder(deployConfig) {
  logger.info('Initialize destination folder...')
  const { destDir } = deployConfig
  await execRemoteCommand(deployConfig, `rm -rf ${destDir}`)
  await execRemoteCommand(deployConfig, `mkdir ${destDir}`)
}

/**
 * Upload the source code to the GCE instance
 *
 * @param {Any} deployConfig - The deploy config
 */
async function uploadFiles2GCE(deployConfig) {
  logger.info('Upload files...')
  const { project, zone, instance, files, destDir, deployDir } = deployConfig
  const tarFile = `build/${avikTarFile}`
  mkdirSync('build', { recursive: true })
  await create({ gzip: true, file: tarFile }, files)
  await execCommand(
    `gcloud compute scp --tunnel-through-iap --project ${project} --zone ${zone} --recurse ${tarFile} ${instance}:${destDir}`
  )
  await execRemoteCommand(
    deployConfig,
    `sudo rm -rf ${files.map(file => `${deployDir}/${file}`).join(' ')}`
  )
  await execRemoteCommand(deployConfig, `sudo tar -xf ${destDir}/${avikTarFile} -C ${deployDir}`)
}

/**
 * Initial deployment folder
 * It is run in the first time to deploy
 *
 * @param {Any} deployConfig - The deployment config
 */
async function initialDeployFolder(deployConfig) {
  logger.info('Initial Deployment...')
  await execRemoteCommand(deployConfig, `sudo rm -rf ${deployConfig.deployDir}`)
  await execRemoteCommand(deployConfig, `sudo mkdir ${deployConfig.deployDir}`)
}

/**
 * Install node modules
 * It is run in the first time to deploy
 *
 * @param {Any} deployConfig - The deployment config
 */
async function install(deployConfig) {
  logger.info('Install the node modules...')
  const { deployDir } = deployConfig
  await execRemoteCommand(deployConfig, `cd ${deployDir}; sudo npm ci --omit=dev`)
}

/**
 * Build the code in the server side
 *
 * @param {Any} deployConfig - The deployment config
 */
async function rebuildInServer(deployConfig) {
  logger.info('Rebuild the new code in the server side...')
  const { deployDir, deployer, deployerGroup } = deployConfig
  await execRemoteCommand(deployConfig, `cd ${deployDir}; sudo rm -rf .next`)
  await execRemoteCommand(deployConfig, `cd ${deployDir}; sudo npm run build`)
  await execRemoteCommand(deployConfig, `sudo chown -R ${deployer}:${deployerGroup} ${deployDir}`)
}

/**
 * Restart the service
 *
 * @param {Any} deployConfig - The deployment config
 */
async function restartService(deployConfig) {
  await execRemoteCommand(deployConfig, `sudo supervisorctl restart ${deployConfig.deployService}`)
}

/**
 * Execute command
 *
 * @param {Any} deployConfig - The deployment config
 * @param {string} command - The command to run
 * @returns The command results
 */
async function execRemoteCommand(deployConfig, command) {
  const { project, zone, instance } = deployConfig
  const cmd = `gcloud compute ssh --tunnel-through-iap --project ${project} --zone ${zone} ${instance} --command "${command}"`
  return await execCommand(cmd)
}

/**
 * The server healthy check after deployment
 *
 * @param {Any} deployConfig - The deployment config
 */
async function healthyCheck(deployConfig) {
  const { server, deployService, port } = deployConfig
  const endpoint = `http://127.0.0.1:${port}/${deployService}`
  logger.info('Check the endpoint in the server')
  const result = await execRemoteCommand(deployConfig, `curl -s -I ${endpoint} | grep HTTP`)
  if (result.trim().includes('HTTP/1.1 200 OK')) {
    logger.info(`The AViK Console is launched. Please try ${server}/${deployService}`)
  } else {
    logger.error(
      `The AVik Console may fail to be launched. Please try ${server}/${deployService} manually`
    )
  }
}

/**
 * Execute command
 *
 * @param {string} command - The command to run
 * @returns The command results
 */
async function execCommand(command) {
  logger.info(`Run command: ${command}`)
  const execProcess = exec(command)
  const resultBuffer = []
  execProcess.stdout.on('data', data => {
    const content = data.trim()
    logger.info(content)
    resultBuffer.push(content)
  })
  execProcess.stderr.on('data', data => {
    const content = data.trim()
    logger.error(content)
    resultBuffer.push(content)
  })
  return await new Promise(resolve => {
    execProcess.once('exit', async () => {
      resolve(resultBuffer.join(''))
    })
  })
}

/**
 * Deploy the tool
 *
 * @param {Function} callback - The callback function
 */
exports.deploy = async callback => {
  const environment = process.env.NODE_ENV
  try {
    const deployConfig = await getConfig(environment)
    await initialDestFolder(deployConfig)
    await uploadFiles2GCE(deployConfig)
    await rebuildInServer(deployConfig)
    await restartService(deployConfig)
    await healthyCheck(deployConfig)
    callback()
  } catch (error) {
    callback(error)
  }
}

/**
 * Deploy the tool in the first time
 *
 * @param {Function} callback - The callback function
 */
exports.deployAll = async callback => {
  const environment = process.env.NODE_ENV
  try {
    const deployConfig = await getConfig(environment)
    await initialDeployFolder(deployConfig)
    await initialDestFolder(deployConfig)
    await uploadFiles2GCE(deployConfig)
    await install(deployConfig)
    await rebuildInServer(deployConfig)
    await restartService(deployConfig)
    await healthyCheck(deployConfig)
    callback()
  } catch (error) {
    callback(error)
  }
}

/**
 * Create configuration for development environment
 *
 * @param {Function} callback - The callback function
 */
exports.createDevConfig = async callback => {
  try {
    if (process.env.NODE_ENV !== 'development') {
      throw new Error('Dev config is supposed to be used under dev environment only')
    }
    // Use staging configuration in dev environment
    const deployConfig = await getConfig('staging')
    callback()
  } catch (error) {
    callback(error)
  }
}
