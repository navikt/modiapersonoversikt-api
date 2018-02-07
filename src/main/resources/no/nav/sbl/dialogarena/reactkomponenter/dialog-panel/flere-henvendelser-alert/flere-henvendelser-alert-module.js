import React from 'react';
import Alertstripe from '../../alertstriper/alertstripe-module';
import PT from 'prop-types';

function onClickHandler(event) {
    event.preventDefault();
    $('button.slaaSammenTraaderToggle')[0].click();
}

const alertStripe = (
    <div className="flere-henvendelser-alert">
        <Alertstripe type={'info'} tekst="">
            <span>
                <p>Det ligger flere uløste oppgaver i lista</p>
                <a href="#" onClick={(e) => onClickHandler(e)}>
                    Slå sammen oppgaver
                </a>
            </span>
        </Alertstripe>
    </div>
);

function FlereHenvendelser(props) {
    if (props.flereOppgaverErTildeltFraBruker) {
        return alertStripe;
    }
    return null;
}

FlereHenvendelser.propTypes = {
    flereOppgaverErTildeltFraBruker: PT.bool.isRequired
};

export default FlereHenvendelser;
