import React from 'react';
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

function onChange(event, props) {
    props.store.setLocale(event.target.value);
}

function LocaleSelect(props) {
    const uniqueLocales = Object.keys(props.tekst.innhold);
    if (uniqueLocales.length <= 1) {
        return null;
    }
    const options = uniqueLocales.map((locale) => (
        <option key={locale} value={locale}>{spraak[locale] ? spraak[locale] : locale}</option>
    ));
    return (
        <select onChange={(event) => onChange(event, props)} value={props.locale}>
            {options}
        </select>
    );
}

LocaleSelect.propTypes = {
    tekst: PT.object.isRequired,
    store: PT.object.isRequired,
    locale: PT.string
};

export default LocaleSelect;
