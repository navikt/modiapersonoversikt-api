import React, { Component } from 'react';
import wicketSender from '../../react-wicket-mixin/wicket-sender';
import DelvisSvar from './delvis-svar';

class LeggTilbakeDelvisSvarPanel extends Component {
    constructor(props) {
        super(props);
        console.log(props);
        this.svarCallback = this.svarCallback.bind(this);

        this.state = {
            erUnderArbeid: true
        };
    }
    svarCallback() {
        console.log("Svar Callback!");
        this.setState({ erUnderArbeid: false });
        wicketSender(this.props.wicketurl, this.props.wicketcomponent, this.props.svarDelvisCallbackId);
    }
    render() {
        if (this.state.erUnderArbeid) {
            return (<DelvisSvar
                henvendelseId={this.props.henvendelseId}
                sporsmal={this.props.sporsmal}
                fodselsnummer={this.props.fodselsnummer}
                traadId={this.props.traadId}
                svarCallback={this.svarCallback}
            />);
        }
        return <h1>Delvis svar er registrert</h1>;
    }
}

LeggTilbakeDelvisSvarPanel.propTypes = {
    wicketurl: React.PropTypes.string.isRequired,
    wicketcomponent: React.PropTypes.string.isRequired,
    svarDelvisCallbackId: React.PropTypes.string.isRequired,
    henvendelseId: React.PropTypes.string.isRequired,
    sporsmal: React.PropTypes.string.isRequired,
    fodselsnummer: React.PropTypes.string.isRequired,
    traadId: React.PropTypes.string.isRequired,
};

export default LeggTilbakeDelvisSvarPanel;
