import React, { Component } from 'react';
import Alertstripe from '../../alertstriper/alertstripe-module';
import PT from 'prop-types';

function alertStripe(props) {
    return (
        <div className="tildelt-flere-alert">
            <Alertstripe type={'info'} tekst={null}>
                Brukeren har <span className="bold">{props.antallTildelteOppgaver}</span> ubesvarte oppgaver som er tildelt deg.
            </Alertstripe>
        </div>
    );
}

class TildeltFlereOppgaverAlert extends Component {

    constructor(props) {
        super();
        this.state = props;
    }

    render() {
        const saksbehandlerErTildeltFlereOppgaver = this.state.antallTildelteOppgaver > 1;
        if (saksbehandlerErTildeltFlereOppgaver) {
            return alertStripe(this.state);
        }
        return null;
    }
}

TildeltFlereOppgaverAlert.propTypes = {
    antallTildelteOppgaver: PT.number.isRequired
};

export default TildeltFlereOppgaverAlert;
