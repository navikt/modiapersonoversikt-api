// Vår egen polyfill for console
((function consolepolyfill(con = {}) {
    if (con) {
        return;
    }

    const empty = {};
    const dummy = function dummy() {};
    const properties = 'memory'.split(',');
    const methods = ('assert,clear,count,debug,dir,dirxml,error,exception,group,' +
    'groupCollapsed,groupEnd,info,log,markTimeline,profile,profiles,profileEnd,' +
    'show,table,time,timeEnd,timeline,timelineEnd,timeStamp,trace,warn').split(',');
    properties.forEach((prop) => {
        con[prop] = empty; // eslint-disable-line no-param-reassign
    });
    methods.forEach((method) => {
        con[method] = dummy; // eslint-disable-line no-param-reassign
    });
    window.console = con;
})(window.console));

module.exports = require('react');
