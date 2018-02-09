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
    const submitErrorMessage = props.submitButtonProps.error ?
        <p className="feedbacklabel">{props.submitButtonProps.errorMessage}</p> : '';
    const submittKnappPanel = (
        <div className="velgPanel">
            <input
                type="submit"
                value={props.submitButtonProps.buttonText}
                className="knapp-hoved-liten"
            />
            {submitErrorMessage}
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

function lagTomVisning(props) {
    const tomInnhold = () => {
        if (props.feilet) {
            return <h1 className="tom" role="alert" aria-atomic="true">Noe feilet</h1>;
        } else if (props.initialisert) {
            return <h1 className="tom" role="alert" aria-atomic="true">Ingen treff</h1>;
        }
        return (
            <div className="tom">
                <img src="../img/ajaxloader/hvit/loader_hvit_128.gif" alt="Henter meldinger"/>
            </div>
        );
    };

    const erTom = getErTom(props);
    return (
        <div className={'sok-visning ' + (erTom ? '' : 'hidden')}>
            {tomInnhold}
        </div>
    );
}

function InnboksVisning(props) {
    const innboksVisning = lagInnboksVisning(props);
    const tomVisning = lagTomVisning(props);
    return (
        <div className="innboks-visning">
            {innboksVisning}
            {tomVisning}
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
    submitButtonProps: PT.object,
    visCheckbox: PT.bool
};

InnboksVisning.defaultProps = {
    visCheckbox: false,
    feilet: false,
    initialisert: true
};

export default InnboksVisning;
