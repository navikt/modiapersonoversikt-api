/** @jsx React.DOM */
var React = ModiaJS.React;
var KnaggInput = require('./../knagginput');

var Filter = React.createClass({
    render: function () {
        var props = $.extend({}, this.props, {
            'fritekst': this.props.sokTekst,
            'knagger': this.props.knagger,
            'placeholder': 'Søk',
            'onChange': this.props.sok,
            'onKeyDown': this.props.sokNavigasjon,
            'aria-label': 'Søk etter hjelpetekster',
            'aria-controls': 'tekstListePanel',
            'auto-focus': true
        });
        return (
            <div className="filter-container">
                <KnaggInput {...props} />
            </div>
        );
    }
});

module.exports = Filter;
