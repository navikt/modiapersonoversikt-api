var gulp = require('gulp');
var rename = require('gulp-rename');
var source = require('vinyl-source-stream'); // Used to stream bundle for further handling
var browserify = require('browserify');
var watchify = require('watchify');
var babelify = require('babelify');
var karma = require('karma').server;
var notify = require('gulp-notify');

var config = require('./buildConfig.json');

var browserifyTask = function (isDev) {
    console.log('starting browserify with options: ', isDev);
    // Our app bundler
    var props = watchify.args;
    props.entries = ['./src/main/resources/no/nav/sbl/dialogarena/reactkomponenter/index.js'];
    props.debug = isDev;
    props.cache = {};
    props.packageCache = {};
    props.fullPaths = isDev;

    var bundler = isDev ? watchify(browserify(props)) : browserify(props);
    bundler.transform(babelify);

    function rebundle() {
        var stream = bundler.bundle();
        return stream.on('error', notify.onError({
            title: 'Compile error',
            message: '<%= error.message %>'
        }))
            .pipe(source("reactkomponenter.js"))
            .pipe(gulp.dest(config.targetPath));
    }

    bundler.on('update', function () {
        var start = new Date();
        rebundle();
        console.log('Rebundled in ' + (new Date() - start) + 'ms');
    });

    return rebundle();
};

var lessTask = function (options) {
    var run = function () {
        console.log('Building LESS');
        gulp.src(options.src)
            .pipe(rename({dirname: ''}))
            .pipe(gulp.dest(options.dest));
        console.log('moved files: ', options.src);
    };

    run();

    if (options.development) {
        gulp.watch(options.src, run);
    }
};

function test() {
    karma.start({
        configFile: __dirname + '/karma.conf.js',
        isSingleRun: true
    });
}

gulp.task('dev', function () {
    browserifyTask(true);

    lessTask({
        development: true,
        src: config.srcPath + '**/*.less',
        dest: config.targetPath
    });
});

gulp.task('default', function () {
    browserifyTask(false);
    lessTask({
        development: false,
        src: config.srcPath + '**/*.less',
        dest: config.targetPath
    });
});

gulp.task('test', function () {
    test();
});