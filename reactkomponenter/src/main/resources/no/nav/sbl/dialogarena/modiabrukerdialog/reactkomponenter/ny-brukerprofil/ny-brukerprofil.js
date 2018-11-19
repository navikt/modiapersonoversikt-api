import React from 'react';
import PT from 'prop-types';

import BrukerprofilStandalone from 'modiapersonoversikt/build/dist/components/standalone/Brukerprofil';

class NyBrukerprofil extends React.Component {
    render() {
        return <BrukerprofilStandalone fødselsnummer={this.props.fødselsnummer}/>
    }
}

NyBrukerprofil.propTypes = {
    fødselsnummer: PT.string.isRequired
}

export default NyBrukerprofil;