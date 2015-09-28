import React from 'react';
import AsyncLoader from './../utils/async-loader';
import VelgSak from '../journalforing-panel/velg-sak';
import WicketSender from './../react-wicket-mixin/wicket-sender';
import OkonomiskSosialhjelpKnapp from './okonomisk-sosialhjelp-knapp';
import PromiseUtils from './../utils/promise-utils';

class VelgSakPanel extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            saker: []
        };
        const gsakSaker = $.get('/modiabrukerdialog/rest/journalforing/' + this.props.fnr + '/saker/sammensatte');
        const psakSaker = $.get('/modiabrukerdialog/rest/journalforing/' + this.props.fnr + '/saker/pensjon');

        this.promise = PromiseUtils.atLeastN(1,gsakSaker, psakSaker);
        this.velgSak = this.velgSak.bind(this);
    }

    velgSak(sak) {
        WicketSender(this.props.wicketurl, this.props.wicketcomponent, 'velgSak', sak);
    }

    render() {
        return (
            <AsyncLoader promises={this.promise} toProp="saker">
                <VelgSak velgSak={this.velgSak}/>
                <OkonomiskSosialhjelpKnapp velgSak={this.velgSak}/>
            </AsyncLoader>
        );
    }
}

export default VelgSakPanel;

