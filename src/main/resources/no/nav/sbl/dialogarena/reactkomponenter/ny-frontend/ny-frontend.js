import React from 'react';

import Visittkort from 'modiapersonoversikt/build/dist/components/StandAloneVisittkort/VisittKortStandAlone';

class NyFrontend extends React.Component {
    render() {
        return <Visittkort fødselsnummer={ this.props.fødselsnummer } />;
    }
}

export default NyFrontend;
