import React, {Component} from 'react';
import PT from 'prop-types';
import Ajax from '../../utils/ajax';
import TraadVisning from '../traadvisning/traadvisning-module';
import TekniskFeil from '../../feilmeldingsmodaler/teknisk-feil';
import {Textarea} from 'nav-frontend-skjema';
import AlertStripe from 'nav-frontend-alertstriper';
import Skrivestotte from '../../skrivestotte/skrivestotte-module';
import {generateId} from '../../utils/utils-module';
import {skrivestotteprops} from '../props';

const API_BASE_URL = '/modiabrukerdialog/rest/';
const DEFAULT_VALGT_TEMAGRUPPE = 'Velg Temagruppe';

const panelState = {
    PENDING: 'PENDING',
    ERROR: 'ERROR',
    INITIALIZED: 'INITIALIZED'
};

class DelvisSvar extends Component {
    constructor(props) {
        super(props);
        this.textId = generateId('textarea');
        this.svarDelvis = this.svarDelvis.bind(this);
        this.handleSvarEndring = this.handleSvarEndring.bind(this);
        this.velgTemagruppe = this.velgTemagruppe.bind(this);
        this.lagFeilmeldingModalHvisFeil = this.lagFeilmeldingModalHvisFeil.bind(this);
        this.feilmeldingCloseButtonCallback = this.feilmeldingCloseButtonCallback.bind(this);
        this.state = {
            svarValue: '',
            valgtTemagruppe: DEFAULT_VALGT_TEMAGRUPPE,
            panelState: panelState.INITIALIZED,
            valideringsFeil: false,
            tekstValidFiel: false,
            temagruppeValidFeil: false
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

    validate() {
        const ugyldigTemagruppe = this.state.valgtTemagruppe === DEFAULT_VALGT_TEMAGRUPPE;
        const ugyldigTekst = this.state.svarValue.length === 0;
        const valideringfeil = ugyldigTemagruppe || ugyldigTekst;
        this.setState({
            temagruppeValidFeil: ugyldigTemagruppe,
            tekstValidFiel: ugyldigTekst,
            valideringsFeil: valideringfeil
        });
        return valideringfeil;
    }

    svarDelvis() {
        this.validate() && this.leggTilbakeDelvisesvar();
    }

    leggTilbakeDelvisesvar() {
        const ferdigstillHenvendelsePromise = this.ferdigstillHenvendelse();
        const leggTilbakeOppgavePromise = this.leggTilbakeOppgave();
        Promise.all([ferdigstillHenvendelsePromise, leggTilbakeOppgavePromise]).then(() => {
            this.props.svarCallback();
        }, (err) => {
            this.setState({
                panelState: panelState.ERROR,
                feilmelding: `Teknisk feil: ${err[0].message}`
            });
        });
    }

    velgTemagruppe(event) {
        const ugyldigTemagruppe = event.target.value === DEFAULT_VALGT_TEMAGRUPPE;
        this.setState({
            valgtTemagruppe: event.target.value,
            temagruppeValidFeil: ugyldigTemagruppe,
            valideringsFeil: ugyldigTemagruppe || this.state.tekstValidFiel
        });
    }

    handleSvarEndring(event) {
        this.setState({svarValue: event.target.value});
    }

    feilmeldingCloseButtonCallback() {
        this.setState({feilmelding: null, panelState: panelState.INITIALIZED});
    }

    lagFeilmeldingModalHvisFeil() {
        return this.state.panelState === panelState.ERROR ?
            (<TekniskFeil
                closeButtonCallback={this.feilmeldingCloseButtonCallback}
                tekst={this.state.feilmelding} isOpen
            />) :
            <div/>;
    }

    lagTemagruppeValg() {
        return (Object.keys(this.props.temagruppeMapping)
                .map((key) =>
                    <option key={key} value={key}>
                        {this.props.temagruppeMapping[key]}
                    </option>)
        );
    }

    valideringsFeilmelding() {
        return <AlertStripe type='advarsel' solid={true}>
            <div>
                {this.state.tekstValidFiel && <p>Tekstfeltet kan ikke være tomt.</p>}
                {this.state.temagruppeValidFeil && <p>Temagruppe må være valgt.</p>}
            </div>
        </AlertStripe>;
    }



    render() {
        const valgTemagruppe = this.lagTemagruppeValg();
        const feilmeldingModal = this.lagFeilmeldingModalHvisFeil();
        const hiddenLabel = <span className="vekk">Skriv delsvar</span>;
        return (
            <div>
                <Skrivestotte {...this.props.skrivestotteprops} tekstfeltId={this.textId} ref={(input) => {
                    this.skrivestoote = input;
                }}/>

                <h3>Legg tilbake med delsvar</h3>

                <TraadVisning traad={this.props.traad}/>

                {this.state.valideringsFeil && this.valideringsFeilmelding()}

                <div className="svar">
                    <div className="svar-overskrift-boks">
                        <h1 className="overskrift medium"><span>Skriv delsvar</span></h1>
                        <button
                            className="skrivestotteToggle"
                            id="skrivestotteToggler10c"
                            title="Hurtigtast: ALT + C"
                            onClick={() => { this.skrivestoote.vis(); }}
                        >
                            <span className="vekk">Skrivestøtte.</span>
                        </button>
                    </div>
                    <Textarea
                        className="expandingtextarea"
                        value={this.state.svarValue}
                        onChange={this.handleSvarEndring}
                        maxLength={5000}
                        id={this.textId}
                        label={hiddenLabel}
                        placeholder="Svaret blir ikke synlig for brukeren"
                    />
                </div>

                <div className="temagruppe-velger">
                    <h3>Velg temagruppe</h3>
                    <select
                        onChange={this.velgTemagruppe}
                        className={this.state.temagruppeValidFeil === true ? 'valideringFeltFeil' : ''}
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
                    Skriv delsvar og legg tilbake
                </a>
                {feilmeldingModal}
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
    skrivestotteprops: skrivestotteprops.isRequired,
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
