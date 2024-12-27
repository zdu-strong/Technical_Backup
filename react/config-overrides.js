const NodePolyfillPlugin = require('node-polyfill-webpack-plugin');
const path = require('path');
const ModuleScopePlugin = require('react-dev-utils/ModuleScopePlugin');

module.exports = function override(config) {
  config.plugins = (config.plugins || []).concat([
    new NodePolyfillPlugin({
      additionalAliases: [
        "_stream_duplex",
        "_stream_passthrough",
        "_stream_readable",
        "_stream_transform",
        "_stream_writable",
        "console",
        "domain",
        "process",
        "punycode",
      ]
    })
  ]);
  config.resolve.alias['@'] = path.join(__dirname, 'src');
  config.resolve.plugins = config.resolve.plugins.filter(plugin => !(plugin instanceof ModuleScopePlugin));
  return config;
};