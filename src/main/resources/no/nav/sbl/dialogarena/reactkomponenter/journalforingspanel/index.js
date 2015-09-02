import React from 'react';
import VelgSak from './velgsak';
import JournalforSak from './journalforsak';
import LukkKnapp from './lukkknapp';

const VELG_SAK = 'VELG_SAK';
const JOURNALFOR = 'JOURNALFOR';

class JournalforingsPanel extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            aktivtVindu: VELG_SAK,
            valgtSak: null
        };
        this.velgSak = this.velgSak.bind(this);
        this.tilbake = this.tilbake.bind(this);
    }

    velgSak(sak) {
        this.setState({
            aktivtVindu: JOURNALFOR,
            valgtSak: sak
        })
    }

    tilbake() {
        debugger;
        this.setState({
            aktivtVindu: VELG_SAK,
            valgtSak: null
        })
    }

    render() {
        let aktivtVindu;
        if (this.state.aktivtVindu === VELG_SAK) {
            aktivtVindu = <VelgSak saker={this.props.saker} velgSak={this.velgSak}/>;
        } else {
            aktivtVindu = <JournalforSak sak={this.state.valgtSak} tilbake={this.tilbake}/>
        }
        return (
            <div className="journalforings-panel">
                <h2 className="header">Journalføring</h2>
                {aktivtVindu}
                <LukkKnapp></LukkKnapp>
            </div>
        );
    }
}

export default JournalforingsPanel;

