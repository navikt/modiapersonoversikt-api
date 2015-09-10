import React from 'react';
import TilbakeKnapp from './tilbakeknapp';
import JournalforKnapp from './journalforknapp';
import WicketSender from './../reactwicketmixin/wicketsender.js';

//var React = require('react');
//var TilbakeKnapp= require('./tilbakeknapp');
//var JournalforKnapp = require('./journalforknapp');
//var WicketSender = require('./../reactwicketmixin/wicketsender.js');

class JournalforSak extends React.Component {
    constructor(props) {
        super(props);
        this.traadJournalfort = this.traadJournalfort.bind(this);
    }
    traadJournalfort() {
        WicketSender(this.props.wicketurl, this.props.wicketcomponent, 'traadJournalfort');

    }
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
                <JournalforKnapp fnr={this.props.fnr} traadId={this.props.traadId} sak={sak} traadJournalfort={this.traadJournalfort}/>
                </div>
        );
    }
}

export default JournalforSak;
//module.exports='Journalforsak';

