import React from 'react';
import AsyncLoader from './../utils/async-loader';
import VelgSak from '../journalforing-panel/velg-sak';
import WicketSender from './../react-wicket-mixin/wicket-sender';

class VelgSakPanel extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            saker: []
        };
        this.promise = $.get('/modiabrukerdialog/rest/journalforing/' + this.props.fnr + '/saker');
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

    render() {
        return (
            <AsyncLoader promises={this.promise} toProp="saker">
                <VelgSak velgSak={this.velgSak}/>
                <button className="sosialhjelp-knapp knapp-advarsel-stor" onClick={this.velgOkonomiskSosialhelp}>
                    Økonomisk sosialhjelp
                </button>
            </AsyncLoader>
        );
    }
}

export default VelgSakPanel;

