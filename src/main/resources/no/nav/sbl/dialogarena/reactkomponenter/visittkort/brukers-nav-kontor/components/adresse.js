import React from 'react';
import { besoeksadresseType } from '../types';
import PT from 'prop-types';

function Adresse({ adresse }) {
    const gatenavn = adresse.gatenavn;
    const husnummer = adresse.husnummer;
    const husbokstav = adresse.husbokstav || '';
    const postnummer = adresse.postnummer;
    const poststed = adresse.poststed;
    return (
        <div className="infoblokk">
            <h3 className={"overskrift"}>Adresse</h3>
            <p>{gatenavn} {husnummer}{husbokstav}, {postnummer} {poststed}</p>
        </div>
    );
}

Adresse.propTypes = PT.shape({ adresse: besoeksadresseType }).isRequired;

export default Adresse;
