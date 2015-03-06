/** @jsx React.DOM */
var React = require('react');

var spraak = {
    'nb_NO': 'Norsk (Bokmål)',
    'nn_NO': 'Norsk (Nynorsk)',
    'en_US': 'Engelsk',
    'se_NO': 'Samisk',
    'de_DE': 'Tysk',
    'fr_FR': 'Fransk',
    'es_ES': 'Spansk',
    'pl_PL': 'Polsk',
    'ru_RU': 'Russisk',
    'ur': 'Urdu'
};
var LocaleSelect = React.createClass({
    onChange: function (event) {
        this.props.store.setLocale(event.target.value);
    },
    render: function () {
        var uniqueLocales = Object.keys(this.props.tekst.innhold);
        if (uniqueLocales.length <= 1) {
            return null;
        } else {
            var options =  uniqueLocales.map(function (locale) {
                return <option value={locale}>{spraak[locale] ? spraak[locale] : locale}</option>;
            });
            return (
                <select onChange={this.onChange} value={this.props.locale}>
                    {options}
                </select>
            );
        }
    }
});

module.exports = LocaleSelect;