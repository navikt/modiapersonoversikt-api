import React from 'react';
import PT from 'prop-types';

import SaksoversiktLamell from 'modiapersonoversikt/build/dist/components/standalone/SaksoversiktLamell';

class NySaksoversikt extends React.Component {
    render() {
        return <SaksoversiktLamell fødselsnummer={this.props.fødselsnummer}/>
    }
}

NySaksoversikt.propTypes = {
    fødselsnummer: PT.string.isRequired
}

export default NySaksoversikt;