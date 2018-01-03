import React from 'react';
import PT from 'prop-types';

import { ADRESSETYPER } from '../../../constants';
import Gateadresse from './gateadresse';
import Matrikkeladresse from './matrikkeladresse';

function Adresse({ adresseType, adresse }) {
    if (adresseType === ADRESSETYPER.INGEN_ADRESSE_REGISTRERT) {
        return (<span>INGEN REGISTRERT ADRESSE</span>);
    } else if (adresseType === ADRESSETYPER.GATEADRESSE) {
        return (<Gateadresse adresse={adresse} />);
    } else if (adresseType === ADRESSETYPER.MATRIKKELADRESSE) {
        return (<Matrikkeladresse adresse={adresse} />);
    }
    return <span>Feil ved visning av adresse</span>;
}

Adresse.propTypes = {
    adresseType: PT.string.isRequired,
    adresse: PT.object
};

export default Adresse;
