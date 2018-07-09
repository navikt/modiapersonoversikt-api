import React from 'react';
import PT from 'prop-types';

function Tilleggsadresse({ adresse }) {
    if (!adresse) {
        return null;
    }
    return (<span>{adresse}</span>);
}

Tilleggsadresse.propTypes = {
    adresse: PT.string
};

export default Tilleggsadresse;
