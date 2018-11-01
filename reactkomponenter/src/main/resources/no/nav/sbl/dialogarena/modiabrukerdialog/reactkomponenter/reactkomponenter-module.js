import 'babel-polyfill';
import MeldingerSok from './meldinger-sok/meldinger-sok-module';
import SlaaSammenTraader from './slaa-sammen-traader/slaa-sammen-traader-module';
import Skrivestotte from './skrivestotte/skrivestotte-module';
import JournalforingsPanel from './journalforing-panel/journalforing-panel-module';
import VelgSakPanel from './velg-sak-panel/velg-sak-panel-module';
import VarselLerret from './varsel-lerret/varsel-lerret-module';
import FeilmeldingsModaler from './feilmeldingsmodaler/feilmeldingsmodaler-module';
import SaksoversiktLerret from './saksoversikt/lerret/saksoversikt-lerret-module';
import PleiepengerPanel from './pleiepenger/pleiepenger-panel';
import AlertStripeSuksessSolid from './alertstriper/alertstripe-module';
import LeggTilbakeDelvisSvarPanel from './dialog-panel/leggtilbakedelvissvar/legg-tilbake-delvis-svar-module';
import TraadVisning from './dialog-panel/traadvisning/traadvisning-module';
import RedirectModal from './redirectmodal/redirectmodal-module';
import BrukersNavKontor from './visittkort/brukers-nav-kontor/nav-kontor-module';
import FolkeregistrertAdresse from './visittkort/adresse/folkeregistrert-adresse-module';
import TildeltFlereOppgaverAlert from './dialog-panel/flere-henvendelser-alert/tildelt-flere-oppgaver-alert';
import NyttVisittkort from './nytt-visittkort/nytt-visittkort';
import NyUtbetaling from './ny-utbetaling/ny-utbetaling';
import NySaksoversikt from './ny-saksoversikt/ny-saksoversikt';

import React from './nav-react';
import ReactDOM from 'react-dom';

window.ModiaJS = {
    Components: {
        MeldingerSok,
        SlaaSammenTraader,
        Skrivestotte,
        JournalforingsPanel,
        VelgSakPanel,
        VarselLerret,
        FeilmeldingsModaler,
        SaksoversiktLerret,
        PleiepengerPanel,
        AlertStripeSuksessSolid,
        LeggTilbakeDelvisSvarPanel,
        TraadVisning,
        RedirectModal,
        BrukersNavKontor,
        FolkeregistrertAdresse,
        TildeltFlereOppgaverAlert,
        NyttVisittkort,
        NyUtbetaling,
        NySaksoversikt
    },
    InitializedComponents: {},
    React,
    ReactDOM
};

module.exports = window.ModiaJS.Components;
