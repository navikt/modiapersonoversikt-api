import React from 'react';
import PT from 'prop-types';

import UtbetalingsLamell from 'modiapersonoversikt/build/dist/components/standalone/UtbetalingsLamell';

class NyUtbetaling extends React.Component {
    render() {
        return <UtbetalingsLamell fødselsnummer={this.props.fødselsnummer}/>
    }
}

NyUtbetaling.propTypes = {
    fødselsnummer: PT.string.isRequired
}

export default NyUtbetaling;