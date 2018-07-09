import React from 'react';
import PT from 'prop-types';

function UstrukturertAdresse({ adresse }) {
    return (<span>{adresse}</span>);
}

UstrukturertAdresse.propTypes = {
    adresse: PT.string.isRequired
};

export default UstrukturertAdresse;
