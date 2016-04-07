import React, { PropTypes as PT } from 'react';
import { FormattedMessage } from 'react-intl';
import IngenDokumenter from './info/ingen-dokumenter';
import IngenDokumenterBidrag from './info/ingen-dokumenter-bidrag';
import TidligereDokumenter from './info/tidligere-dokumenter';
import FiltrerteDokumenter from './filtrering/filtrerte-dokumenter';
import FiltrerAvsender from './filtrering/filtrer-avsender';
import ViktigAViteLenke from './../../viktigavite/viktig-aa-vite-lenke';

const VisningDokumentliste = ({ valgtTema, brukerNavn, velgJournalpost, visSide, filtreringsvalg }) => {
    const dokumentlisteParam = { brukerNavn, visSide, velgJournalpost };
    dokumentlisteParam.visTema = valgtTema.temakode === 'alle';

    const dokumentliste = (
        <FiltrerteDokumenter dokumentMetadata={valgtTema.dokumentMetadata}
          filtreringsvalg={filtreringsvalg}
          dokumentlisteParam={dokumentlisteParam}
        />);

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

    const infoMeldingDokumenterBidrag = valgtTema.temakode === 'BID' ?
        <div className="infoingenbidrag">
            <FormattedMessage
              id="dokumentinfo.sakstema.ingen.dokumenter.bidrag"
            />
        </div> : <noscript/>;

    return (
        <div>
            <FiltrerAvsender alleredeValgt={filtreringsvalg}/>
            <ViktigAViteLenke valgtTema={valgtTema} visSide={visSide}/>
            {infoMeldingDokumenterBidrag}
            { dokumentliste }
            <TidligereDokumenter />
        </div>);
};

VisningDokumentliste.propTypes = {
    valgtTema: PT.object,
    visSide: PT.func.isRequired,
    velgJournalpost: PT.func,
    filtreringsvalg: PT.object.isRequired
};

export default VisningDokumentliste;
