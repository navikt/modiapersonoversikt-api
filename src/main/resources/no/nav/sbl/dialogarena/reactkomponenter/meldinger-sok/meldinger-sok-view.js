import React from 'react';
import PT from 'prop-types';
import InnboksVisning from './innboks-visning';

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

function MeldingerSokView(props) {
    const sokeFelt = props.state.visSok ? lagSokeFelt(props) : '';
    const cls = `${props.state.className} sok-layout`;
    return (
        <form
            className={cls}
            onSubmit={(e) => props.onSubmit(e)}
            onKeyDown={props.keyDownHandler}
        >
            {sokeFelt}
            <InnboksVisning
                nyTraadValgtCallback={props.nyTraadValgtCallback}
                traader={props.state.traader}
                valgtTraad={props.state.valgtTraad}
                listePanelId={props.state.listePanelId}
                forhandsvisningsPanelId={props.state.forhandsvisningsPanelId}
                feilet={props.state.feilet}
                initialisert={props.state.initialisert}
                visCheckbox={props.state.visCheckbox}
                submitButtonProps={props.state.submitButtonProps}
            />
        </form>
    );
}

MeldingerSokView.propTypes = {
    state: PT.object.isRequired,
    store: PT.object.isRequired,
    onChangeProxy: PT.func.isRequired,
    keyDownHandler: PT.func.isRequired,
    onSubmit: PT.func.isRequired,
};

export default MeldingerSokView;
