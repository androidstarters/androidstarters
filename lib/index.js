const path = require('path');
const memFs = require('mem-fs');
const editor = require('mem-fs-editor');
const filesystem = require('fs');
const rimraf = require('rimraf');
const mkdirp = require('mkdirp');
const archiver = require('archiver');
const merge = require('lodash.merge');

module.exports = function(config) {
    const templateConfig = require('./config/' + config.templateName + '.json');

    config.packageDir = config.packageName.replace(/\./g, '/');
    config.templatePath = path.join(__dirname, '/templates/' + config.templateName + '/');
    config = merge(config, templateConfig);
    config.appPath = path.join(__dirname, config.appName + '/');
    createDirectories(config);
    copyFiles(config);
};

function createDirectories(config) {
    for (var i = 0; i < config.dirs.length; i++) {
        var prefix = config.dirs[i].appName ? config.appName : '';
        var suffix = config.dirs[i].packageDir ? config.packageDir : '';
        var directoryPath = prefix + config.dirs[i].name + suffix;
        mkdirp(directoryPath);
    }
}

function copyFiles(config) {
    let store = memFs.create();
    let fs = editor.create(store);
    for (var i = 0; i < config.files.length; i++) {
        var file = config.files[i];
        var dot = file.prefixDot ? '.' : '';
        if (file.props) {
            fs.copyTpl(config.templatePath + file.name, config.appPath + dot + file.name, config.props);
        } else {
            fs.copy(config.templatePath + file.name, config.appPath + dot + file.name);
        }
    }

    for (var j = 0; j < config.dirs.length; j++) {
        var directory = config.dirs[j];
        if (directory.packageDir) {
            fs.copyTpl(config.templatePath + directory.name + config.defaultPackageDir, config.appPath + directory.name + config.packageDir, config.props);
        }
    }

    fs.commit(function() {
        var output = filesystem.createWriteStream(config.appName + '.zip');
        var archive = archiver('zip', {
            store: true // Sets the compression method to STORE.
        });

        output.on('close', function() {
            console.log(archive.pointer() + ' total bytes');
            console.log('archiver has been finalized and the output file descriptor has closed.');
            rimraf.sync(path.join(__dirname, config.appName));
        });

        archive.on('error', function(err) {
            console.log(err);
        });

        archive.pipe(output);
        archive.directory(config.appName, false);
        archive.finalize();
    });
}