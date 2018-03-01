import React from 'react';
import ListeElement from './listeelement';
import ScrollPortal from '../../utils/scroll-portal';
import TraadVisning from './traadvisning';
import PT from 'prop-types';
import { checkboxProps } from '../types';

function erTom(props) {
    return props.traader.length === 0;
}

function erTraadValgt(traad, valgtTraad) {
    return traad === valgtTraad;
}

function lagMeldingsListe(props) {
    const meldingsListeElementer = props.traader.map((traad) => {
        const erValgt = erTraadValgt(traad, props.valgtTraad);
        return (
            <ListeElement
                key={traad.traadId}
                traad={traad}
                onClick={() => props.nyTraadValgtCallback(traad)}
                checkboxProps={props.checkboxProps}
                erValgt={erValgt}
            />
        );
    });
    const antallTraader = meldingsListeElementer.length;
    return (
        <ScrollPortal
            id={props.listePanelId}
            className="sok-liste"
            role="tablist"
            tabIndex="-1"
            aria-live="assertive"
            aria-atomic="true"
            aria-controls={props.traadvisningsPanelId}
        >
            <div className="antall-traader">
                Viser <span className="bold">{antallTraader}</span> {antallTraader > 1 ? 'dialoger' : 'dialog'}
            </div>
            {meldingsListeElementer}
        </ScrollPortal>
    );
}

function lagTraadVisning(props) {
    return (
        <div
            tabIndex="-1"
            className="sok-forhandsvisning"
            role="tabpanel"
            id={props.traadvisningsPanelId}
            aria-atomic="true"
            aria-live="polite"
        >
            <TraadVisning
                traad={props.valgtTraad}
            />
        </div>
    );
}

function lagInnboksVisning(props) {
    const meldingsListe = lagMeldingsListe(props);

    const traadVisning = lagTraadVisning(props);

    return (
        <div className={'sok-visning ' + (erTom(props) ? 'hidden' : '')}>
            {meldingsListe}
            {traadVisning}
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
    traadvisningsPanelId: PT.string.isRequired,
    checkboxProps: checkboxProps.isRequired
};

export default InnboksVisning;
