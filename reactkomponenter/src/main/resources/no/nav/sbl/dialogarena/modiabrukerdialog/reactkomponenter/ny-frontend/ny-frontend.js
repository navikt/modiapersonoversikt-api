import React from 'react';
import PT from 'prop-types';

import Visittkort from 'modiapersonoversikt/build/dist/components/StandAloneVisittkort/VisittKortStandAlone';

class NyFrontend extends React.Component {
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
            document.addEventListener('keyup', function(event) {
                if (event.key === 'b') {
                    event.stopPropagation();
                }
            }, true);
            setTimeout(this.tryUpdateToggle, 500);
        }
    }

    tryUpdateToggle() {
        if (document.getElementById('brukerprofillenke')) {
            document.getElementById('brukerprofillenke').href =
                '/modiabrukerdialog/person/' + this.props.fødselsnummer + '#!brukerprofil';
        } else {
            setTimeout(this.tryUpdateToggle, 500)
        }
    }
}

NyFrontend.propTypes = {
    fødselsnummer: PT.string.isRequired,
    nyBrukerprofil: PT.bool.isRequired
};

export default NyFrontend;
