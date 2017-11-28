import React from 'react';
import { OrganiasjonType } from '../types';

function Adresse({ organisasjon }) {
    const publikumsmottak = organisasjon.kontaktinformasjon.publikumsmottak;
    const gatenavn = publikumsmottak.length === 0 ? '' : publikumsmottak[0].besoeksadresse.gatenavn;
    return (
        <div className="infoblokk">
            <h3 className={"overskrift"}>Adresse</h3>
            <p>{gatenavn}</p>
        </div>
    );
}

Adresse.propTypes = OrganiasjonType;

export default Adresse;
