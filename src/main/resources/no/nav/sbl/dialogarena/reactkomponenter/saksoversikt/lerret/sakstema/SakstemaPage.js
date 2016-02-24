import React, { PropTypes as PT } from 'react';
import SakstemaListe from './SakstemaListe';
import DokumentListe from './dokumentliste/dokumentliste'

class SakstemaPage extends React.Component {
    render() {
        const { sakstema, valgtTema, velgSak } = this.props;

        const dokumenter = sakstema.reduce((acc, tema) => {
            return acc.concat(tema.dokumentMetadata);
        }, []);
        const dokumentliste = typeof valgtTema === 'undefined' ?
            <DokumentListe visTema="true" dokumentMetadata={dokumenter}></DokumentListe> :
            <DokumentListe visTema="false" dokumentMetadata={valgtTema.dokumentMetadata}></DokumentListe>;

        return (
            <div>
                <section className="saksoversikt-liste">
                    <SakstemaListe sakstema={sakstema} velgSak={velgSak} valgtTema={valgtTema}/>
                </section>
                <section className="saksoversikt-innhold">
                    {dokumentliste}
                </section>
            </div>
        )
    };
}

SakstemaPage.propTypes = {
    sakstema: PT.array.isRequired
};

export default SakstemaPage;

