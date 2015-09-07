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
    }
    velgSak(sak) {
        WicketSender(this.props.wicketurl, this.props.wicketcomponent, 'velgSak', sak);
    }
    componentDidMount() {
        $.get('/modiabrukerdialog/rest/journalforing/' + this.props.fnr + '/saker').then(
            okCallback.bind(this),
            feiletCallback.bind(this));
    }

    render() {
        console.log('render');
        return (
            <VelgSak saker={this.state.saker} velgSak={this.velgSak}/>
        );
    }
}

function okCallback(data) {
    console.log('ok', data);
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

