import React, { Component } from 'react';
import Ajax from '../../utils/ajax';

class DelvisSvar extends Component {
    constructor(props) {
        super(props);
        this.svarDelvis = this.svarDelvis.bind(this);
        this.handleSvarEndring = this.handleSvarEndring.bind(this);
        console.log(props);
        this.state = {
            svarValue: ''
        };
    }

    svarDelvis() {
        console.log(this.state.svarValue);
        const url = `/modiabrukerdialog/rest/personer/${this.props.fodselsnummer}/traader/${this.props.traadId}/henvendelser/${this.props.henvendelseId}/ferdigstill`;
        const data = JSON.stringify({ svar: this.state.svarValue });
        const svarDelvisPromise = Ajax.put(url, data);
        svarDelvisPromise.done(() => {
            console.log('Well done!');
            this.props.svarCallback();
        });

        svarDelvisPromise.fail(err => {
            console.err(err);
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
                <a>Avbryt</a>
            </div>
        );
    }
}

DelvisSvar.propTypes = {
    henvendelseId: React.PropTypes.string.isRequired,
    sporsmal: React.PropTypes.string.isRequired,
    fodselsnummer: React.PropTypes.string.isRequired,
    traadId: React.PropTypes.string.isRequired,
    svarCallback: React.PropTypes.func.isRequired
};

export default DelvisSvar;
