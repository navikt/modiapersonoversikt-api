/* eslint "react/jsx-no-bind": 1 */
import React from 'react';
import ListevisningKomponent from './listevisning';
import Forhandsvisning from './forhandsvisning';
import ScrollPortal from './../utils/scroll-portal';
import PT from 'prop-types';

function lagSokVisning(erTom, props){
    const tekstlistekomponenter = props.state.traader.map((traad) =>
            <ListevisningKomponent
                key={traad.traadId}
                traad={traad}
                valgtTraad={props.state.valgtTraad}
                store={props.store}
                visCheckBox={props.state.visCheckbox}
            />
    );
    return (
        <div className={'sok-visning ' + (erTom ? 'hidden' : '')}>
            <ScrollPortal
                id={props.state.listePanelId}
                className="sok-liste"
                role="tablist"
                tabIndex="-1"
                aria-live="assertive"
                aria-atomic="true"
                aria-controls={props.state.forhandsvisningsPanelId}
            >
                {tekstlistekomponenter}
            </ScrollPortal>
            <div
                tabIndex="-1"
                className="sok-forhandsvisning"
                role="tabpanel"
                id={props.state.forhandsvisningsPanelId}
                aria-atomic="true"
                aria-live="polite"
            >
                <Forhandsvisning
                    traad={props.state.valgtTraad}
                    submitButtonValue={props.state.submitButtonValue}
                    submitError={props.state.submitError}
                    submitErrorMessage={props.state.submitErrorMessage}
                />
            </div>
        </div>
    );
}

function lagVisninger(props){
    const erTom = props.state.traader.length === 0;
    const sokVisning = lagSokVisning(erTom, props);

    let tomInnhold = (
        <div className="tom">
            <img src="../img/ajaxloader/hvit/loader_hvit_128.gif" alt="Henter meldinger" />
        </div>
    );
    if (props.state.feilet) {
        tomInnhold = <h1 className="tom" role="alert" aria-atomic="true">Noe feilet</h1>;
    } else if (props.state.initialisert) {
        tomInnhold = <h1 className="tom" role="alert" aria-atomic="true">Ingen treff</h1>;
    }

    const tomVisning = (
        <div className={'sok-visning ' + (erTom ? '' : 'hidden')}>
            {tomInnhold}
        </div>
    );
    return { sokVisning, tomVisning };
}

let lagSokeFelt = function (props) {
    return <div tabIndex="-1" className="sok-container">
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
    </div>;
};

function MeldingerSokView(props){
    const { sokVisning, tomVisning } = lagVisninger(props);
    const sokeFelt = props.state.visSok ? lagSokeFelt(props) : '';
    const cls = `${props.state.className} sok-layout`;
    return (
        <form
            className={cls}
            onSubmit={(e) => props.onSubmit(e)}
            onKeyDown={props.keyDownHandler}
        >
            {sokeFelt}
            {sokVisning}
            {tomVisning}
            <input type="submit" value="submit" className="hidden"/>
        </form>
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
