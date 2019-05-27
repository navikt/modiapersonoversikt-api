import React from 'react';
import PT from 'prop-types';
import HurtigReferatStandalone from 'modiapersonoversikt/build/dist/components/standalone/Hurtigreferat/HurtigreferatStandalone';

class HurtigReferat extends React.Component {
    render() {
        return (
            <div className="ny-frontend">
                <HurtigReferatStandalone fødselsnummer={this.props.fødselsnummer} meldingBleSendtCallback={this.props.meldingBleSendtCallback}/>
            </div>
        );
    }
}

HurtigReferat.propTypes = {
    fødselsnummer: PT.string.isRequired,
    meldingBleSendtCallback: PT.func.isRequired
};

export default HurtigReferat;
