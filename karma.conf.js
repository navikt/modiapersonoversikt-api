var istanbul = require('browserify-istanbul');

module.exports = function (config) {
    config.set({
        frameworks: ['mocha', 'browserify'],

        files: [
            './src/main/resources/no/nav/sbl/dialogarena/reactkomponenter/**/*-test.js'
        ],

        exclude: [],

        preprocessors: {
            './src/main/resources/no/nav/sbl/dialogarena/reactkomponenter//**/*-test.js': ['browserify', 'coverage']
        },

        reporters: ['progress', 'junit', 'coverage'],

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
            'karma-chrome-launcher',
            'karma-junit-reporter',
            'karma-coverage',
            'browserify-istanbul',
            'karma-mocha'
        ],

        browserify: {
            debug: true,
            transform: [
                'babelify',
                istanbul({ignore: ['**/*-test.js']})
            ]
        },

        coverageReporter: {
            dir: './target/karma-coverage',
            reporters: [
                { type: 'lcovonly' }
            ]
        },
        junitReporter: {
            outputFile: './target/surefire-reports/TEST-karma.xml',
            suite: ''
        }
    });

};