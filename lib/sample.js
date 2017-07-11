const androidstarters = require('./index.js');

const config = {
  appName: 'SampleApp',
  packageName: 'io.sample.app',
  templateName: 'androidstarters-java',
  props: {
    appPackage: 'io.sample.app'
  }
};

androidstarters(config);
