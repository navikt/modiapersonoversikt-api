import React from 'react';
import PT from 'prop-types';
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
            <div className="hurtigreferat_ikke_lenger_i_bruk"></div>
        );
    }
}

HurtigReferat.propTypes = {
    fødselsnummer: PT.string.isRequired,
    wicketurl: PT.string.isRequired,
    wicketcomponent: PT.string.isRequired,
};

export default HurtigReferat;
