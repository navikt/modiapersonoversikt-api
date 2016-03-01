import React, { PropTypes as PT } from 'react';
import SakstemaListe from './SakstemaListe';
import DokumentListe from './dokumentliste/dokumentliste'
import ViktigAViteLenke from './../viktigavite/ViktigAViteLenke'
import TidligereDokumenter from './dokumentliste/tidligere-dokumenter';

class SakstemaPage extends React.Component {
    render() {
        const { sakstema, valgtTema, velgSak, brukerNavn, visSide } = this.props;
        const dokumenter = sakstema.reduce((acc, tema) => {
            return acc.concat(tema.dokumentMetadata);
        }, []);

        const dokumentliste = valgtTema.temakode !== 'alle' ?
            <DokumentListe visTema="false"
                           dokumentMetadata={valgtTema.dokumentMetadata}
                           harTilgang={valgtTema.harTilgang}
                           brukerNavn={brukerNavn}/> :
            <DokumentListe visTema="true" dokumentMetadata={dokumenter}
                           harTilgang={valgtTema.harTilgang}
                           brukerNavn={brukerNavn}/>;
        return (
            <div>
                <section className="saksoversikt-liste">
                    <SakstemaListe sakstema={sakstema} velgSak={velgSak} valgtTema={valgtTema}/>
                </section>
                <section className="saksoversikt-innhold side-innhold">
                    <ViktigAViteLenke valgtTema={valgtTema} visSide={visSide} />
                    {dokumentliste}
                    <TidligereDokumenter />
                </section>
            </div>
        )
    };
}

SakstemaPage.propTypes = {
    sakstema: PT.array.isRequired,
    visSide: PT.func.isRequired
};

export default SakstemaPage;

