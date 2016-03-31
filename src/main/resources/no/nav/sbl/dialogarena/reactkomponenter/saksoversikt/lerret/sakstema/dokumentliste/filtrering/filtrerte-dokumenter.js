import React, { PropTypes as pt } from 'react';
import { NAV, BRUKER, ANDRE } from './filtrering-avsender-valg';
import DokumentListe from './../dokumentliste';

const filtrertPaaNav = (retning, filtreringsvalg) =>
    filtreringsvalg[NAV] && (retning === 'UT' || retning === 'INTERN');
const filtrertPaaBruker = (retning, avsender, filtreringsvalg) =>
    filtreringsvalg[BRUKER] && retning === 'INN' && avsender === 'SLUTTBRUKER';
const filtrertPaaAndre = (retning, avsender, filtreringsvalg) =>
    filtreringsvalg[ANDRE] && retning === 'INN' && avsender !== 'SLUTTBRUKER';

export const skalViseDokument = ({ avsender, retning }, filtreringsvalg) => filtrertPaaNav(retning, filtreringsvalg) ||
filtrertPaaBruker(retning, avsender, filtreringsvalg) || filtrertPaaAndre(retning, avsender, filtreringsvalg);

const FiltrerteDokumenter = props => {
    const { dokumentMetadata, filtreringsvalg, dokumentlisteParam } = props;

    const filtrerteDokumenter = dokumentMetadata.filter(dokument => skalViseDokument(dokument, filtreringsvalg));

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
    filtreringsvalg: pt.object.isRequired
};

export default FiltrerteDokumenter;
