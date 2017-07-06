const androidstarters = require('./index.js');

const config = {
  appName: 'SampleApp',
  packageName: 'io.sample.app',
  templateName: 'kotlin',
  props: {
    appPackage: 'io.sample.app'
  }
};

androidstarters(config);
