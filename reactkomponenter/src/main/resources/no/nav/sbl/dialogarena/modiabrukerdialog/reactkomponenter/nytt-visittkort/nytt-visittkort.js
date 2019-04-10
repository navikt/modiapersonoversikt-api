import React from 'react';
import PT from 'prop-types';

import Visittkort from 'modiapersonoversikt/build/dist/components/standalone/VisittKort';

class NyttVisittkort extends React.Component {
    render() {
        return <Visittkort fødselsnummer={this.props.fødselsnummer}/>;
    }
}

NyttVisittkort.propTypes = {
    fødselsnummer: PT.string.isRequired
};

export default NyttVisittkort;
