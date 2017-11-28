import React from 'react';

import { organisasjonType } from '../types';
import Apningstider from './apningstider';
import Adresse from './adresse';

function DetaljertEnhetsInformasjon({ organisasjon }) {
    if (organisasjon.kontaktinformasjon.publikumsmottak.length === 0) {
        return <p>Ingen publikumsmottak</p>;
    }
    const publikumsmottak = organisasjon.kontaktinformasjon.publikumsmottak[0];
    const flerePublikumsmottak = organisasjon.kontaktinformasjon.publikumsmottak.length > 1 ?
        <p className="infoblokk">Det finnes flere publikumsmottak.</p> : <div />;
    return (
        <div>
            <Adresse adresse={publikumsmottak.besoeksadresse} />
            {flerePublikumsmottak}
            <Apningstider apningstider={publikumsmottak.apningstider} />
        </div>
    );
}
DetaljertEnhetsInformasjon.propTypes = organisasjonType;

export default DetaljertEnhetsInformasjon;
