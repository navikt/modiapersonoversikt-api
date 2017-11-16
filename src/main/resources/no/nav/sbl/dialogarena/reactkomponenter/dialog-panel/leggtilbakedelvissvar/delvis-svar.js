import React, {Component} from 'react';
import PT from 'prop-types';
import Ajax from '../../utils/ajax';
import TraadVisning from '../traadvisning/traadvisning-module';

const API_BASE_URL = '/modiabrukerdialog/rest/';

class DelvisSvar extends Component {
    constructor(props) {
        super(props);
        this.svarDelvis = this.svarDelvis.bind(this);
        this.handleSvarEndring = this.handleSvarEndring.bind(this);
        this.velgTemagruppe = this.velgTemagruppe.bind(this);
        this.state = {
            svarValue: '',
            valgtTemagruppe: 'Velg Temagruppe',
            valideringFeil: false
        };
    }

    ferdigstillHenvendelse() {
        const url = `${API_BASE_URL}personer/${this.props.fodselsnummer}/traader/${this.props.traadId}/henvendelser/${this.props.henvendelseId}/delvisSvar`;
        const data = JSON.stringify({
            svar: this.state.svarValue
        });
        return Ajax.post(url, data);
    }

    leggTilbakeOppgave() {
        const url = `${API_BASE_URL}oppgaver/${this.props.oppgaveId}/leggTilbake`;
        const data = JSON.stringify({
            temagruppe: this.state.valgtTemagruppe,
            beskrivelse: `Henvendelsen er besvart delvis og lagt tilbake med ny temagruppe ${this.state.valgtTemagruppe}`
        });
        return Ajax.post(url, data);
    }

    validTemagruppe(){
        return this.state.valgtTemagruppe !== 'Velg Temagruppe';
    }

    svarDelvis() {
        if (this.validTemagruppe()) {
            this.leggDelvisesvar();
        } else {
            this.setState({ valideringFeil: true })
        }
    }

    leggDelvisesvar(){
        const ferdigstillHenvendelsePromise = this.ferdigstillHenvendelse();
        const leggTilbakeOppgavePromise = this.leggTilbakeOppgave();
        Promise.all([ferdigstillHenvendelsePromise, leggTilbakeOppgavePromise])
            .then(() => {
                    this.props.svarCallback();
                }, (err) => {
                    console.error(err);
                }
            );
    }

    velgTemagruppe(event) {
        const valgtTemagruppe = event.target.value ;
        this.setState({ valgtTemagruppe: valgtTemagruppe ,
            valideringFeil: this.validTemagruppe()
        });
    }

    handleSvarEndring(event) {
        this.setState({ svarValue: event.target.value });
    }

    render() {
        const valgTemagruppe = Object.keys(this.props.temagruppeMapping)
                                     .map((key) =>
                                                    <option key={key} value={key}>
                                                        { this.props.temagruppeMapping[key] }
                                                    </option>);

        return (
            <div>
                <h3>Legg tilbake med delvis svar</h3>

                <TraadVisning traad={this.props.traad} />

                <div className="svar">
                    <div className="svar-overskrift-boks">
                        <h1 className="overskrift medium"><span>Delvis svar</span></h1>
                    </div>
                    <textarea
                        value={this.state.svarValue}
                        onChange={this.handleSvarEndring}
                        className="svar-tekst"
                        placeholder="Svaret blir ikke synlig for brukeren"
                    >
                    </textarea>
                </div>

                <div className="temagruppe-velger">
                    <h3>Velg temagruppe</h3>
                    <select
                        onChange={this.velgTemagruppe}
                        className={this.state.valideringFeil === true ? 'valideringFeltFeil' : ''}
                    >
                        <option value="Velg Temagruppe">Velg Temagruppe</option>
                        {valgTemagruppe}
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
    traad: PT.array.isRequired,
    henvendelseId: PT.string.isRequired,
    sporsmal: PT.string.isRequired,
    fodselsnummer: PT.string.isRequired,
    traadId: PT.string.isRequired,
    temagruppe: PT.string.isRequired,
    svarCallback: PT.func.isRequired,
    oppgaveId: PT.string.isRequired,
    avbrytCallback: PT.func.isRequired,
    opprettetDato: PT.string.isRequired,
    temagruppeMapping: PT.shape({
        temagruppeKode: PT.string,
        temagruppeNavn: PT.string
    }).isRequired
};

export default DelvisSvar;
