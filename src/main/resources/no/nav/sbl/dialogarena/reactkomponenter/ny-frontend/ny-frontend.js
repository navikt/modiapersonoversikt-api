import React from 'react';
import PT from 'prop-types';

import Visittkort from 'modiapersonoversikt/build/dist/components/StandAloneVisittkort/VisittKortStandAlone';

function NyFrontend() {
    return <Visittkort fødselsnummer={this.props.fødselsnummer} />;
}

NyFrontend.propTypes = {
    fødselsnummer: PT.string.isRequired
};

export default NyFrontend;
