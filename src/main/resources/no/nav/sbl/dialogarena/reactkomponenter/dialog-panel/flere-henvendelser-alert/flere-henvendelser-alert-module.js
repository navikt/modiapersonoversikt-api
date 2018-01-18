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
                    Det ligger flere oppgaver i lista.
                    <br /><a href="#" onClick={(e) => onClickHandler(e)}>
                        Slå sammen tråder
                    </a>
                </span>
        </Alertstripe>
        <input type="button"
               value="Slå sammen tråder"
               className="knapp-liten"
               onClick={(e) => onClickHandler(e)}
        />
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
