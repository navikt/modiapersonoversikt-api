/** @jsx React.DOM */
var React = require('react');
var KnaggInput = require('knagginput');

var Filter = React.createClass({
    render: function () {
        var props = $.extend({}, this.props, {
            'placeholder': 'Søk',
            'aria-label': 'Søk etter hjelpetekster',
            'aria-controls': 'tekstListePanel',
            'auto-focus': false //Blir brukt innenfor modal, så la modalen fikse det. :)
        });
        return (
            <KnaggInput {...props} />
        );
    }
});

module.exports = Filter;
