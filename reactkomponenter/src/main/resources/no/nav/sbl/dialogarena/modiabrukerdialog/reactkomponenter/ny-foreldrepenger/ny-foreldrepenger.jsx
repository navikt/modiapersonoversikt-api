import React from 'react';
import PT from 'prop-types';

import ForeldrepengerLamell from 'modiapersonoversikt/build/dist/components/standalone/Foreldrepenger/ForeldrepengerLamell';

class NyForeldrepenger extends React.Component {
    render() {
        return (
            <div className="ny-frontend">
            <ForeldrepengerLamell fødselsnummer={this.props.fødselsnummer} />
        </div>
    );
    }
}

NyForeldrepenger.propTypes = {
    fødselsnummer: PT.string.isRequired,
};

export default NyForeldrepenger;
