const NodePolyfillPlugin = require('node-polyfill-webpack-plugin');
const path = require('path');
const ModuleScopePlugin = require('react-dev-utils/ModuleScopePlugin');

module.exports = function override(config) {
  config.plugins = (config.plugins || []).concat([
    new NodePolyfillPlugin()
  ]);
  config.resolve.alias['@'] = path.join(__dirname, 'src');
  config.resolve.plugins = config.resolve.plugins.filter(plugin => !(plugin instanceof ModuleScopePlugin));
  return config;
};