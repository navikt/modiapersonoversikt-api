import React, { PropTypes as PT } from 'react';
import { FormattedMessage } from 'react-intl';
import IngenDokumenter from './info/ingen-dokumenter';
import IngenDokumenterBidrag from './info/ingen-dokumenter-bidrag';
import TidligereDokumenter from './info/tidligere-dokumenter';
import FiltrerteDokumenter from './filtrering/filtrerte-dokumenter';
import FiltrerAvsender from './filtrering/filtrer-avsender';
import ViktigAViteLenke from './../../viktigavite/viktig-aa-vite-lenke';

const VisningDokumentliste = ({ sakstema, valgtTema, brukerNavn, velgJournalpost, visSide, filtreringsvalg }) => {
    const dokumenter = sakstema.slice(1).reduce((acc, tema) => acc.concat(tema.dokumentMetadata), []);
    const dokumentlisteParam = { brukerNavn, visSide, velgJournalpost };

    dokumentlisteParam.visTema = valgtTema.temakode === 'alle';

    const dokumentliste = valgtTema.temakode !== 'alle' ?
        <FiltrerteDokumenter dokumentMetadata={valgtTema.dokumentMetadata} filtreringsvalg={filtreringsvalg}
          dokumentlisteParam={dokumentlisteParam}
        /> :
        <FiltrerteDokumenter dokumentMetadata={dokumenter} filtreringsvalg={filtreringsvalg}
          dokumentlisteParam={dokumentlisteParam}
        />;

    const ingendokumenter = (
        <h2 className="robust-ikon-feil-strek ingendokumenterheader">
            <FormattedMessage id="dokumentinfo.sakstema.ingen.dokumenter.header"/>
        </h2>);

    if (valgtTema.dokumentMetadata.length === 0) {
        if (valgtTema.temakode === 'BID') {
            return <IngenDokumenterBidrag ingenDokumenterHeader={ingendokumenter}/>;
        }
        return <IngenDokumenter ingenDokumenterHeader={ingendokumenter}/>;
    }

    const ingenDokumenterBidrag = valgtTema.temakode === 'BID' ?
        <div className="infoingenbidrag">
            <FormattedMessage
              id="dokumentinfo.sakstema.ingen.dokumenter.bidrag"
            />
        </div> : <noscript/>;

    return (
        <div>
            <FiltrerAvsender alleredeValgt={filtreringsvalg}/>
            <ViktigAViteLenke valgtTema={valgtTema} visSide={visSide}/>
            {ingenDokumenterBidrag}
            { dokumentliste }
            <TidligereDokumenter />
        </div>);
};

VisningDokumentliste.propTypes = {
    sakstema: PT.array.isRequired,
    valgtTema: PT.object,
    visSide: PT.func.isRequired,
    velgJournalpost: PT.func,
    filtreringsvalg: PT.object.isRequired
};

export default VisningDokumentliste;
