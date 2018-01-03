import React from 'react';
import PT from 'prop-types';

function Gateadresse({ adresse }) {
    const { gatenavn, husnummer, husbokstav, bolignummer, postnummer, poststed } = adresse;
    const adresseLinje = `${gatenavn} ${husnummer}${husbokstav ? husbokstav : ''}${bolignummer ? ` ${bolignummer}` : ''}, ${postnummer} ${poststed}`;
    return (<span>{adresseLinje}</span>);
}

Gateadresse.propTypes = {
    adresse: PT.shape({
        gatenavn: PT.string.isRequired,
        husnumer: PT.string.isRequired,
        husbokstav: PT.string,
        bolignummer: PT.string,
        postnummer: PT.string.isRequired,
        poststed: PT.string.isRequired
    }).isRequired
};

export default Gateadresse;
