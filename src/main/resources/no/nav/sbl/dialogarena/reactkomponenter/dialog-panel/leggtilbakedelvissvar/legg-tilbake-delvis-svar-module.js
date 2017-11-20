import React, { Component } from 'react';
import PT from 'prop-types';
import wicketSender from '../../react-wicket-mixin/wicket-sender';
import DelvisSvar from './delvis-svar';

class LeggTilbakeDelvisSvarPanel extends Component {
    constructor(props) {
        super(props);
        this.svarCallback = this.svarCallback.bind(this);
        this.avbrytCallback = this.avbrytCallback.bind(this);
        this.startNyDialog = this.startNyDialog.bind(this);
        this.state = {
            erUnderArbeid: true
        };
    }

    svarCallback() {
        this.setState({ erUnderArbeid: false });
        wicketSender(this.props.wicketurl, this.props.wicketcomponent, this.props.svarDelvisCallbackId);
    }

    avbrytCallback() {
        wicketSender(this.props.wicketurl, this.props.wicketcomponent, this.props.avbrytCallbackId);
    }

    startNyDialog() {
        wicketSender(this.props.wicketurl, this.props.wicketcomponent, this.props.startNyDialogId);
    }

    lagKvittering() {
        return (
            <div className="kvittering">
                <div className="robust-ikon-gront-sjekk" />
                <h2 className="medium">Delvis svar er registrert</h2>
                <button className="startNyDialog knapp-stor" onClick={this.startNyDialog}>Start Ny Dialog</button>
            </div>
        );
    }

    render() {
        if (this.state.erUnderArbeid) {
            return (<DelvisSvar
                henvendelseId={this.props.henvendelseId}
                sporsmal={this.props.sporsmal}
                fodselsnummer={this.props.fodselsnummer}
                traadId={this.props.traadId}
                svarCallback={this.svarCallback}
                avbrytCallback={this.avbrytCallback}
                oppgaveId={this.props.oppgaveId}
                temagruppe={this.props.temagruppe}
                opprettetDato={this.props.opprettetDato}
                temagruppeMapping={this.props.temagruppeMapping}
                traad={this.props.traad}
                grunnInfo={this.props.grunnInfo}
            />);
        }
        return this.lagKvittering();
    }
}

LeggTilbakeDelvisSvarPanel.propTypes = {
    traad: PT.array.isRequired,
    wicketurl: PT.string.isRequired,
    wicketcomponent: PT.string.isRequired,
    svarDelvisCallbackId: PT.string.isRequired,
    avbrytCallbackId: PT.string.isRequired,
    startNyDialogId: PT.string.isRequired,
    henvendelseId: PT.string.isRequired,
    sporsmal: PT.string.isRequired,
    fodselsnummer: PT.string.isRequired,
    traadId: PT.string.isRequired,
    temagruppe: PT.string.isRequired,
    oppgaveId: PT.string.isRequired,
    opprettetDato: PT.string.isRequired,
    temagruppeMapping: PT.shape({
        temagruppeKode: PT.string,
        temagruppeNavn: PT.string
    }),
    grunnInfo: PT.shape({
        bruker: PT.shape({
            fnr: PT.string,
            fornavn: PT.string,
            etternavn: PT.string,
            navkontor: PT.string
        }),
        Saksbehandler: PT.shape({
            enhet: PT.string,
            fornavn: PT.string,
            etternavn: PT.string
        })
    })
};

export default LeggTilbakeDelvisSvarPanel;
