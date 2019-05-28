import React from 'react';
import PT from 'prop-types';
import HurtigReferatStandalone from 'modiapersonoversikt/build/dist/components/standalone/Hurtigreferat/HurtigreferatStandalone';
import sendToWicket from '../react-wicket-mixin/wicket-sender';

class HurtigReferat extends React.Component {
    constructor(props) {
        super(props);
        this.suksessCallBack = this.suksessCallBack.bind(this);
    }

    suksessCallBack () {
        sendToWicket(this.props.wicketurl, this.props.wicketcomponent, "visKvittering")
    }

    render() {
        return (
            <div className="ny-frontend">
                <HurtigReferatStandalone fødselsnummer={this.props.fødselsnummer} meldingBleSendtCallback={this.suksessCallBack}/>
            </div>
        );
    }
}

HurtigReferat.propTypes = {
    fødselsnummer: PT.string.isRequired,
    wicketurl: PT.string.isRequired,
    wicketcomponent: PT.string.isRequired,
};

export default HurtigReferat;
