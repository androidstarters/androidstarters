const androidstarters = require('./lib/index.js');
const merge = require('lodash.merge');
const path = require('path');

var config = {
  appName: 'SampleApp',
  packageName: 'io.sample.app',
  templateName: 'androidstarters-kotlin',
  props: {
    appPackage: 'io.sample.app'
  }
};

const templateConfig = require('./config/' + config.templateName + '.json');

config.packageDir = config.packageName.replace(/\./g, '/');
config.templatePath = path.join(__dirname, '/templates/' + config.templateName + '/');
config = merge(config, templateConfig);
config.appPath = path.join(__dirname, config.appName + '/');

console.log(config);

androidstarters(config);
