import React from 'react';
import TilbakeKnapp from './tilbakeknapp';
import JournalforKnapp from './journalforknapp';

class JournalforSak extends React.Component {
    render() {
        var sak = this.props.sak;
        console.log('sak', sak);
        var header2 = sak.sakstype === 'GEN'? "Generelle saker":"Fagsaker";
        return(
            <div className="detaljer-sak">
                <h3 className="sub-header">{header2}</h3>
                <div>
                    <h3 className="header-detaljer">{sak.temaKode}</h3>
                    <div className="info-bar">
                        <span className="text-cell">Saksid</span>

                        <span className="text-cell">Opprettet</span>
                        <span className="text-cell">Fagsystem</span>
                    </div>
                    <div className="info-sak">
                        <span className="text-cell">{sak.saksIdVisning}</span>
                        <span className="text-cell">{sak.opprettetDatoFormatert}</span>
                        <span className="text-cell">{sak.fagsystemKode}</span>
                    </div>
                </div>
                <TilbakeKnapp tilbake={this.props.tilbake}></TilbakeKnapp>
                <JournalforKnapp></JournalforKnapp>
                </div>
        );
    }
}

export default JournalforSak;

