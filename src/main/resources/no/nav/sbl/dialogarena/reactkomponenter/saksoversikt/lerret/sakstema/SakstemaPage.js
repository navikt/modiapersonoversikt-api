import React, { PropTypes as PT } from 'react';
import SakstemaListe from './SakstemaListe';
import { connect } from 'react-redux';
import VisningDokumentliste from './dokumentliste/visning-dokumentliste';
import { FormattedMessage } from 'react-intl';

class SakstemaPage extends React.Component {

    render() {
        const { sakstema, valgtTema, velgSak, brukerNavn, visSide, velgJournalpost, filtreringsvalg } = this.props;

        if (this.props.sakstema.length === 0) {
            return (
                <div className="ingen-sakstemaer">
                    <img className="ingensakstemaerimage"
                         src="/modiabrukerdialog/img/saksoversikt/dokument_flyttet.svg"/>
                    <h1 className="ingen-sakstemaer-tekst">
                        <FormattedMessage id="sakslamell.ingensaker"/>
                    </h1>
                </div>);
        }

        return (
            <div className="sakstema-container">
                <section className="saksoversikt-liste">
                    <SakstemaListe sakstema={sakstema} velgSak={velgSak} valgtTema={valgtTema}/>
                </section>
                <section className="saksoversikt-innhold side-innhold">
                    <h2 className="vekk">{valgtTema.temanavn}</h2>

                    <VisningDokumentliste visSide={visSide} sakstema={sakstema} valgtTema={valgtTema}
                                          brukerNavn={brukerNavn} velgJournalpost={velgJournalpost}
                                          filtreringsvalg={filtreringsvalg}/>
                </section>
            </div>
        );
    }
}

SakstemaPage.propTypes = {
    sakstema: PT.array.isRequired,
    valgtTema: PT.object,
    velgSak: PT.func,
    brukerNavn: PT.string,
    visSide: PT.func.isRequired,
    velgJournalpost: PT.func,
    filtreringsvalg: PT.object.isRequired
};

export default connect()(SakstemaPage);
