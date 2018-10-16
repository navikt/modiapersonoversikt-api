import React from 'react';
import PT from 'prop-types';

import Visittkort from 'modiapersonoversikt/build/dist/components/standalone/VisittKort';

class NyttVisittkort extends React.Component {
    render() {
        return <Visittkort fødselsnummer={this.props.fødselsnummer}/>;
    }

    componentDidMount() {
        this.oppdaterToggle();
    }

    componentDidUpdate() {
        this.oppdaterToggle();
    }

    oppdaterToggle() {
        if (!this.props.nyBrukerprofil) {
            document.addEventListener('keydown', function(event) {
                if (event.key === 'b') {
                    event.stopPropagation();
                }
            }, true);
        }
    }
}

NyttVisittkort.propTypes = {
    fødselsnummer: PT.string.isRequired,
    nyBrukerprofil: PT.bool.isRequired
};

export default NyttVisittkort;
