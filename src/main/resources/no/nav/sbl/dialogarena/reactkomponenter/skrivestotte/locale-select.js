const React = require('react/addons');

const spraak = {
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
const LocaleSelect = React.createClass({
    propTypes: {
        'tekst': React.PropTypes.object.isRequired,
        'store': React.PropTypes.object.isRequired,
        'locale': React.PropTypes.string
    },
    onChange: function onChange(event) {
        this.props.store.setLocale(event.target.value);
    },
    render: function render() {
        const uniqueLocales = Object.keys(this.props.tekst.innhold);
        if (uniqueLocales.length <= 1) {
            return null;
        }
        const options = uniqueLocales.map(function tilOptionsValg(locale) {
            return <option key={locale} value={locale}>{spraak[locale] ? spraak[locale] : locale}</option>;
        });
        return (
            <select onChange={this.onChange} value={this.props.locale}>
                {options}
            </select>
        );
    }
});

module.exports = LocaleSelect;
