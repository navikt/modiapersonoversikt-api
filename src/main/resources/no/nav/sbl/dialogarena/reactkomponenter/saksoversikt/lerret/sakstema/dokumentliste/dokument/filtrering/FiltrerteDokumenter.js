import React, { PropTypes as pt } from 'react';
import DokumentListe from './../../dokumentliste';
import { ALLE, NAV, BRUKER, ANDRE } from './FiltreringAvsenderValg';
import FiltrerAvsender from './FiltrerAvsender';

const FiltrerteDokumenter = props => {

    const { dokumentMetadata, filtreringsvalg } = props;

    const valgtAlle = filtreringsvalg === ALLE;
    const valgtNav = filtreringsvalg === NAV;
    const valgtBruker = filtreringsvalg === BRUKER;
    const valgtAndre = filtreringsvalg === ANDRE;

    const filtrertPaaNav = (retning) => valgtNav && (retning === 'INN' || retning === 'NOTAT');
    const filtrertPaaBruker = (retning, avsender) => valgtBruker && retning === 'INN' && avsender === 'SLUTTBRUKER';
    const filtrertPaaAndre = (retning, avsender) => valgtAndre && retning === 'INN' && avsender != 'SLUTTBRUKER';

    const skalViseDokument = ({ avsender, retning }) => {
        return valgtAlle ||
            filtrertPaaNav(retning) ||
            filtrertPaaBruker(retning, avsender) ||
            filtrertPaaAndre(retning, avsender);
    };


    const filtrerteDokumenter = dokumentMetadata.filter(dokument => skalViseDokument(dokument));

    if (filtrerteDokumenter.length == 0) {
        return (
            <div>
                <FiltrerAvsender />
                <noscript/>
            </div>);
    }

    console.log("Filtrerte dokumenter.");
    console.log(filtrerteDokumenter);

    /*
     visTema="false"
     dokumentMetadata={valgtTema.dokumentMetadata}
     brukerNavn={brukerNavn}
     visSide={visSide}
     velgJournalpost={velgJournalpost}
     */


    return (
        <div>
            <FiltrerAvsender />
            <DokumentListe {...props} />
        </div>

    );

};

//<DokumentListe {...props} />


export default FiltrerteDokumenter;