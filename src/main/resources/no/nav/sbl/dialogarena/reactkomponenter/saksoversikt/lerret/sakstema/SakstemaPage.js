import React, { PropTypes as PT } from 'react';
import SakstemaListe from './SakstemaListe';
import ViktigAViteLenke from './../viktigavite/ViktigAViteLenke'
import VisningDokumentliste from './dokumentliste/visning-dokumentliste'

class SakstemaPage extends React.Component {
    render() {
        const { sakstema, valgtTema, velgSak, brukerNavn, visSide } = this.props;

        return (
            <div className="sakstema-container">
                <section className="saksoversikt-liste">
                    <SakstemaListe sakstema={sakstema} velgSak={velgSak}
                                   valgtTema={valgtTema}/>
                </section>
                <section className="saksoversikt-innhold side-innhold">
                    <ViktigAViteLenke valgtTema={valgtTema} visSide={visSide}/>
                    <VisningDokumentliste sakstema={sakstema} valgtTema={valgtTema} brukerNavn={brukerNavn}/>
                </section>
            </div>
        );
    }
}

SakstemaPage.propTypes = {
    sakstema: PT.array.isRequired,
    visSide: PT.func.isRequired

};

export default SakstemaPage;
