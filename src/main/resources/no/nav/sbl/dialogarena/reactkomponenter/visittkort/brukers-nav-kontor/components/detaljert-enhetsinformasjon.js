import React from 'react';
import PT from 'prop-types';

import { organisasjonType } from '../types';
import Apningstider from './apningstider';
import Adresse from './adresse';
import AntallPublikumsmottakInformasjon from './publikumsmottak-informasjon';

function DetaljertEnhetsinformasjon({ organisasjon, norg2FrontendBaseUrl }) {
    if (organisasjon.kontaktinformasjon.publikumsmottak.length === 0) {
        return <p>Ingen publikumsmottak</p>;
    }
    const publikumsmottak = organisasjon.kontaktinformasjon.publikumsmottak[0];
    return (
        <div>
            <Adresse adresse={publikumsmottak.besoeksadresse} />
            <AntallPublikumsmottakInformasjon publikumsmottak={organisasjon.kontaktinformasjon.publikumsmottak} />
            <Apningstider apningstider={publikumsmottak.apningstider} />
            <a
                href={`${norg2FrontendBaseUrl}/#/startsok?enhetNr=${organisasjon.enhetId}`}
                target="_blank"
                rel="noopener noreferrer"
            >
                Mer informasjon om kontoret
            </a>
        </div>
    );
}

DetaljertEnhetsinformasjon.propTypes = {
    organisasjon: organisasjonType.isRequired,
    norg2FrontendBaseUrl: PT.string.isRequired
};

export default DetaljertEnhetsinformasjon;
