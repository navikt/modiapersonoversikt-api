import React, { PropTypes as pt } from 'react';
import SakstemaListe from './SakstemaListe';
import ViktigAViteLenke from './../viktigavite/ViktigAViteLenke'
import VisningDokumentliste from './dokumentliste/visning-dokumentliste'
import { FormattedMessage } from 'react-intl';

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
