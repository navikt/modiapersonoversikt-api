import React from 'react';
import PT from 'prop-types';
import InnboksVisning from './innboks-visning/innboks-visning';
import NavFrontendSpinner from 'nav-frontend-spinner';

function getErTom(props) {
    return props.state.traader.length === 0;
}

function lagSokeFelt(props) {
    return (
        <div tabIndex="-1" className="sok-container">
            <div>
                <input
                    type="text"
                    placeholder="Søk"
                    value={props.state.fritekst}
                    title="Søk"
                    onChange={props.onChangeProxy}
                    onKeyDown={
                        props.store.onKeyDown.bind(
                            props.store,
                            document.getElementById(props.state.listePanelId))
                    }
                    aria-controls={props.state.listePanelId}
                />
                <img src="../img/sok.svg" alt="Forstørrelseglass-ikon" aria-hidden="true"/>
            </div>
        </div>
    );
}

function lagTomVisning(props) {
    const tomInnhold = () => {
        if (props.state.feilet) {
            return <h1 className="tom" role="alert" aria-atomic="true">Noe feilet</h1>;
        } else if (props.state.initialisert) {
            return <h1 className="tom" role="alert" aria-atomic="true">Ingen treff</h1>;
        }
        return (
            <NavFrontendSpinner type="XXL" />
        );
    };

    const erTom = getErTom(props);
    return (
        <div className={(erTom ? 'tom-visning' : 'tom-visning hidden')}>
            {tomInnhold()}
        </div>
    );
}

function lagSubmitPanel(props) {
    const submitErrorMarkup = props.state.submitButtonProps.error
        ? <div role="alert" aria-live="assertive">
            <p className="feedbacklabel">{props.state.submitButtonProps.errorMessage}</p>
        </div>
        : '';

    return (
        <div className="velgPanel">
            <input
                type="submit"
                value={props.state.submitButtonProps.buttonText}
                className="knapp-hoved-liten"
            />
            {submitErrorMarkup}
        </div>
    );
}

function lagInnboks(props) {
    const traadBegrep = props.state.modulNavn === 'BesvarFlereOppgaverModul'
        ? {
            entall: 'oppgave',
            bestemtEntall: 'oppgaven',
            flertall: 'oppgaver'
        } : {
            entall: 'dialog',
            bestemtEntall: 'dialogen',
            flertall: 'dialoger'
        };
    return (
        <InnboksVisning
            nyTraadValgtCallback={props.store.traadChanged}
            traader={props.state.traader}
            valgtTraad={props.state.valgtTraad}
            listePanelId={props.state.listePanelId}
            traadvisningsPanelId={props.state.traadvisningsPanelId}
            checkboxProps={props.state.checkboxProps}
            traadBegrep={traadBegrep}
            visAntallMeldingerITraad={props.state.modulNavn !== 'BesvarFlereOppgaverModul'}
        />
    );
}

function MeldingerSokView(props) {
    const sokeFelt = props.state.visSok ? lagSokeFelt(props) : '';
    const submitPanel = lagSubmitPanel(props);
    const cls = `${props.state.className} sok-layout`;
    const innboks = lagInnboks(props);
    const tomVisning = lagTomVisning(props);
    return (
        <div className={cls}>
            <form
                aria-describedby={`modul-description-${props.state.modulNavn}`}
                onSubmit={(e) => props.onSubmit(e)}
                onKeyDown={props.keyDownHandler}
            >
                {sokeFelt}
                {innboks}
                {tomVisning}
                {submitPanel}
            </form>
            <div id={`modul-description-${props.state.modulNavn}`} className="vekk">
                {props.state.hjelpetekst}
            </div>
        </div>
    );
}

MeldingerSokView.propTypes = {
    state: PT.object.isRequired,
    store: PT.object.isRequired,
    onChangeProxy: PT.func.isRequired,
    keyDownHandler: PT.func.isRequired,
    onSubmit: PT.func.isRequired
};

export default MeldingerSokView;
