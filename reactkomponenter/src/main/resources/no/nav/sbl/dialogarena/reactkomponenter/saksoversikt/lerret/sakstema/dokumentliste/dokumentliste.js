import React from 'react';
import PT from 'prop-types';
import { groupBy } from 'lodash';
import DokumentInfoElm from './dokument/dokument-info-elm';
import { nyesteDokumentForst } from './../../../utils/siste-oppdatering/siste-oppdatering';

const grupperDokumenterPaaAar = (dokumenterGruppertPaaAar) =>
    aarstall => ({ aarstall, dokumenter: dokumenterGruppertPaaAar[aarstall].sort(nyesteDokumentForst) });
const akkumulerDokumenterPaaAar = (brukerNavn, visTema, velgJournalpost, visSide, gjeldendeAar) =>
    (acc, { aarstall, dokumenter }) => {
        if (aarstall !== gjeldendeAar) {
            acc.push(<li key={`aarstall-'${aarstall}`} className="aarstall" aria-hidden="true">{aarstall}</li>);
        }
        return acc.concat(
            dokumenter.map((dokument, index) => (
                <DokumentInfoElm
                    key={`dokument-${aarstall}-${index}`}
                    brukerNavn={brukerNavn}
                    visTema={visTema}
                    velgJournalpost={velgJournalpost}
                    visSide={visSide}
                    dokumentinfo={dokument}
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
    dokumentMetadata: PT.array.isRequired,
    brukerNavn: PT.string.isRequired,
    visTema: PT.bool.isRequired,
    velgJournalpost: PT.func.isRequired,
    visSide: PT.func.isRequired
};

export default DokumentListe;
