import React, { PropTypes as PT } from 'react';
import SakstemaListe from './SakstemaListe';
import ViktigAViteLenke from './../viktigavite/ViktigAViteLenke'
import VisningDokumentliste from './dokumentliste/visning-dokumentliste'
import { FormattedMessage } from 'react-intl';

class SakstemaPage extends React.Component {
    render() {
        const { sakstema, valgtTema, velgSak, brukerNavn, visSide, velgJournalpost } = this.props;

        if(this.props.sakstema.length === 0) {
            return (
                <div className="ingen-sakstemaer">
                    <FormattedMessage id="sakslamell.ingensaker"/>
                </div>);
        }

        return (
            <div className="sakstema-container">
                <section className="saksoversikt-liste">
                    <SakstemaListe sakstema={sakstema} velgSak={velgSak} valgtTema={valgtTema}/>
                </section>
                <section className="saksoversikt-innhold side-innhold">
                    <ViktigAViteLenke valgtTema={valgtTema} visSide={visSide}/>
                    <VisningDokumentliste visSide={visSide} sakstema={sakstema} valgtTema={valgtTema} brukerNavn={brukerNavn} velgJournalpost={velgJournalpost}/>
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
