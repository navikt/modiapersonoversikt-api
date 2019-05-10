import React from 'react';
import PT from 'prop-types';

import VarslerLamell from 'modiapersonoversikt/build/dist/components/standalone/VarslerLamell';

class NyVarsel extends React.Component {
    render() {
        return <VarslerLamell fødselsnummer={this.props.fødselsnummer}/>
    }
}

NyVarsel.propTypes = {
    fødselsnummer: PT.string.isRequired
}

export default NyVarsel;