import React, { Component } from 'react';

class LeggTilbakeDelvisSvarPanel extends Component {
    render() {
        return (
            <div>
                <h3>Legg tilbake med delvis svar</h3>
                <h2>Spørsmål</h2>
                <h3>FAMILIE</h3>
                <p>22.03.2017 kl 10:22</p>
                <p className="sporsmaal">
                    Hei. Jeg har blitt kontakta av en saksbehandler som lurte på om jeg heller ville ha engangsstønad enn foreldrepenger. Dette fordi hun mente jeg ville få mer utbetalt om jeg valgte denne løsningen. Vi ønsker at far skal ta ut fedrekvoten, og eventuelt noe mer, noe som utgjør en sum høyere enn engangstønaden. Vi ønsker derfor foreldrepenger om dette er mulig.
                </p>
                <div className="svar">
                    <div className="svar-overskrift-boks">
                        <h1 className="overskrift medium"><span>Delvis svar</span></h1>
                    </div>
                    <textarea className="svar-tekst" placeholder="Svaret blir ikke synlig for brukeren"></textarea>
                </div>

                <div className="temagruppe-velger">
                    <h3>Velg temagruppe</h3>
                    <select>
                        <option>Arbeid</option>
                        <option>Familie</option>
                        <option>Hjelpemidler</option>
                    </select>
                </div>
                <a className="knapp-hoved-stor submit" role="button">Svar delvis og legg tilbake</a>
                <a>Avbryt</a>
            </div>
        );
    }
}

export default LeggTilbakeDelvisSvarPanel;
