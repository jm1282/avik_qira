/** @type {import('next').NextConfig} */
module.exports = {
  basePath: '/avik-console',
  reactStrictMode: true,
  rewrites() {
    return [{ source: `/`, destination: `/rounds` }]
  },
}
