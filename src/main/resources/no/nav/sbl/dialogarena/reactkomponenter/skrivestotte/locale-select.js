const React = require('react');
import PT from 'prop-types';


const spraak = {
    nb_NO: 'Norsk (Bokmål)',
    nn_NO: 'Norsk (Nynorsk)',
    en_US: 'Engelsk',
    se_NO: 'Samisk',
    de_DE: 'Tysk',
    fr_FR: 'Fransk',
    es_ES: 'Spansk',
    pl_PL: 'Polsk',
    ru_RU: 'Russisk',
    ur: 'Urdu'
};

/* eslint "react/prefer-es6-class": 1 */
const LocaleSelect = React.createClass({
    propTypes: {
        tekst: PT.object.isRequired,
        store: PT.object.isRequired,
        locale: PT.string
    },
    onChange: function onChange(event) {
        this.props.store.setLocale(event.target.value);
    },
    render: function render() {
        const uniqueLocales = Object.keys(this.props.tekst.innhold);
        if (uniqueLocales.length <= 1) {
            return null;
        }
        const options = uniqueLocales.map((locale) => (
            <option key={locale} value={locale}>{spraak[locale] ? spraak[locale] : locale}</option>
        ));
        return (
            <select onChange={this.onChange} value={this.props.locale}>
                {options}
            </select>
        );
    }
});

module.exports = LocaleSelect;
