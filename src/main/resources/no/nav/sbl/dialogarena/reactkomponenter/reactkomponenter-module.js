import 'babel-polyfill';
import MeldingerSok from './meldinger-sok/meldinger-sok-module';
import Skrivestotte from './skrivestotte/skrivestotte-module';
import JournalforingsPanel from './journalforing-panel/journalforing-panel-module';
import VelgSakPanel from './velg-sak-panel/velg-sak-panel-module';
import VarselLerret from './varsel-lerret/varsel-lerret-module';
import FeilmeldingsModaler from './feilmeldingsmodaler/feilmeldingsmodaler-module';
import SaksoversiktLerret from './saksoversikt/lerret/saksoversikt-lerret-module';
import LeggTilbakeDelvisSvarPanel from './dialog-panel/leggtilbakedelvissvar/legg-tilbake-delvis-svar-module';

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
        LeggTilbakeDelvisSvarPanel
    },
    InitializedComponents: {},
    React,
    ReactDOM
};

module.exports = window.ModiaJS.Components;
