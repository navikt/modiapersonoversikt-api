var istanbul = require('browserify-istanbul');

module.exports = function (config) {
    config.set({
        frameworks: ['mocha', 'browserify', 'intl-shim'],

        files: [
            './src/main/resources/no/nav/sbl/dialogarena/reactkomponenter/**/*-test.js'
        ],

        exclude: [],

        preprocessors: {
            './src/main/resources/no/nav/sbl/dialogarena/reactkomponenter//**/*-test.js': ['browserify']
        },

        reporters: ['mocha'],

        colors: true,

        logLevel: config.LOG_INFO,

        browsers: ['PhantomJS'],

        singleRun: true,
        captureTimeout: 60000,

        //For å hinde disconnect melding når det bygges
        browserDisconnectTimeout: 10000, //Default 2000
        browserDisconnectTolerance: 1, //Default 0
        browserNoActivityTimeout: 60000, //Default 10000


        plugins: [
            'karma-browserify',
            'karma-phantomjs-launcher',
            'karma-mocha-reporter',
            'karma-mocha',
            'karma-intl-shim'
        ],

        browserify: {
            debug: true,
            transform: [
                'babelify'
            ]
        }
    });

};