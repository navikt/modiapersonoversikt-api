import React from 'react';
import PT from 'prop-types';

import { ADRESSETYPER } from '../../../constants';
import Gateadresse from './gateadresse';
import Matrikkeladresse from './matrikkeladresse';
import UstrukturertAdresse from './ustrukturert-adresse';

function Adresse({ adresseType, adresse }) {
    if (adresseType === ADRESSETYPER.INGEN_ADRESSE_REGISTRERT) {
        return (<span>INGEN REGISTRERT ADRESSE</span>);
    } else if (adresseType === ADRESSETYPER.GATEADRESSE) {
        return (<Gateadresse adresse={adresse} />);
    } else if (adresseType === ADRESSETYPER.MATRIKKELADRESSE) {
        return (<Matrikkeladresse adresse={adresse} />);
    } else if (adresseType === ADRESSETYPER.USTRUKTURERT) {
        return (<UstrukturertAdresse adresse={adresse} />);
    }
    return <span>Feil ved visning av adresse</span>;
}

Adresse.propTypes = {
    adresseType: PT.string.isRequired,
    adresse: PT.any
};

Adresse.defaultProps = {
    adresse: {}
};

export default Adresse;
