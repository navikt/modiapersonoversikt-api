/** @jsx React.DOM */
var React = require('react');
var Tekstforslag = require('./Tekstforslag.js');

(function () {
    window.ModiaJS = {};

    window.ModiaJS.React = React;

    window.ModiaJS.Components = {};
    window.ModiaJS.InitializedComponents = {};

    window.ModiaJS.Components.Tekstforslag = Tekstforslag;
})();
