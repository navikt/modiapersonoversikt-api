window.ModiaJS = window.ModiaJS || {};
window.ModiaJS.Components = window.ModiaJS.Components || {};
window.ModiaJS.InitializedComponents = window.ModiaJS.InitializedComponents || {};

//Vår egen polyfill for console
(function(con){
    "use strict";
    if (con) {
        return;
    }
    con = {};
    var prop, method;
    var empty = {};
    var dummy = function(){};
    var properties = 'memory'.split(',');
    var methods = ('assert,clear,count,debug,dir,dirxml,error,exception,group,'+
    'groupCollapsed,groupEnd,info,log,markTimeline,profile,profiles,profileEnd,'+
    'show,table,time,timeEnd,timeline,timelineEnd,timeStamp,trace,warn').split(',');
    properties.forEach(function(prop){
        con[prop] = empty;
    });
    methods.forEach(function(method){
        con[method] = dummy;
    });
    window.console = con;
})(window.console);


module.exports = require('react/addons');
