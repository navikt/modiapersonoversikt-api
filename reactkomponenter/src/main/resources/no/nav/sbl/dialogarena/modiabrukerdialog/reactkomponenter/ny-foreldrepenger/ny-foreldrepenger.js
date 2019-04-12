import React from 'react';
import PT from 'prop-types';

import ForeldrepengerLamell
    from 'modiapersonoversikt/build/dist/components/standalone/Foreldrepenger/ForeldrepengerLamell';

class NyForeldrepenger extends React.Component {
    render() {
        return (
            <ForeldrepengerLamell fødselsnummer={this.props.fødselsnummer}/>
        );
    }
}

NyForeldrepenger.propTypes = {
    fødselsnummer: PT.string.isRequired,
};

export default NyForeldrepenger;
