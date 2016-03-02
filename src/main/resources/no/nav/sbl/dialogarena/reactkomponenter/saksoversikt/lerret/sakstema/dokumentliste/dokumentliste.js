import React, { PropTypes as pt } from 'react';
import { groupBy } from 'lodash';
import DokumentInfoElm from './dokument-info-elm';

const nyesteForst = (a, b) => a.dato.dayOfYear < b.dato.dayOfYear ? 1 : -1;
const nyesteAarForst = (a, b) => a < b ? 1 : -1;

const DokumentListe = ({ dokumentMetadata, brukerNavn, visTema, velgJournalpost, visSide }) => {
    const dokumenterGruppertPaaAar = groupBy(dokumentMetadata, dokument => dokument.dato.year);
    const gjeldendeAar = new Date().getFullYear().toString();
    
        const dokumentListeForAarstall = Object.keys(dokumenterGruppertPaaAar)
            .sort(nyesteAarForst)
            .map(aarstall => ({ aarstall, dokumenter: dokumenterGruppertPaaAar[aarstall].sort(nyesteForst) }))
            .reduce((acc, { aarstall, dokumenter }) => {
                if (aarstall !== gjeldendeAar) {
                    acc.push(<li key={'aarstall-' + aarstall} className="aarstall">{aarstall}</li>);
                }

                return acc.concat(
                    dokumenter.map((dokument, index) => (
                        <DokumentInfoElm key={`dokument-${aarstall}-${index}`} brukerNavn={brukerNavn} visTema={visTema}
                                         velgJournalpost={velgJournalpost} visSide={visSide} dokumentinfo={dokument}/>
                    ))
                );
            }, []);


    return (<ul className="ustilet dokumentliste">{dokumentListeForAarstall}</ul>);
}


DokumentListe.propTypes = {
    dokumentMetadata: pt.array.isRequired,
    brukerNavn: pt.string.isRequired,
    visTema: pt.string
};

export default DokumentListe;
