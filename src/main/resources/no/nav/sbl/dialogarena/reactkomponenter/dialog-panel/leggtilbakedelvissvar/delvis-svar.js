import React, { Component } from 'react';
import PT from 'prop-types';
import Ajax from '../../utils/ajax';

class DelvisSvar extends Component {
    constructor(props) {
        super(props);
        this.svarDelvis = this.svarDelvis.bind(this);
        this.handleSvarEndring = this.handleSvarEndring.bind(this);
        this.state = {
            svarValue: ''
        };
    }

    ferdigstillHenvendelse() {
        const url = `/modiabrukerdialog/rest/personer/${this.props.fodselsnummer}/traader/${this.props.traadId}/henvendelser/${this.props.henvendelseId}/ferdigstill`;
        const data = JSON.stringify({ svar: this.state.svarValue });
        return Ajax.put(url, data);
    }

    leggTilbakeOppgave() {
        const url = `/modiabrukerdialog/rest/oppgaver/${this.props.oppgaveId}`;
        const data = JSON.stringify({});
        return Ajax.put(url, data);
    }

    svarDelvis() {
        const ferdigstillHenvendelsePromise = this.ferdigstillHenvendelse();
        const leggTilbakeOppgavePromise = this.leggTilbakeOppgave();
        Promise.all([ferdigstillHenvendelsePromise, leggTilbakeOppgavePromise]).then(() => {
            console.log('Well done!');
            this.props.svarCallback();
        }, (err) => {
            console.error(err);
        });
    }

    handleSvarEndring(event) {
        this.setState({ svarValue: event.target.value });
    }

    render() {
        const sporsmal = this.props.sporsmal.split('\n').map((paragraf, index) =>
            <p key={`paragraf-${index}`}>{paragraf}</p>);
        return (
            <div>
                <h3>Legg tilbake med delvis svar</h3>
                <h2>Spørsmål</h2>
                <h3>FAMILIE</h3>
                <p>22.03.2017 kl 10:22</p>
                <div className="sporsmaal">
                    {sporsmal}
                </div>
                <div className="svar">
                    <div className="svar-overskrift-boks">
                        <h1 className="overskrift medium"><span>Delvis svar</span></h1>
                    </div>
                    <textarea
                        value={this.state.svarValue}
                        onChange={this.handleSvarEndring}
                        className="svar-tekst"
                        placeholder="Svaret blir ikke synlig for brukeren">
                    </textarea>
                </div>

                <div className="temagruppe-velger">
                    <h3>Velg temagruppe</h3>
                    <select>
                        <option>Arbeid</option>
                        <option>Familie</option>
                        <option>Hjelpemidler</option>
                    </select>
                </div>
                <a
                    className="knapp-hoved-stor submit"
                    role="button"
                    onClick={this.svarDelvis}
                >
                    Svar delvis og legg tilbake
                </a>
                <a
                    role="button"
                    onClick={this.props.avbrytCallback}
                    className="avbryt"
                >
                    Avbryt
                </a>
            </div>
        );
    }
}

DelvisSvar.propTypes = {
    henvendelseId: PT.string.isRequired,
    sporsmal: PT.string.isRequired,
    fodselsnummer: PT.string.isRequired,
    traadId: PT.string.isRequired,
    temagruppe: PT.string.isRequired,
    svarCallback: PT.func.isRequired,
    avbrytCallback: PT.func.isRequired,
    oppgaveId: PT.string.isRequired
};

export default DelvisSvar;
