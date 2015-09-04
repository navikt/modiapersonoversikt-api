import React from 'react';
import TilbakeKnapp from './tilbakeknapp';
import JournalforKnapp from './journalforknapp';

class JournalforSak extends React.Component {
    render() {
        var sak = this.props.sak;
        var header = sak.sakstype === 'GEN'? "Generelle saker" : "Fagsaker";
        return(
            <div className="detaljer-sak">
                <h3 className="sub-header">{header}</h3>
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
                <TilbakeKnapp tilbake={this.props.tilbake}></TilbakeKnapp>
                <JournalforKnapp fnr={this.props.fnr} traadId={this.props.traadId} sak={sak}/>
                </div>
        );
    }
}

export default JournalforSak;

