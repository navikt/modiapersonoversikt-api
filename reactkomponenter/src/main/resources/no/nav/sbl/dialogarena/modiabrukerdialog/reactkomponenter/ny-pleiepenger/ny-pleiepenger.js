import React from 'react';
import PT from 'prop-types';

import PleiepengerLamell from 'modiapersonoversikt/build/dist/components/standalone/Pleiepenger/PleiepengerLamell';

class NyPleiepenger extends React.Component {
    render() {
        return (
            <div className="ny-frontend">
                <PleiepengerLamell fødselsnummer={this.props.fødselsnummer} barnetsFødselsnummer={this.props.barnetsFødselsnummer}/>
            </div>
        );
    }
}

NyPleiepenger.propTypes = {
    fødselsnummer: PT.string.isRequired,
    barnetsFødselsnummer: PT.string.isRequired
};

export default NyPleiepenger;
