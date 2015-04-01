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
            'karma-bro',
            'karma-phantomjs-launcher',
            'karma-chrome-launcher',
            'karma-junit-reporter',
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
        junitReporter: {
            outputFile: './target/surefire-reports/TEST-karma.xml',
            suite: ''
        }
    });

};