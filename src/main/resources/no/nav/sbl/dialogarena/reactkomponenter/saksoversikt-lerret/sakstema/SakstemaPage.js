import React from 'react';
import SakstemaListe from './SakstemaListe';
import DokumentListe from './dokumentliste/dokumentliste'

class SakstemaPage extends React.Component {
    render() {
        const {store} = this.props;
        const sakstema = store.state.sakstema;

        const valgtTema = sakstema.find((tema)=>tema.temakode === store.state.valgtTema);
        const dokumenter = sakstema.reduce((acc, tema) => {
            return acc.concat(tema.dokumentMetadata);
        }, []);
        const dokumentliste = typeof valgtTema === 'undefined' ?
            <DokumentListe visTema="true" dokumentMetadata={dokumenter}></DokumentListe> :
            <DokumentListe visTema="false" dokumentMetadata={valgtTema.dokumentMetadata}></DokumentListe>;

        const tekster = store.state.tekster;

        return (
            <div>
                <section className="saksoversikt-liste">
                    <SakstemaListe tekster={tekster} sakstema={sakstema} erValgt={this.props.erValgt.bind(this)}
                                   velgSak={this.props.velgSak} />
                </section>
                <section className="saksoversikt-innhold">
                    {dokumentliste}
                </section>
            </div>
        )
    };
}

export default SakstemaPage;

