import React from 'react';
import TilbakeKnapp from './tilbake-knapp';
import JournalforKnapp from './journalfor-knapp';
import WicketSender from './../react-wicket-mixin/wicket-sender';

class JournalforSak extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
        this.state.feilmeldinger = [];

        this.traadJournalfort = this.traadJournalfort.bind(this);
        this.journalforingFeilet = this.journalforingFeilet.bind(this);
        this.lagFeedbackPanel = this.lagFeedbackPanel.bind(this);
    }

    traadJournalfort() {
        WicketSender(this.props.wicketurl, this.props.wicketcomponent, 'traadJournalfort');
    }

    journalforingFeilet() {
        this.setState({
            feilmeldinger: ['Noe gikk galt. Det er dessverre ikke mulig å journalføre henvendelser, prøv igjen senere.']
        });
    }

    lagFeedbackPanel() {
        const feilmeldinger = this.state.feilmeldinger;
        if (feilmeldinger.length === 0)return;
        else {
            const feilmeldingerElement = feilmeldinger.map((feilmelding) => <li className="feedbackPanelERROR">{feilmelding}</li>);
            return (<div className="feedback" role="alert" aria-live="assertive" aria-atomic="true">
                <ul className="feedbackPanel">
                    {feilmeldingerElement}
                </ul>
            </div>);
        }
    }

    render() {
        const sak = this.props.sak;
        const header = sak.sakstype === 'GEN' ? "Generelle saker" : "Fagsaker";
        const feedbackpanel = this.lagFeedbackPanel();

        return (
            <div className="detaljer-sak">
                <TilbakeKnapp tilbake={this.props.tilbake} ref="tilbakeknapp"></TilbakeKnapp>

                <h3 className="sub-header">{header}</h3>
                {feedbackpanel}
                <div>
                    <h3 className="header-detaljer">{sak.temaNavn}</h3>

                    <div className="info-bar">
                        <span className="text-cell">Saksid</span>
                        <span className="vekk">'|'</span>
                        <span className="text-cell">Opprettet</span>
                        <span className="vekk">'|'</span>
                        <span className="text-cell">Fagsystem</span>
                    </div>
                    <div className="info-sak">
                        <span className="text-cell">{sak.saksIdVisning}</span>
                        <span className="vekk">'|'</span>
                        <span className="text-cell">{sak.opprettetDatoFormatert}</span>
                        <span className="vekk">'|'</span>
                        <span className="text-cell">{sak.fagsystemNavn}</span>
                    </div>
                </div>

                <JournalforKnapp fnr={this.props.fnr} traadId={this.props.traadId} sak={sak}
                                 traadJournalfort={this.traadJournalfort} feiletCallback={this.journalforingFeilet}/>
            </div>
        );
    }
}

export default JournalforSak;