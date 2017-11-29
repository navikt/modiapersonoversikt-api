import React from 'react';

import { organisasjonType } from '../types';
import Apningstider from './apningstider';
import Adresse from './adresse';
import AntallPublikumsmottakInformasjon from './publikumsmottak-informasjon';

function DetaljertEnhetsinformasjon({ organisasjon }) {
    if (organisasjon.kontaktinformasjon.publikumsmottak.length === 0) {
        return <p>Ingen publikumsmottak</p>;
    }
    const publikumsmottak = organisasjon.kontaktinformasjon.publikumsmottak[0];
    return (
        <div>
            <Adresse adresse={publikumsmottak.besoeksadresse} />
            <AntallPublikumsmottakInformasjon publikumsmottak={organisasjon.kontaktinformasjon.publikumsmottak} />
            <Apningstider apningstider={publikumsmottak.apningstider} />
        </div>
    );
}
DetaljertEnhetsinformasjon.propTypes = {
    organisasjon: organisasjonType
};

export default DetaljertEnhetsinformasjon;
