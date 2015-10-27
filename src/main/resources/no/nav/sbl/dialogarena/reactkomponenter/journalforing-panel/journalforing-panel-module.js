import React from 'react';
import VelgSak from './velg-sak';
import JournalforSak from './journalfor-sak';
import LukkKnapp from './lukk-knapp';
import AsyncLoader from './../utils/async-loader';
import PromiseUtils from './../utils/promise-utils';
import Ajax from './../utils/ajax';
import Q from 'q';

const VELG_SAK = 'VELG_SAK';
const JOURNALFOR = 'JOURNALFOR';

function markerSomPsakSaker(pesysSaker) {
    return pesysSaker.map((sak) => {
        sak.erPesysSak = true;
        return sak;
    })
}

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

        const baseUrl = '/modiabrukerdialog/rest/journalforing/' + this.props.fnr;

        var wrapperPromise = {
            gsak: Ajax.get(baseUrl + '/saker/sammensatte'),
            psak: Ajax.get(baseUrl + '/saker/pensjon').then(markerSomPsakSaker)
        };

        this.promise = PromiseUtils.atLeastN(1, wrapperPromise);
    }

    velgSak(sak) {
        this.setState({
            aktivtVindu: JOURNALFOR,
            valgtSak: sak
        })
    }

    tilbake(event) {
        event.preventDefault();
        this.setState({
            aktivtVindu: VELG_SAK,
            valgtSak: null
        })
    }

    render() {
        let aktivtVindu;
        if (this.state.aktivtVindu === VELG_SAK) {
            aktivtVindu = (
                <AsyncLoader promises={this.promise} toProp="saker">
                    <VelgSak
                        temagruppe={this.state.temagruppe}
                        velgSak={this.velgSak}
                        temagruppeTemaMapping={this.props.temagruppeTemaMapping}/>
                </AsyncLoader>
            );
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
            <form className="journalforings-panel shadow">
                <h2 className="header">Journalføring</h2>
                {aktivtVindu}
                <LukkKnapp wicketurl={this.props.wicketurl} wicketcomponent={this.props.wicketcomponent}/>
            </form>
        );
    }
}

export default JournalforingsPanel;

