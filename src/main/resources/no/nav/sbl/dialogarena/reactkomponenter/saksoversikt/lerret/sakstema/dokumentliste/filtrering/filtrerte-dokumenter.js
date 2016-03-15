import React, { PropTypes as pt } from 'react';
import { ALLE, NAV, BRUKER, ANDRE } from './filtrering-avsender-valg';
import DokumentListe from './../dokumentliste';

const FiltrerteDokumenter = props => {
    const { dokumentMetadata, filtreringsvalg, dokumentlisteParam } = props;

    const valgtAlle = filtreringsvalg === ALLE;
    const valgtNav = filtreringsvalg === NAV;
    const valgtBruker = filtreringsvalg === BRUKER;
    const valgtAndre = filtreringsvalg === ANDRE;

    const filtrertPaaNav = (retning) => valgtNav && (retning === 'UT' || retning === 'INTERN');
    const filtrertPaaBruker = (retning, avsender) => valgtBruker && retning === 'INN' && avsender === 'SLUTTBRUKER';
    const filtrertPaaAndre = (retning, avsender) => valgtAndre && retning === 'INN' && avsender !== 'SLUTTBRUKER';

    const skalViseDokument = ({ avsender, retning }) => valgtAlle || filtrertPaaNav(retning) ||
    filtrertPaaBruker(retning, avsender) || filtrertPaaAndre(retning, avsender);

    const filtrerteDokumenter = dokumentMetadata.filter(dokument => skalViseDokument(dokument));

    const dokumentliste = filtrerteDokumenter.length === 0 ?
        <noscript/> :
        <DokumentListe dokumentMetadata={filtrerteDokumenter} {...dokumentlisteParam} />;

    return <div>{dokumentliste}</div>;
};


FiltrerteDokumenter.propTypes = {
    dokumentlisteParam: pt.shape({
        visTema: pt.bool.isRequired,
        brukerNavn: pt.string.isRequired,
        visSide: pt.func.isRequired,
        velgJournalpost: pt.func.isRequired
    }).isRequired,
    dokumentMetadata: pt.array.isRequired,
    filtreringsvalg: pt.string.isRequired
};

export default FiltrerteDokumenter;
