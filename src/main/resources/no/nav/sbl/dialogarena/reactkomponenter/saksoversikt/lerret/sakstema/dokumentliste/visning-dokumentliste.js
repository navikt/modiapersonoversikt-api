import React, { PropTypes as PT } from 'react';
import { FormattedMessage } from 'react-intl';
import IngenDokumenter from './ingen-dokumenter';
import IngenDokumenterBidrag from './ingen-dokumenter-bidrag';
import TidligereDokumenter from './tidligere-dokumenter';
import FiltrerteDokumenter from './dokument/filtrering/FiltrerteDokumenter';
import FiltrerAvsender from './dokument/filtrering/FiltrerAvsender';
import ViktigAViteLenke from './../../viktigavite/ViktigAViteLenke';

const VisningDokumentliste = ({ sakstema, valgtTema, brukerNavn, velgJournalpost, visSide, filtreringsvalg }) => {
    const dokumenter = sakstema.slice(1).reduce((acc, tema) => acc.concat(tema.dokumentMetadata), []);
    const dokumentlisteParam = { brukerNavn, visSide, velgJournalpost };

    dokumentlisteParam.visTema = valgtTema.temakode === 'alle';

    const dokumentliste = valgtTema.temakode !== 'alle' ?
        <FiltrerteDokumenter dokumentMetadata={valgtTema.dokumentMetadata} filtreringsvalg={filtreringsvalg} dokumentlisteParam={dokumentlisteParam}  /> :
        <FiltrerteDokumenter dokumentMetadata={dokumenter} filtreringsvalg={filtreringsvalg} dokumentlisteParam={dokumentlisteParam} />;

    const ingendokumenter = (
        <h2 className="robust-ikon-feil-strek ingendokumenterheader">
            <FormattedMessage id="dokumentinfo.sakstema.ingen.dokumenter.header"/>
        </h2>);

    if (valgtTema.temakode === 'BID') {
        return <IngenDokumenterBidrag ingenDokumenterHeader={ingendokumenter}/>;
    }
    if (valgtTema.dokumentMetadata.length === 0) {
        return <IngenDokumenter ingenDokumenterHeader={ingendokumenter}/>;
    }

    return (
        <div>
            <div className="dokumentliste-container">
                <ViktigAViteLenke valgtTema={valgtTema} visSide={visSide}/>
                <FiltrerAvsender valgtValg={filtreringsvalg}/>
            </div>
            { dokumentliste }
            <TidligereDokumenter />
        </div>);
};

VisningDokumentliste.propTypes = {
    sakstema: PT.array.isRequired,
    valgtTema: PT.object,
    visSide: PT.func.isRequired,
    velgJournalpost: PT.func,
    filtreringsvalg: PT.string
};

export default VisningDokumentliste;
