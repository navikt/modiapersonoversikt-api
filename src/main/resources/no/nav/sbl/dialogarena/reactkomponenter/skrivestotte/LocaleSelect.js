/** @jsx React.DOM */
var React = require('react');

var spraak = {
    'nb_NO': 'Norsk (Bokmål)',
    'nn_NO': 'Norsk (Nynorsk)',
    'en_US': 'Engelsk'
};
var LocaleSelect = React.createClass({
    onChange: function (event) {
        this.props.setValgtLocale(event.nativeEvent.target.value);
    },
    render: function () {
        return Object.keys(this.props.valgtTekst.innhold).length > 1 ? (
            <select onChange={this.onChange} value={this.props.valgtLocale}>
            {Object.keys(this.props.valgtTekst.innhold).map(function (locale) {
                return <option value={locale}>{spraak[locale] ? spraak[locale] : locale}</option>;
            }.bind(this))}
            </select>
        ) : null;
    }
});

module.exports = LocaleSelect;

