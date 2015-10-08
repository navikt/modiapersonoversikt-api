import React from 'react';
import AsyncLoader from './../utils/async-loader';
import VelgSak from '../journalforing-panel/velg-sak';
import WicketSender from './../react-wicket-mixin/wicket-sender';
import OkonomiskSosialhjelpKnapp from './okonomisk-sosialhjelp-knapp';
import PromiseUtils from './../utils/promise-utils';
import Ajax from './../utils/ajax';
import Q from 'q';

class VelgSakPanel extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            saker: []
        };
        const url = '/modiabrukerdialog/rest/journalforing/' + this.props.fnr;

        const gsakPromise = Ajax.get(url + '/saker/sammensatte');
        const psakPromise = Ajax.get(url + '/saker/pensjon');

        var wrapperPromise = {
            gsak: gsakPromise,
            psak: psakPromise
        };

        this.promise = PromiseUtils.atLeastN(1, wrapperPromise);

        this.velgSak = this.velgSak.bind(this);
    }

    velgSak(sak) {
        WicketSender(this.props.wicketurl, this.props.wicketcomponent, 'velgSak', sak);
    }

    render() {
        const okonomiskSosialhjelpValg = this.props.skalViseOkonomiskSosialhjelp ?
            <OkonomiskSosialhjelpKnapp velgSak={this.velgSak}/> : null;

        return (
            <AsyncLoader promises={this.promise} toProp="saker">
                <VelgSak velgSak={this.velgSak}/>
                {okonomiskSosialhjelpValg}
            </AsyncLoader>
        );
    }
}

export default VelgSakPanel;

