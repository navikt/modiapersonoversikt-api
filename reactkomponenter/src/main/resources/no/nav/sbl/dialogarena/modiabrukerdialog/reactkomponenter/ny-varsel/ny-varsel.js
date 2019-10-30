import React from 'react';
import PT from 'prop-types';

import VarslerLamell from 'modiapersonoversikt/build/dist/components/standalone/VarslerLamell';

class NyVarsel extends React.Component {
    render() {
        return (
            <div class="ny-varsel">
                <VarslerLamell fødselsnummer={this.props.fødselsnummer}/>
            </div>
    );
    }
}

NyVarsel.propTypes = {
    fødselsnummer: PT.string.isRequired
}

export default NyVarsel;