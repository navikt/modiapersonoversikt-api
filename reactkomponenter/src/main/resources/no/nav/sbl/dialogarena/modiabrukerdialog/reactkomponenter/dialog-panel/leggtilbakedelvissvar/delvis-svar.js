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
const TEMAGRUPPE_PLACEHOLDER = 'Velg temagruppe';

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
        this.sendDelsvar = this.sendDelsvar.bind(this);

        this.state = {
            svarValue: window.dialogTekst,
            valgtTemagruppe: TEMAGRUPPE_PLACEHOLDER,
            panelState: panelState.INITIALIZED,
            valideringsFeil: false,
            tekstValidFiel: false,
            temagruppeValidFeil: false
        };
        window.dialogTekst = undefined;
    }

    sendDelsvar() {
        const url = `${API_BASE_URL}dialog/${this.props.fodselsnummer}/delvis-svar`;
        const data = JSON.stringify({
            fritekst: this.state.svarValue,
            traadId: this.props.traadId,
            behandlingsId: this.props.henvendelseId, // TODO riktig navn på props her. Denne ble feilaktig kallt henvendelseId da delsvar ble laget, men dette er egentlig behandlingsId
            temagruppe: this.state.valgtTemagruppe,
            oppgaveId: this.props.oppgaveId
        });
        return Ajax.post(url, data);
    }

    validerParametere() {
        const ugyldigTemagruppe = this.state.valgtTemagruppe === TEMAGRUPPE_PLACEHOLDER;
        const ugyldigTekst = this.state.svarValue.length === 0;
        const valideringfeil = ugyldigTemagruppe || ugyldigTekst;
        this.setState({
            temagruppeValidFeil: ugyldigTemagruppe,
            tekstValidFeil: ugyldigTekst
        });
        return !valideringfeil;
    }

    svarDelvis() {
        if (this.state.panelState === panelState.PENDING) {
            return;
        }

        if (this.validerParametere()) {
            this.leggTilbakeDelvisesvar();
        }
    }

    leggTilbakeDelvisesvar() {
        this.setState({
            panelState: panelState.PENDING
        });

        Promise.resolve()
            .then(this.sendDelsvar)
            .then(() => {
                this.setState({
                    panelState: panelState.INITIALIZED,
                });
                this.props.svarCallback();
            })
            .catch((err) => {
                console.log(err);
                this.setState({
                    panelState: panelState.ERROR,
                    feilmelding: `Teknisk feil: ${err[0].message}`
                });
            });
    }

    velgTemagruppe(event) {
        this.setState({
            valgtTemagruppe: event.target.value,
            temagruppeValidFeil: false
        });
    }

    handleSvarEndring(event) {
        this.setState({
            svarValue: event.target.value,
            tekstValidFeil: false
        });
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
            <div />;
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
        return (
            <AlertStripe type="advarsel" solid>
                <div role="alert">
                    {this.state.tekstValidFeil && <p>Tekstfeltet kan ikke være tomt.</p>}
                    {this.state.temagruppeValidFeil && <p>Temagruppe må være valgt.</p>}
                </div>
            </AlertStripe>
        );
    }

    render() {
        const valgTemagruppe = this.lagTemagruppeValg();
        const feilmeldingModal = this.lagFeilmeldingModalHvisFeil();
        const hiddenLabel = <span className="vekk">Skriv delsvar</span>;
        const visValideringsAlert = this.state.tekstValidFeil || this.state.temagruppeValidFeil;
        return (
            <div>
                <Skrivestotte
                    {...this.props.skrivestotteprops}
                    tekstfeltId={this.textId} ref={(input) => { this.skrivestoote = input; }}
                />

                <h3>Legg tilbake med delsvar</h3>

                <TraadVisning traad={this.props.traad} />

                {visValideringsAlert && this.valideringsFeilmelding()}

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
                        placeholder={`Alt du skriver i denne boksen blir synlig for bruker når endelig svar foreligger og man har trykket «Del med bruker».`}
                    />
                </div>

                <div className="temagruppe-velger">
                    <h3>Velg temagruppe</h3>
                    <select
                        onChange={this.velgTemagruppe}
                        className={this.state.temagruppeValidFeil === true ? 'valideringFeltFeil' : ''}
                    >
                        <option value={TEMAGRUPPE_PLACEHOLDER}>{TEMAGRUPPE_PLACEHOLDER}</option>
                        {valgTemagruppe}
                    </select>
                </div>

                <button
                    className="knapp-hoved-stor submit"
                    onClick={this.svarDelvis}
                >
                    Skriv delsvar og legg tilbake
                </button>
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
