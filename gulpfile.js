var gulp = require('gulp');
var rename = require('gulp-rename');
var source = require('vinyl-source-stream'); // Used to stream bundle for further handling
var browserify = require('browserify');
var watchify = require('watchify');
var reactify = require('reactify');
var notify = require('gulp-notify');
var gutil = require('gulp-util');

var srcPath = './src/main/resources/no/nav/sbl/dialogarena/reactkomponenter/';
var targetPath = './target/classes/no/nav/sbl/dialogarena/reactkomponenter/';
var components = [
    'skrivestotte'
];

var browserifyTask = function (options) {

    // Our app bundler
    var appBundler = browserify({
        entries: [options.src],
        transform: [reactify],
        debug: options.development,
        cache: {}, packageCache: {}, fullPaths: options.development
    });

    // The rebundle process
    var rebundle = function () {
        var start = Date.now();
        console.log('Building APP bundle');
        appBundler.bundle()
            .on('error', gutil.log)
            .pipe(source(options.name))
            .pipe(gulp.dest(options.dest))
            .pipe(notify(function () {
                console.log('APP bundle built in ' + (Date.now() - start) + 'ms to ' + options.name);
            }));
    };

    // Fire up Watchify when developing
    if (options.development) {
        appBundler = watchify(appBundler);
        appBundler.on('update', rebundle);
    }

    rebundle();
};

var lessTask = function (options) {
    var run = function () {
        gulp.src(options.src)
            .pipe(rename({dirname: ''}))
            .pipe(gulp.dest(options.dest));
    };

    run();

    if (options.development) {
        gulp.watch(options.src, run);
    }
};

gulp.task('dev', function () {
    toAbsolutePath(components).forEach(function (component) {
        browserifyTask({
            development: true,
            src: component.path,
            dest: targetPath + 'build',
            name: component.name
        });
    });

    lessTask({
        development: true,
        src: srcPath + '**/*.less',
        dest: targetPath + 'build'
    });
});

gulp.task('default', function () {
    toAbsolutePath(components).forEach(function (component) {
        browserifyTask({
            development: false,
            src: component.path,
            dest: targetPath + 'build',
            name: component.name
        });
    });

    lessTask({
        development: false,
        src: srcPath + '**/*.less',
        dest: targetPath + 'build'
    });
});

function toAbsolutePath(componentNames) {
    return componentNames.map(function (componentName) {
        console.log('Component name: ', componentName+'.js');
        return {
            path: srcPath + componentName + '/index.js',
            name: componentName + '.js'
        };
    });
}

