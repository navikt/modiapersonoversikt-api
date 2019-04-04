import React from 'react';
import PT from 'prop-types';

import SykepengerLamell from 'modiapersonoversikt/build/dist/components/standalone/Sykepenger/SykepengerLamell';

class NySykepenger extends React.Component {
    render() {
        return (
            <div className="ny-frontend">
                <SykepengerLamell fødselsnummer={this.props.fødselsnummer} sykmeldtFraOgMed={this.props.sykmeldtFraOgMed}/>
            </div>
        );
    }
}

NySykepenger.propTypes = {
    fødselsnummer: PT.string.isRequired,
    sykmeldtFraOgMed: PT.string.isRequired
};

export default NySykepenger;
