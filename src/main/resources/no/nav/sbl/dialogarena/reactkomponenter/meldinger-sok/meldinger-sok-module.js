/* eslint "react/jsx-no-bind": 1 */
import React, {Component} from 'react';
import Modal from './../modal/modal-module';
import ListevisningKomponent from './listevisning';
import ForhandsvisningKomponent from './forhandsvisning';
import Utils from './../utils/utils-module';
import MeldingerSokStore from './meldinger-sok-store';
import ScrollPortal from './../utils/scroll-portal';

const modalConfig = {
    title: {
        text: 'Meldingersøk modal',
        show: false,
        tag: 'h1.vekk'
    },
    description: {
        text: '',
        show: false,
        tag: 'div.vekk'
    },
    closeButton: {
        text: 'Lukk meldingersøk modal',
        show: true,
        tag: 'span.vekk'
    }
};

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
    }
    componentDidMount() {
        this.store.setContainerElement(this.refs.modal.portalElement);
        this.store.addListener(this.storeChanged.bind(this));
    }
    componentWillUnmount() {
        this.store.removeListener(this.storeChanged.bind(this));
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
        this.refs.modal.open();
    }
    skjul() {
        this.refs.modal.close();
    }
    storeChanged() {
        this.setState(this.store.getState());
    }
    lagSokVisning(erTom, tekstlistekomponenter) {
        return (
            <div className={'sok-visning ' + (erTom ? 'hidden' : '')}>
                <ScrollPortal
                    id={this.state.listePanelId}
                    className="sok-liste"
                    role="tablist"
                    tabIndex="-1"
                    aria-live="assertive"
                    aria-atomic="true"
                    aria-controls={this.state.forhandsvisningsPanelId}
                >
                    {tekstlistekomponenter}
                </ScrollPortal>
                <div
                    tabIndex="-1"
                    className="sok-forhandsvisning"
                    role="tabpanel"
                    id={this.state.forhandsvisningsPanelId}
                    aria-atomic="true"
                    aria-live="polite"
                >
                    <ForhandsvisningKomponent traad={this.state.valgtTraad} store={this.store}/>
                </div>
            </div>
        );
    }

    lagVisninger() {
        const tekstlistekomponenter = this.state.traader.map((traad) => <ListevisningKomponent
            key={traad.traadId}
            traad={traad}
            valgtTraad={this.state.valgtTraad}
            store={this.store}
        />);
        const erTom = this.state.traader.length === 0;
        const sokVisning = this.lagSokVisning(erTom, tekstlistekomponenter);
        let tomInnhold;
        if (this.state.feilet) {
            tomInnhold = <h1 className="tom" role="alert" aria-atomic="true">Noe feilet</h1>;
        } else if (this.state.initialisert) {
            tomInnhold = <h1 className="tom" role="alert" aria-atomic="true">Ingen treff</h1>;
        } else {
            tomInnhold = (
                <div className="tom">
                    <img src="../img/ajaxloader/hvit/loader_hvit_128.gif" alt="Henter meldinger"></img>
                </div>
            );
        }

        const tomVisning = (
            <div className={'sok-visning ' + (erTom ? '' : 'hidden')}>
                {tomInnhold}
            </div>
        );
        return {sokVisning, tomVisning};
    }

    render() {
        const {sokVisning, tomVisning} = this.lagVisninger();

        return (
            <Modal
                ref="modal"
                title={modalConfig.title}
                description={modalConfig.description}
                closeButton={modalConfig.closeButton}
            >
                <form
                    className="sok-layout meldinger-sok"
                    onSubmit={this.store.submit.bind(this.store, this.skjul.bind(this))}
                    onKeyDown={this.keyDownHandler}
                >
                    <div tabIndex="-1" className="sok-container">
                        <div>
                            <input
                                type="text"
                                placeholder="Søk"
                                value={this.state.fritekst}
                                title="Søk"
                                onChange={this.onChangeProxy}
                                onKeyDown={
                                    this.store.onKeyDown.bind(
                                        this.store,
                                        document.getElementById(this.state.listePanelId))
                                }
                                aria-controls={this.state.listePanelId}
                            />
                            <img src="../img/sok.svg" alt="Forstørrelseglass-ikon" aria-hidden="true" />
                        </div>
                    </div>
                    {sokVisning}
                    {tomVisning}
                    <input type="submit" value="submit" className="hidden" />
                </form>
            </Modal>
        );
    }
}

MeldingerSok.defaultProps = {
    mode: {
        name: 'MeldingerSok',
        visCheckbox: false,
        submitButtonValue: 'Vis dialog'
    }
};

export default MeldingerSok;
