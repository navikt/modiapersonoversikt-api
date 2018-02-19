import React from 'react';
import Alertstripe from '../../alertstriper/alertstripe-module';
import PT from 'prop-types';

function alertStripe(props) {
    return (
        <div className="flere-oppgaver-alert">
            <Alertstripe type={'info'} tekst="">
                Brukeren har <span className="bold">{props.antallOppgaverFraBruker}</span> ubesvarte oppgaver som er tildelt deg.
            </Alertstripe>
        </div>
    );
}

function FlereOppgaverFraBrukerAlert(props) {
    const saksbehandlerErTildeltFlereOppgaver = props.antallOppgaverFraBruker > 1;
    if (saksbehandlerErTildeltFlereOppgaver) {
        return alertStripe(props);
    }
    return null;
}

FlereOppgaverFraBrukerAlert.propTypes = {
    antallOppgaverFraBruker: PT.number.isRequired
};

export default FlereOppgaverFraBrukerAlert;
