import React from 'react';
import PT from 'prop-types';

import { organisasjonType } from '../types';
import Apningstider from './apningstider';
import Adresse from './adresse';
import AntallPublikumsmottakInformasjon from './publikumsmottak-informasjon';

function NORGLenke({ enhetId, norg2FrontendBaseUrl }) {
    return (
        <a
            href={`${norg2FrontendBaseUrl}/#/startsok?enhetNr=${enhetId}`}
            target="_blank"
            rel="noopener noreferrer"
        >
            Mer informasjon om kontoret
        </a>
    );
}

NORGLenke.propTypes = {
    enhetId: PT.string.isRequired,
    norg2FrontendBaseUrl: PT.string.isRequired
};

function DetaljertEnhetsinformasjon({ organisasjon, norg2FrontendBaseUrl }) {
    if (organisasjon.kontaktinformasjon.publikumsmottak.length === 0) {
        return (
            <div>
                <p className="infoblokk">Ingen publikumsmottak</p>
                <NORGLenke enhetId={organisasjon.enhetId} norg2FrontendBaseUrl={norg2FrontendBaseUrl}/>
            </div>
        );
    }
    const publikumsmottak = organisasjon.kontaktinformasjon.publikumsmottak[0];
    return (
        <div>
            <Adresse adresse={publikumsmottak.besoeksadresse} />
            <AntallPublikumsmottakInformasjon publikumsmottak={organisasjon.kontaktinformasjon.publikumsmottak} />
            <Apningstider apningstider={publikumsmottak.apningstider} />
            <NORGLenke enhetId={organisasjon.enhetId} norg2FrontendBaseUrl={norg2FrontendBaseUrl} />
        </div>
    );
}

DetaljertEnhetsinformasjon.propTypes = {
    organisasjon: organisasjonType.isRequired,
    norg2FrontendBaseUrl: PT.string.isRequired
};

export default DetaljertEnhetsinformasjon;
