module.exports = function(config){
    config.set({
        basePath: './src/main/resources/no/nav/sbl/dialogarena/reactkomponenter',

        frameworks: ['mocha', 'browserify'],

        files: [
            '**/tests/*.js'
        ],

        exclude: [],

        preprocessors: {
            '**/tests/*.js': ['browserify']
        },

        browserify: {
            debug: true,
            transform: ['reactify']
        },

        reporters: ['progress'],

        colors: true,

        logLevel: config.LOG_INFO,

        browsers: ['Chrome'],

        singleRun: true
    });

};