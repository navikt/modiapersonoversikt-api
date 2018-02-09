import React from 'react';
import ListeElement from './listeelement';
import ScrollPortal from '../utils/scroll-portal';
import TraadVisning from './traadvisning';
import PT from 'prop-types';

function getErTom(props) {
    return props.traader.length === 0;
}

function erTraadValgt(traad, valgtTraad) {
    return traad === valgtTraad;
}

function lagInnboksVisning(props) {
    const meldingsListeElementer = props.traader.map((traad) => {
        const valgt = erTraadValgt(traad, props.valgtTraad);
        return (
            <ListeElement
                key={traad.traadId + valgt}
                traad={traad}
                onClick={() => props.nyTraadValgtCallback(traad)}
                visCheckBox={props.visCheckbox}
                erValgt={valgt}
            />
        );
    });
    const erTom = getErTom(props);
    const submitErrorMessage = props.submitButtonProps.error ? props.submitButtonProps.errorMessage : '';
    const submittKnappPanel = (
        <div className="velgPanel">
            <input
                type="submit"
                value={props.submitButtonProps.buttonText}
                className="knapp-hoved-liten"
            />
            <p className="feedbacklabel">{submitErrorMessage}</p>
        </div>
    );

    return (
        <div className={'sok-visning ' + (erTom ? 'hidden' : '')}>
            <ScrollPortal
                id={props.listePanelId}
                className="sok-liste"
                role="tablist"
                tabIndex="-1"
                aria-live="assertive"
                aria-atomic="true"
                aria-controls={props.forhandsvisningsPanelId}
            >
                {meldingsListeElementer}
            </ScrollPortal>
            <div
                tabIndex="-1"
                className="sok-forhandsvisning"
                role="tabpanel"
                id={props.forhandsvisningsPanelId}
                aria-atomic="true"
                aria-live="polite"
            >
                <TraadVisning
                    traad={props.valgtTraad}
                    submitButtonProps={props.submitButtonProps}
                />
                {submittKnappPanel}
            </div>
        </div>
    );
}

function InnboksVisning(props) {
    const innboksVisning = lagInnboksVisning(props);
    return (
        <div className="innboks-visning">
            {innboksVisning}
        </div>
    );
}

InnboksVisning.propTypes = {
    traader: PT.arrayOf(PT.object).isRequired,
    nyTraadValgtCallback: PT.func.isRequired,
    valgtTraad: PT.object.isRequired,
    listePanelId: PT.string.isRequired,
    forhandsvisningsPanelId: PT.string.isRequired,
    feilet: PT.bool,
    initialisert: PT.bool,
    submitButtonProps: PT.shape({
        buttonText: PT.string,
        errorMessage: PT.string,
        error: PT.bool
    }),
    visCheckbox: PT.bool
};

InnboksVisning.defaultProps = {
    visCheckbox: false,
    feilet: false,
    initialisert: true
};

export default InnboksVisning;
