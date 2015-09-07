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
            saker: [],
            aktivtVindu: VELG_SAK,
            valgtSak: null
        };
        this.velgSak = this.velgSak.bind(this);
        this.tilbake = this.tilbake.bind(this);
    }

    componentDidMount() {
        $.get('/modiabrukerdialog/rest/journalforing/' + this.props.fnr + '/saker').then(
            okCallback.bind(this),
            feiletCallback.bind(this));
    }

    velgSak(sak) {
        this.setState({
            aktivtVindu: JOURNALFOR,
            valgtSak: sak
        })
    }

    tilbake() {
        this.setState({
            aktivtVindu: VELG_SAK,
            valgtSak: null
        })
    }

    render() {
        let aktivtVindu;
        if (this.state.aktivtVindu === VELG_SAK) {
            aktivtVindu = <VelgSak saker={this.state.saker} velgSak={this.velgSak}/>;
        } else {
            aktivtVindu = <JournalforSak
                                fnr={this.props.fnr}
                                traadId={this.state.traadId}
                                sak={this.state.valgtSak}
                                tilbake={this.tilbake}
                                wicketurl={this.props.wicketurl}
                                wicketcomponent={this.props.wicketcomponent}/>
        }
        return (
            <div className="journalforings-panel shadow">
                <h2 className="header">Journalføring</h2>
                {aktivtVindu}
                <LukkKnapp wicketurl={this.props.wicketurl} wicketcomponent={this.props.wicketcomponent}/>
            </div>
        );
    }
}

function okCallback(data) {
    this.setState({
        saker: data
    });
}
function feiletCallback() {
    this.setState({
        feilet: true
    });
}


export default JournalforingsPanel;

