import React, { PropTypes as PT } from 'react';
import TidligereDokumenter from './tidligere-dokumenter';
import { FormattedMessage } from 'react-intl';
import DokumentListe from './dokumentliste'

function openGosys(e) {
    e.preventDefault();
    document.querySelector('.hiddenGosysLenkePanel').click();
}

const VisningDokumentliste = ({ sakstema, valgtTema, velgJournalpost, visSide }) => {
    const dokumenter = sakstema.slice(1).reduce((acc, tema) => {
        return acc.concat(tema.dokumentMetadata);
    }, []);

    const dokumentliste = valgtTema.temakode !== 'alle' ?
        <DokumentListe visTema="false"
                       dokumentMetadata={valgtTema.dokumentMetadata}
                       visSide={visSide}
                       velgJournalpost={velgJournalpost}/> :
        <DokumentListe visTema="true" dokumentMetadata={dokumenter}
                       visSide={visSide}
                       velgJournalpost={velgJournalpost}/>;

    const ingenDokumenterBidrag = valgtTema.temakode === 'BID' ?
        <p className="ingendokumenterforklaring"><FormattedMessage
            id="dokumentinfo.sakstema.ingen.dokumenter.bidrag"/></p> : <noscript/>;

    const ingendokumenter = <h1 className="robust-ikon-feil-strek ingendokumenterheader">
        <FormattedMessage
            id="dokumentinfo.sakstema.ingen.dokumenter.header"/></h1>;

    if (valgtTema.dokumentMetadata.length > 0) {
        return <div>{ ingenDokumenterBidrag }{ dokumentliste }<TidligereDokumenter /></div>;
    }

    if (valgtTema.temakode === 'BID') {
        return (
            <div className="default-error ingendokumenter">{ingendokumenter}{ingenDokumenterBidrag}
            </div>);
    }

    return (
        <div className="default-error ingendokumenter">{ingendokumenter}
            <p className="ingendokumenterforklaring"><FormattedMessage
                id="dokumentinfo.sakstema.ingen.dokumenter.forklaring"/></p>
            <a href="javascript:void(0);" onClick={openGosys}><FormattedMessage
                id="dokumentinfo.sakstema.lenke.gosys"/></a>
        </div >);
};

VisningDokumentliste.propTypes = {
    sakstema: PT.array.isRequired,
    valgtTema: PT.object,
    visSide: PT.func.isRequired,
    velgJournalpost: PT.func
};

export default VisningDokumentliste;
