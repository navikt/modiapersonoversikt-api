import React from 'react';

import Visittkort from 'modiapersonoversikt/lib/components/StandAloneVisittkort/VisittKortStandAlone';

class NyFrontend extends React.Component {
    render() {
        return <Visittkort fødselsnummer={ this.props.fødselsnummer } />;
    }
}

export default NyFrontend;
