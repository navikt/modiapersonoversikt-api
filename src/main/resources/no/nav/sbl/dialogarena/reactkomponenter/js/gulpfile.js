var gulp = require('gulp');
var source = require('vinyl-source-stream'); // Used to stream bundle for further handling
var browserify = require('browserify');
var watchify = require('watchify');
var reactify = require('reactify');
var gulpif = require('gulp-if');
var uglify = require('gulp-uglify');
var streamify = require('gulp-streamify');
var notify = require('gulp-notify');
var concat = require('gulp-concat');
var less = require('gulp-less');
var cssmin = require('gulp-cssmin');
var gutil = require('gulp-util');
var shell = require('gulp-shell');
var glob = require('glob');
var livereload = require('gulp-livereload');

// External dependencies you do not want to rebundle while developing,
// but include in your application deployment
var dependencies = [
    'react',
    'react-addons'
];

var browserifyTask = function (options) {

    // Our app bundler
    var appBundler = browserify({
        entries: [options.src], // Only need initial file, browserify finds the rest
        transform: [reactify], // We want to convert JSX to normal javascript
        debug: options.development, // Gives us sourcemapping
        cache: {}, packageCache: {}, fullPaths: options.development // Requirement of watchify
    });

    // We set our dependencies as externals on our app bundler when developing
    //(options.development ? dependencies : []).forEach(function (dep) {
    //    appBundler.external(dep);
    //});

    // The rebundle process
    var rebundle = function () {
        var start = Date.now();
        console.log('Building APP bundle');
        appBundler.bundle()
            .on('error', gutil.log)
            .pipe(source('main.js'))
            .pipe(gulpif(!options.development, streamify(uglify())))
            .pipe(gulp.dest(options.dest))
            .pipe(gulpif(options.development, livereload()))
            .pipe(notify(function () {
                console.log('APP bundle built in ' + (Date.now() - start) + 'ms');
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
            .pipe(less())
            .pipe(gulp.dest(options.dest));
    };

    run();

    if (options.development) {
        gulp.watch(options.src, run);
    }
};

// Starts our development workflow
gulp.task('default', function () {

    browserifyTask({
        development: true,
        src: './skrivestotte/index.js',
        dest: './build'
    });

    lessTask({
        development: true,
        src: './less/**/*.less',
        dest: './styles'
    });
});

gulp.task('deploy', function () {

    browserifyTask({
        development: false,
        src: './skrivestotte/index.js',
        dest: './dist'
    });

    lessTask({
        development: false,
        src: './less/**/*.less',
        dest: './styles'
    });
});

