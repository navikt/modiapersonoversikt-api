import React, { Component } from 'react';
import Utils from './../utils/utils-module';
import MeldingerSokStore from './meldinger-sok-store';
import MeldingerSokModal from "./meldinger-sok-modal";
import MeldingerSokView from "./meldinger-sok-view";
import PT from 'prop-types';

class MeldingerSok extends Component {

    componentWillMount() {
        this.store = new MeldingerSokStore($.extend({}, {
            fritekst: '',
            traader: [],
            valgtTraad: {},
            traadMarkupIds: {},
            listePanelId: Utils.generateId('sok-liste-'),
            traadvisningsPanelId: Utils.generateId('sok-traadvisningsPanelId-')
        }, this.props));
        this.state = this.store.getState();
        this.props.setVisModalVindu(() => this.vis());
        this.props.setSkjulModalVindu(() => this.skjul());
    }
    componentDidMount() {
        this.store.setContainerElement(this.modalRef.portalElement);
        this.store.addListener(this.storeChanged.bind(this));
    }
    componentWillUnmount() {
        this.store.removeListener(this.storeChanged.bind(this));
    }
    componentWillReceiveProps(props) {
        this.store.update(props);
    }
    onChangeProxy(e) {
        const value = e.target.value;
        if (this.state.fritekst !== value) {
            this.store.onChange(e);
        }
    }
    keyDownHandler(event) {
        if (event.keyCode === 13) {
            this.onSubmit(event);
        }
    }
    vis(props = {}) {
        this.store.update(props);
        this.modalRef.open();
    }
    skjul() {
        this.modalRef.close();
    }
    storeChanged() {
        this.setState(this.store.getState());
    }
    onSubmit(e) {
        const onSuccess = () => this.skjul();
        this.props.onSubmit(e, this.state, () => onSuccess());
    }

    render() {
        return (
            <MeldingerSokModal setRef={ref => this.modalRef = ref} moduleName={this.props.modulNavn}>
                <MeldingerSokView
                    onChangeProxy={e => this.onChangeProxy(e)}
                    keyDownHandler={e => this.keyDownHandler(e)}
                    onSubmit={(e) => this.onSubmit(e)}
                    state={this.state}
                    store={this.store}
                />
            </MeldingerSokModal>
        );
    }
}

MeldingerSok.propTypes = {
    modulNavn: PT.string,
    visSok: PT.bool,
    visCheckbox: PT.bool,
    submitButtonValue: PT.string,
    submitErrorMessage: PT.string,
    submitError: PT.bool,
    onSubmit: PT.func,
    setVisModalVindu: PT.func,
    setSkjulModalVindu: PT.func
};

const defaultOnSubmit = (event, state, onSuccess) => {
    event.preventDefault();
    document.getElementById(state.traadMarkupIds[state.valgtTraad.traadId]).click();
    onSuccess();
};

MeldingerSok.defaultProps = {
    modulNavn: 'Meldingersok',
    visSok: true,
    visCheckbox: false,
    submitButtonProps: {
        buttonText: "Velg dialog",
        errorMessage: "Det skjedde en feil.",
        error: false
    },
    className: 'meldinger-sok',
    onSubmit: defaultOnSubmit,
    setVisModalVindu: () => {},
    setSkjulModalVindu: () => {}
};

export default MeldingerSok;
