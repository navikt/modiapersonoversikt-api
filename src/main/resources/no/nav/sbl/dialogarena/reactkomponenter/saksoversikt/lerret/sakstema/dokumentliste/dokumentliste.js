import React, { PropTypes as pt } from 'react';
import { groupBy } from 'lodash';
import DokumentInfoElm from './dokument/dokument-info-elm';

const nyesteForst = (a, b) => b.dato.dayOfYear - a.dato.dayOfYear;
const grupperDokumenterPaaAar = (dokumenterGruppertPaaAar) => aarstall => ({ aarstall, dokumenter: dokumenterGruppertPaaAar[aarstall].sort(nyesteForst) });
const akkumulerDokumenterPaaAar = (brukerNavn, visTema, velgJournalpost, visSide, gjeldendeAar) => (acc, { aarstall, dokumenter }) => {
    if (aarstall !== gjeldendeAar) {
        acc.push(<li key={`aarstall-'${aarstall}`} className="aarstall" aria-hidden="true">{aarstall}</li>);
    }

    return acc.concat(
        dokumenter.map((dokument, index) => (
            <DokumentInfoElm key={`dokument-${aarstall}-${index}`} brukerNavn={brukerNavn} visTema={visTema}
                             velgJournalpost={velgJournalpost} visSide={visSide} dokumentinfo={dokument}
            />
        ))
    );
};

const DokumentListe = ({ dokumentMetadata, brukerNavn, visTema, velgJournalpost, visSide }) => {
    const dokumenterGruppertPaaAar = groupBy(dokumentMetadata, dokument => dokument.dato.year);
    const gjeldendeAar = new Date().getFullYear().toString();

    const dokumentListeForAarstall = Object.keys(dokumenterGruppertPaaAar)
        .slice(0).sort().reverse()
        .map(grupperDokumenterPaaAar(dokumenterGruppertPaaAar))
        .reduce(akkumulerDokumenterPaaAar(brukerNavn, visTema, velgJournalpost, visSide, gjeldendeAar), []);

    return <ul className="ustilet">{dokumentListeForAarstall}</ul>;
};

DokumentListe.propTypes = {
    dokumentMetadata: pt.array.isRequired,
    brukerNavn: pt.string.isRequired,
    visTema: pt.bool.isRequired,
    velgJournalpost: pt.func.isRequired,
    visSide: pt.func.isRequired
};

export default DokumentListe;
