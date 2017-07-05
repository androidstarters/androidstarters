const path = require('path');
const mkdirp = require('mkdirp');
const memFs = require('mem-fs');
const editor = require('mem-fs-editor');
const filesystem = require('fs');
const os = require('os');
const rimraf = require('rimraf');
const archiver = require('archiver');

function createDirs(appName, packageName) {

    var packageDir = packageName.replace(/\./g, '/');

    mkdirp(appName);
    mkdirp(appName + '/app');
    mkdirp(appName + '/app/src/main/assets');
    mkdirp(appName + '/app/src/main/java/' + packageDir);
    mkdirp(appName + '/app/src/androidTest/java/' + packageDir);
    mkdirp(appName + '/app/src/commonTest/java/' + packageDir);
    mkdirp(appName + '/app/src/debug');
    mkdirp(appName + '/app/src/release');
    mkdirp(appName + '/app/src/test/resources');
    mkdirp(appName + '/app/src/test/java/' + packageDir);
}

function copyDirs(appName, packageName) {
    let store = memFs.create();
    let fs = editor.create(store);

    let props = {
        appPackage: packageName
    }

    let packageDir = packageName.replace(/\./g, '/');

    let templatePath = path.join(__dirname, '/templates/template-kotlin/');
    let appPath = path.join(__dirname, + appName + "/");

    fs.copy(templatePath + 'gitignore', appPath + '.gitignore');
    fs.copy(templatePath + 'build.gradle', appPath + 'build.gradle');
    fs.copy(templatePath + 'gradle.properties', appPath + 'gradle.properties');
    fs.copy(templatePath + 'gradlew', appPath + 'gradlew');
    fs.copy(templatePath + 'gradlew.bat', appPath + 'gradlew.bat');
    fs.copy(templatePath + 'settings.gradle', appPath + 'settings.gradle');
    fs.copy(templatePath + 'app/gitignore', appPath + 'app/.gitignore');
    fs.copy(templatePath + 'app/lint.xml', appPath + 'app/lint.xml');
    fs.copy(templatePath + 'app/dependencies.gradle', appPath + 'app/dependencies.gradle');
    fs.copy(templatePath + 'app/proguard-rules.pro', appPath + 'app/proguard-rules.pro');

    fs.copy(templatePath + 'gradle', appPath + 'gradle');
    fs.copy(templatePath + 'app/src/main/res', appPath + 'app/src/main/res');
    fs.copy(templatePath + 'app/src/test/resources', appPath + 'app/src/test/resources');

    fs.copyTpl(templatePath + 'README.md', appPath + 'README.md', props);
    fs.copyTpl(templatePath + 'app/build.gradle', appPath + 'app/build.gradle', props);
    fs.copyTpl(templatePath + 'app/src/androidTest/java/io/mvpstarter/sample', appPath + 'app/src/androidTest/java/' + packageDir, props);
    fs.copyTpl(templatePath + 'app/src/commonTest/java/io/mvpstarter/sample', appPath + 'app/src/commonTest/java/' + packageDir, props);
    fs.copyTpl(templatePath + 'app/src/debug/AndroidManifest.xml', appPath + 'app/src/debug/AndroidManifest.xml', props);
    fs.copyTpl(templatePath + 'app/src/debug/res', appPath + 'app/src/debug/res', props);
    fs.copyTpl(templatePath + 'app/src/main/AndroidManifest.xml', appPath + 'app/src/main/AndroidManifest.xml', props);
    fs.copyTpl(templatePath + 'app/src/main/java/io/mvpstarter/sample', appPath + 'app/src/main/java/' + packageDir, props);
    fs.copyTpl(templatePath + 'app/src/main/res/layout', appPath + 'app/src/main/res/layout', props);
    fs.copyTpl(templatePath + 'app/src/release/res', appPath + 'app/src/release/res', props);
    fs.copyTpl(templatePath + 'app/src/test/java/io/mvpstarter/sample', appPath + 'app/src/test/java/' + packageDir, props);

    fs.commit(function() {
        var output = filesystem.createWriteStream(appName + '.zip');
        var archive = archiver('zip', {
            store: true // Sets the compression method to STORE.
        });

        output.on('close', function() {
            console.log(archive.pointer() + ' total bytes');
            console.log('archiver has been finalized and the output file descriptor has closed.');
            rimraf.sync(path.join(__dirname, appName));
        });

        archive.on('error', function(err) {
            console.log(err);
        });

        archive.pipe(output);
        archive.directory(appName, false);
        archive.finalize();
    })
}


createDirs("SampleApp", "io.sample.app")
copyDirs("SampleApp", "io.sample.app")