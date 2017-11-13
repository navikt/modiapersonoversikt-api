import 'babel-polyfill';
import MeldingerSok from './meldinger-sok/meldinger-sok-module';
import Skrivestotte from './skrivestotte/skrivestotte-module';
import JournalforingsPanel from './journalforing-panel/journalforing-panel-module';
import VelgSakPanel from './velg-sak-panel/velg-sak-panel-module';
import VarselLerret from './varsel-lerret/varsel-lerret-module';
import FeilmeldingsModaler from './feilmeldingsmodaler/feilmeldingsmodaler-module';
import SaksoversiktLerret from './saksoversikt/lerret/saksoversikt-lerret-module';
import PleiepengerPanel from './pleiepenger/pleiepenger-panel';
import AlertStripeSuksessSolid from './alertstriper/alertstripe-module';
import LeggTilbakeDelvisSvarPanel from './dialog-panel/leggtilbakedelvissvar/legg-tilbake-delvis-svar-module';
import TraadVisning from './traadvisning/traadvisning-module';

import React from './nav-react';
import ReactDOM from 'react-dom';

window.ModiaJS = {
    Components: {
        MeldingerSok,
        Skrivestotte,
        JournalforingsPanel,
        VelgSakPanel,
        VarselLerret,
        FeilmeldingsModaler,
        SaksoversiktLerret,
        PleiepengerPanel,
        AlertStripeSuksessSolid,
        LeggTilbakeDelvisSvarPanel,
        TraadVisning
    },
    InitializedComponents: {},
    React,
    ReactDOM
};

module.exports = window.ModiaJS.Components;
