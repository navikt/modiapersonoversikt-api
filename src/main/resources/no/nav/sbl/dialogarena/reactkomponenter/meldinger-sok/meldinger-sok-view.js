import React from 'react';
import PT from 'prop-types';
import InnboksVisning from './innboks-visning';

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
            {tomInnhold()}
        </div>
    );
}

function MeldingerSokView(props) {
    const sokeFelt = props.state.visSok ? lagSokeFelt(props) : '';
    const cls = `${props.state.className} sok-layout`;
    const tomVisning = lagTomVisning(props);
    return (
        <form
            className={cls}
            onSubmit={(e) => props.onSubmit(e)}
            onKeyDown={props.keyDownHandler}
        >
            {sokeFelt}
            <InnboksVisning
                nyTraadValgtCallback={props.store.traadChanged}
                traader={props.state.traader}
                valgtTraad={props.state.valgtTraad}
                listePanelId={props.state.listePanelId}
                forhandsvisningsPanelId={props.state.forhandsvisningsPanelId}
                feilet={props.state.feilet}
                initialisert={props.state.initialisert}
                visCheckbox={props.state.visCheckbox}
                submitButtonProps={props.state.submitButtonProps}
            />
            {tomVisning}
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
