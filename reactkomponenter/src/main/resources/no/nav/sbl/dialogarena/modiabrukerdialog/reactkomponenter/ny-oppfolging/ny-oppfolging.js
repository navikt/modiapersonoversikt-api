import React from 'react';
import PT from 'prop-types';

import OppfolgingLamell from 'modiapersonoversikt/build/dist/components/standalone/OppfolgingLamell';

class NyOppfolging extends React.Component {
    render() {
        return <OppfolgingLamell fødselsnummer={this.props.fødselsnummer}/>
    }
}

NyOppfolging.propTypes = {
    fødselsnummer: PT.string.isRequired
}

export default NyOppfolging;