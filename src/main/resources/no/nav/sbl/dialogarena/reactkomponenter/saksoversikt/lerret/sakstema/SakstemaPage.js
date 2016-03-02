import React, { PropTypes as PT } from 'react';
import SakstemaListe from './SakstemaListe';
import DokumentListe from './dokumentliste/dokumentliste'
import ViktigAViteLenke from './../viktigavite/ViktigAViteLenke'
import TidligereDokumenter from './dokumentliste/tidligere-dokumenter';
import { FormattedMessage } from 'react-intl';

class SakstemaPage extends React.Component {
    render() {
        const { sakstema, valgtTema, velgSak, brukerNavn, visSide } = this.props;
        const dokumenter = sakstema.reduce((acc, tema) => {
            return acc.concat(tema.dokumentMetadata);
        }, []);

        const dokumentliste = valgtTema.temakode !== 'alle' ?
            <DokumentListe visTema="false"
                           dokumentMetadata={valgtTema.dokumentMetadata}
                           brukerNavn={brukerNavn}/> :
            <DokumentListe visTema="true" dokumentMetadata={dokumenter}
                           brukerNavn={brukerNavn}/>;

        return (
            <div className="sakstema-container">
                <section className="saksoversikt-liste">
                    <SakstemaListe sakstema={sakstema} velgSak={velgSak}
                                   valgtTema={valgtTema}/>
                </section>
                <section className="saksoversikt-innhold side-innhold">
                    <ViktigAViteLenke valgtTema={valgtTema} visSide={visSide}/>
                    {this._visningDokumentliste(valgtTema, dokumentliste)}
                </section>
            </div>
        );
    }

    _visningDokumentliste(valgtTema, dokumentliste) {
        const ingendokumenter = <h1 className="robust-ikon-feil-strek ingendokumenterheader">
            <FormattedMessage
                id="dokumentinfo.sakstema.ingen.dokumenter.header"/></h1>;

        if (valgtTema.dokumentMetadata.length > 0) {
            return <div>{ dokumentliste }<TidligereDokumenter /></div>;
        }
        else if (valgtTema.temakode === 'BID') {
            return <div>{ingendokumenter}<p> Modia viser ikke dokumenter på temaet Bidrag. </p></div>;
        }

        return (
            <div className="default-error ingendokumenter">{ingendokumenter}
                <p className="ingendokumenterforklaring"><FormattedMessage
                    id="dokumentinfo.sakstema.ingen.dokumenter.forklaring"/></p>
                <a>Lenke til Gosys</a>
            </div >);
    }
}

SakstemaPage.propTypes = {
    sakstema: PT.array.isRequired,
    visSide: PT.func.isRequired

};

export default SakstemaPage;
