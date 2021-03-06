# androidstarters [![NPM version][npm-image]][npm-url] [![Build Status][travis-image]][travis-url] [![Dependency Status][daviddm-image]][daviddm-url] [![Coverage percentage][coveralls-image]][coveralls-url]
> Kickstart your next android app in 10 secs.

## Installation

```sh
$ npm install --save androidstarters
```

## Usage

```js
const androidstarters = require('androidstarters');

const config = {
  appName: 'SampleApp',
  packageName: 'io.sample.app',
  templateName: 'kotlin',
  props: {
    appPackage: 'io.sample.app'
  }
};

androidstarters(config);
```
## License

MIT © [Ravindra Kumar]()


[npm-image]: https://badge.fury.io/js/androidstarters.svg
[npm-url]: https://npmjs.org/package/androidstarters
[travis-image]: https://travis-ci.org/androidstarters/androidstarters.svg?branch=master
[travis-url]: https://travis-ci.org/androidstarters/androidstarters
[daviddm-image]: https://david-dm.org/androidstarters/androidstarters.svg?theme=shields.io
[daviddm-url]: https://david-dm.org/androidstarters/androidstarters
[coveralls-image]: https://coveralls.io/repos/ravidsrk/androidstarters/badge.svg
[coveralls-url]: https://coveralls.io/r/ravidsrk/androidstarters
