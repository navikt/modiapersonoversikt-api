/* eslint no-var:0, no-console:0 */
var gulp = require('gulp');
var rename = require('gulp-rename');
var source = require('vinyl-source-stream'); // Used to stream bundle for further handling
var browserify = require('browserify');
var watchify = require('watchify');
var babelify = require('babelify');
var karma = require('karma').server;
var notify = require('gulp-notify');
var eslint = require('gulp-eslint');
var chalk = require('chalk');

var config = require('./buildConfig.json');

function bundleJS(isDev) {
    // Our app bundler
    var props;
    var bundler;
    console.log('starting browserify with options: ', isDev);
    props = watchify.args;
    props.entries = ['./src/main/resources/no/nav/sbl/dialogarena/reactkomponenter/reactkomponenter-module.js'];
    props.debug = isDev;
    props.cache = {};
    props.packageCache = {};
    props.fullPaths = isDev;

    bundler = isDev ? watchify(browserify(props)) : browserify(props);
    bundler.transform(babelify);

    function rebundle() {
        var stream;
        stream = bundler.bundle();
        return stream.on('error', notify.onError({
                title: 'Compile error',
                message: '<%= error.message %>'
            }))
            .pipe(source('reactkomponenter.js'))
            .pipe(gulp.dest(config.targetPath));
    }

    bundler.on('update', function onUpdateRebuild() {
        var start = new Date();
        rebundle();
        console.log('Rebundled in ' + (new Date() - start) + 'ms');
    });

    return rebundle();
}

function lessTask(options) {
    function run() {
        console.log('Building LESS');
        gulp.src(options.src)
            .pipe(rename({ dirname: '' }))
            .pipe(gulp.dest(options.dest));
        console.log('moved files: ', options.src);
    }

    run();

    if (options.development) {
        gulp.watch(options.src, run);
    }
}

function test(options) {
    karma.start({
        configFile: __dirname + options.file,
        singleRun: options.singleRun
    });
}

gulp.task('dev', function runDev() {
    bundleJS(true);
    lessTask({
        development: true,
        src: config.srcPath + '**/*.less',
        dest: config.targetPath
    });
});

gulp.task('default', function runDefault() {
    bundleJS(false);
    lessTask({
        development: false,
        src: config.srcPath + '**/*.less',
        dest: config.targetPath
    });
});

gulp.task('test', function runTest() {
    test({ singleRun: true, file: '/karma.conf.js' });
});

gulp.task('tdd', function runTdd() {
    test({ singleRun: false, file: '/karma.conf.local.js' });
});

gulp.task('eslint', function () {
    return gulp.src([config.srcPath + '*.{js,jsx}'])
        .pipe(eslint())
        .pipe(eslint.format())
        .pipe(eslint.failAfterError());
});