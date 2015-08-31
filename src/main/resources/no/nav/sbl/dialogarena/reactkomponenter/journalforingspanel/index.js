import React from 'react';
import TypeValg from './typevalg';
import SakerListe from './sakerliste';
import LukkKnapp from './lukkknapp';

class JournalforingsPanel extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        console.log('saker', this.props.saker);
        return (
            <div className="journalforings-panel">
                <h2 className="header">Journalføring</h2>
                <TypeValg></TypeValg>

                <SakerListe saker={this.props.saker}></SakerListe>
                <LukkKnapp></LukkKnapp>
            </div>
        );
    }
}

export default JournalforingsPanel;

