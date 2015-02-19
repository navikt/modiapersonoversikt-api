module.exports = function (config) {
    config.set({
        frameworks: ['mocha', 'browserify'],

        files: [
            './src/main/resources/no/nav/sbl/dialogarena/reactkomponenter/**/tests/*.js'
        ],

        exclude: [],

        preprocessors: {
            '**/tests/*.js': ['browserify', 'coverage']
        },

        reporters: ['progress', 'junit', 'remote', 'coverage'],

        colors: true,

        logLevel: config.LOG_INFO,

        browsers: ['PhantomJS'],

        singleRun: true,
        captureTimeout: 60000,

        plugins: [
            'karma-phantomjs-launcher',
            'karma-chrome-launcher',
            'karma-junit-reporter',
            'karma-remote-reporter',
            'karma-bro',
            'karma-coverage',
            'karma-mocha'
        ],

        browserify: {
            debug: true,
            transform: ['reactify']
        },

        coverageReporter: {
            type: 'cobertura',
            dir: './target/karma-coverage'
        },
        remoteReporter: {
            host: 'localhost',
            port: '9876'

        },
        junitReporter: {
            outputFile: './target/surefire-reports/TEST-karma.xml',
            suite: ''
        }
    });

};