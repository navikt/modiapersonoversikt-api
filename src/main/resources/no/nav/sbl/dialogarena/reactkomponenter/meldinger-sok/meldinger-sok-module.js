/* eslint "react/jsx-no-bind": 1 */
import React, {Component} from 'react';
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
            forhandsvisningsPanelId: Utils.generateId('sok-forhandsvisningsPanelId-')
        }, this.props));
        this.state = this.store.getState();
        this.props.setVisModalVindu(() => this.vis());
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
            this.store.submit(this.skjul, event);
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
            <MeldingerSokModal setRef={ref => this.modalRef = ref} >
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
    visSok: PT.bool,
    visCheckbox: PT.bool,
    submitButtonValue: PT.string,
    submitErrorMessage: PT.string,
    submitError: PT.bool,
    onSubmit: PT.func,
    setVisModalVindu: PT.func
};

const defaultOnSubmit = (event, state, onSuccess) => {
    event.preventDefault();
    document.getElementById(state.traadMarkupIds[state.valgtTraad.traadId]).click();
    onSuccess();
};

MeldingerSok.defaultProps = {
    visSok: true,
    visCheckbox: false,
    submitButtonValue: 'Vis dialog',
    submitErrorMessage: '',
    submitError: false,
    className: 'meldinger-sok',
    onSubmit: defaultOnSubmit,
    setVisModalVindu: () => {}
};

export default MeldingerSok;
