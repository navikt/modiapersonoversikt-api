import React from 'react';
import PT from 'prop-types';

function Matrikkeladresse({ adresse }) {
    console.log(adresse);
    const { eiendomsnavn, postnummer, poststed } = adresse;
    const adresseLinje = `${eiendomsnavn ? `${eiendomsnavn}, ` : ''}${postnummer} ${poststed}`;
    return (<span>{adresseLinje}</span>);
}

Matrikkeladresse.propTypes = {
    adresse: PT.shape({
        eidendomsnavn: PT.string,
        postnummer: PT.string.isRequired,
        poststed: PT.string.isRequired
    }).isRequired
};

export default Matrikkeladresse;
