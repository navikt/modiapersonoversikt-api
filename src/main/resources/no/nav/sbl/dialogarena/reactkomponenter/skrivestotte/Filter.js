/** @jsx React.DOM */
var React = ModiaJS.React;
var KnaggInput = ModiaJS.Components.KnaggInput;

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
            'auto-focus': false //Blir brukt innenfor modal, så la modalen fikse det. :)
        });
        return (
            <div tabIndex="-1" className="filter-container">
                <KnaggInput {...props} />
            </div>
        );
    }
});

module.exports = Filter;
