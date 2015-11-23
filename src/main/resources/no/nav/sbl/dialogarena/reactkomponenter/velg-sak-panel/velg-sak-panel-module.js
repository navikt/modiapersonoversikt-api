import React from 'react';
import AsyncLoader from './../utils/async-loader';
import VelgSak from '../journalforing-panel/velg-sak';
import wicketSender from './../react-wicket-mixin/wicket-sender';
import OkonomiskSosialhjelpKnapp from './okonomisk-sosialhjelp-knapp';
import PromiseUtils from './../utils/promise-utils';
import Ajax from './../utils/ajax';

function markerSomPsakSaker(pesysSaker) {
    return pesysSaker.map((sak) => {
        sak.erPesysSak = true;
        return sak;
    });
}

class VelgSakPanel extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            saker: []
        };
        const baseUrl = '/modiabrukerdialog/rest/journalforing/' + this.props.fnr;

        const wrapperPromise = {
            gsak: Ajax.get(baseUrl + '/saker/sammensatte'),
            psak: Ajax.get(baseUrl + '/saker/pensjon').then(markerSomPsakSaker)
        };

        this.promise = PromiseUtils.atLeastN(1, wrapperPromise);

        this.velgSak = this.velgSak.bind(this);
    }

    velgSak(sak) {
        wicketSender(this.props.wicketurl, this.props.wicketcomponent, 'velgSak', sak);
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

VelgSakPanel.propTypes = {
    'wicketurl': React.PropTypes.string.isRequired,
    'wicketcomponent': React.PropTypes.string.isRequired,
    'fnr': React.PropTypes.string.isRequired,
    'skalViseOkonomiskSosialhjelp': React.PropTypes.bool.isRequired
};

export default VelgSakPanel;
