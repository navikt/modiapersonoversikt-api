import React, { PropTypes as pt } from 'react';
import SakstemaListe from './SakstemaListe';
import DokumentListe from './dokumentliste/dokumentliste';
import ViktigAViteLenke from './../viktigavite/ViktigAViteLenke';
import TidligereDokumenter from './dokumentliste/tidligere-dokumenter';

class SakstemaPage extends React.Component {
    _visningDokumentliste(valgtTema, dokumentliste) {
        if (valgtTema.temakode === 'BID') {
            return <p> Modia viser ikke dokumenter på temaet Bidrag. </p>;
        } else if (valgtTema.dokumentMetadata.length > 0) {
            return <div>{dokumentliste}<TidligereDokumenter /></div>;
        }

        return (<p> Det finnes ingen dokumenter på dette temaet. Modia viser kun dokumenter etter dd.måned åååå
            (prodsettingsdato). Du kan gå til Gosys for å se eldre dokumenter</p>);
    }

    render() {
        const { sakstema, valgtTema, velgSak, brukerNavn, visSide } = this.props;
        const dokumenter = sakstema.reduce((acc, tema) => acc.concat(tema.dokumentMetadata), []);

        const dokumentliste = valgtTema.temakode === 'alle' ?
            <DokumentListe visTema="true" dokumentMetadata={dokumenter} brukerNavn={brukerNavn} /> :
            <DokumentListe visTema="false" dokumentMetadata={valgtTema.dokumentMetadata} brukerNavn={brukerNavn} />;
        return (
            <div className="sakstema-container">
                <section className="saksoversikt-liste">
                    <SakstemaListe sakstema={sakstema} velgSak={velgSak} valgtTema={valgtTema}/>
                </section>
                <section className="saksoversikt-innhold side-innhold">
                    <ViktigAViteLenke valgtTema={valgtTema} visSide={visSide}/>
                    {this._visningDokumentliste(valgtTema, dokumentliste)}
                </section>
            </div>
        );
    }
}

SakstemaPage.propTypes = {
    sakstema: pt.array.isRequired,
    valgtTema: pt.string.isRequired,
    velgSak: pt.string,
    brukerNavn: pt.string,
    visSide: pt.func.isRequired
};

export default SakstemaPage;
