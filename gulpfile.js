var gulp = require('gulp');
var rename = require('gulp-rename');
var source = require('vinyl-source-stream'); // Used to stream bundle for further handling
var browserify = require('browserify');
var watchify = require('watchify');
var reactify = require('reactify');
var globalShim = require('browserify-global-shim');
var karma = require('karma').server;
var notify = require('gulp-notify');

var config = require('./buildConfig.json');

var browserifyTask = function (isDev, component) {
    console.log('starting browserify with options: ', isDev, component);
    // Our app bundler
    var props = watchify.args;
    props.entries = [component.file];
    props.debug = isDev;
    props.standalone = createStandaloneName(component);
    props.cache = {};
    props.packageCache = {};
    props.fullPaths = isDev;

    var bundler = isDev ? watchify(browserify(props)) : browserify(props);
    bundler.transform(reactify);
    bundler.transform(globalShim.configure(createShimConfigurationFor(component)));

    bundler.ignore('j')

    if (component.name !== config.navReact) {
        bundler.ignore('react');
        bundler.ignore('react');
    }


    function rebundle() {
        var stream = bundler.bundle();
        return stream.on('error', notify.onError({
            title: 'Compile error',
            message: '<%= error.message %>'
        }))
            .pipe(source(capitalize(component.name) + ".js"))
            .pipe(gulp.dest(config.targetPath));
    }

    bundler.on('update', function () {
        var start = new Date();
        console.log('Rebundling: ' + component.name);
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

function toIndexFile(component) {
    return {
        file: config.srcPath + component + '/index.js',
        name: component,
        namespace: config.namespace.base + '.' + config.namespace.components
    };
}

function capitalize(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}

function createShimConfigurationFor(currentComponent) {
    var shimConfig = [];
    if (currentComponent.name !== 'React') {
        var globalLocation = [config.namespace.base, 'React'].join('.');
        shimConfig['react'] = globalLocation;
        shimConfig['React'] = globalLocation;
        shimConfig[config.navReact] = globalLocation;
    }
    config.components.map(toIndexFile).forEach(function(component){
       if (currentComponent.name !== component.name) {
           shimConfig[component.name] = createStandaloneName(component);
       }
    });
    console.log('shim for ' , currentComponent.name, shimConfig);
    return shimConfig;
}

function createStandaloneName(component) {
    return component.namespace + '.' + capitalize(component.name);
}

gulp.task('dev', function () {
    browserifyTask(false, {
        file: config.
            srcPath + 'nav-react.js',
        name: 'React',
        namespace: config.namespace.base
    });
    config.components
        .map(toIndexFile)
        .forEach(browserifyTask.bind(this, true));

    lessTask({
        development: true,
        src: config.srcPath + '**/*.less',
        dest: config.targetPath
    });
});

gulp.task('default', function () {
    browserifyTask(false, {
        file: config.srcPath + 'nav-react.js',
        name: 'React',
        namespace: config.namespace.base
    });
    config.components
        .map(toIndexFile)
        .forEach(browserifyTask.bind(this, false));

    lessTask({
        development: false,
        src: config.srcPath + '**/*.less',
        dest: config.targetPath
    });

    //test();
});

gulp.task('test', function () {
    test();
});

