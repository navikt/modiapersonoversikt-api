import React from 'react';
import VelgSak from '../journalforingspanel/velgsak.js';
import WicketSender from './../reactwicketmixin/wicketsender.js';

class VelgSakPanel extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            saker: []
        };
        this.velgSak = this.velgSak.bind(this);
        this.velgOkonomiskSosialhelp = this.velgOkonomiskSosialhelp.bind(this);
    }

    velgSak(sak) {
        WicketSender(this.props.wicketurl, this.props.wicketcomponent, 'velgSak', sak);
    }

    velgOkonomiskSosialhelp(event) {
        event.preventDefault();
        const sak = {
            saksId: null,
            fagsystemSaksId: null,
            temaKode: 'OKSOS',
            temaNavn: 'Økonomisk sosialhjelp',
            fagsystemKode: null,
            fagsystemNavn: null,
            sakstype: null,
            finnesIGsak: null,
            finnesIPsak: null
        };
        this.velgSak(sak);
    }

    componentDidMount() {
        $.get('/modiabrukerdialog/rest/journalforing/' + this.props.fnr + '/saker').then(
            okCallback.bind(this),
            feiletCallback.bind(this));
    }

    render() {
        return (
            <div>
                <VelgSak saker={this.state.saker} velgSak={this.velgSak}/>
                <button className="sosialhjelp-knapp knapp-advarsel-stor" onClick={this.velgOkonomiskSosialhelp}>Økonomisk sosialhjelp</button>
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


export default VelgSakPanel;

