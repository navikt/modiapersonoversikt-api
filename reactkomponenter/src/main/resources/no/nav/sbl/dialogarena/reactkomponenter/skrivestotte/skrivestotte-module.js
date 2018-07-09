/* eslint "react/jsx-no-bind": 1 */
import React, { Component } from 'react';
import Modal from './../modal/modal-module';
import Utils from './../utils/utils-module';
import TekstForhandsvisning from './tekst-forhandsvisning';
import TekstListeKomponent from './tekst-liste';
import KnaggInput from './../knagginput/knagginput-module';
import SkrivestotteStore from './skrivestotte-store';
import ScrollPortal from './../utils/scroll-portal';

const modalConfig = {
    title: {
        text: 'Skrivestøtte modal',
        show: false,
        tag: 'h1.vekk'
    },
    description: {
        text: '',
        show: false,
        tag: 'div.vekk'
    },
    closeButton: {
        text: 'Lukk skrivestøtte modal',
        show: true,
        tag: 'span.vekk'
    }
};

class Skrivestotte extends Component {

    componentWillMount() {
        this.store = new SkrivestotteStore($.extend({}, {
            knagger: [],
            fritekst: '',
            tekster: [],
            valgtTekst: {},
            valgtLocale: Utils.Constants.LOCALE_DEFAULT,
            listePanelId: Utils.generateId('sok-layout-'),
            forhandsvisningsPanelId: Utils.generateId('sok-layout-')
        }, this.props));

        this.setState((prevState, props) => {
           return this.store.getState();
        });

        this.keyDownHandler = this.keyDownHandler.bind(this);
    }
    componentDidMount() {
        this.store.setContainerElement(this.modalRef.portalElement);
        this.store.addListener(this.storeChanged.bind(this));
        this.store.onChange({ fritekst: this.state.fritekst, knagger: this.state.knagger });
    }
    componentWillUnmount() {
        this.store.removeListener(this.storeChanged.bind(this));
    }
    keyDownHandler(event) {
        if (event.keyCode === 13) {
            this.store.submit(() => this.skjul(), event);
        }
    }
    vis() {
        this.modalRef.open();
    }
    skjul() {
        this.modalRef.close();
    }
    storeChanged() {
        this.setState(this.store.getState());
    }
    render() {
        const tekstlistekomponenter = this.state.tekster.map((tekst) => (
            <TekstListeKomponent
                key={tekst.key}
                tekst={tekst}
                valgtTekst={this.state.valgtTekst}
                locale={this.state.valgtLocale}
                store={this.store}
            />
        ));

        const erTom = this.state.tekster.length === 0;
        const sokVisning = (
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
                    <TekstForhandsvisning
                        tekst={this.state.valgtTekst}
                        locale={this.state.valgtLocale}
                        store={this.store}
                    />
                </div>
            </div>
        );

        const tomVisning = (
            <div className={'sok-visning ' + (erTom ? '' : 'hidden')}>
                <h1 className="tom" role="alert" aria-atomic="true">Ingen treff</h1>
            </div>
        );
        return (
            <Modal
                ref={ref => this.modalRef = ref}
                skipFocus={['.knagg > button']}
                title={modalConfig.title}
                description={modalConfig.description}
                closeButton={modalConfig.closeButton}
                width={904}
                height={600}
            >
                <form
                    className="sok-layout tekstforslag"
                    onSubmit={this.store.submit.bind(this.store, () => this.skjul())}
                    onKeyDown={this.keyDownHandler}
                >
                    <div tabIndex="-1" className="sok-container">
                        <KnaggInput
                            knagger={this.state.knagger}
                            fritekst={this.state.fritekst}
                            store={this.store}
                            tablisteId={this.state.listePanelId}
                            placeholder={'Søk'}
                        />
                    </div>
                    <fieldset className="wcag">
                        <legend>Tekst liste</legend>
                        {sokVisning}
                    </fieldset>
                    {tomVisning}
                    <input type="submit" value="submit" className="hidden" />
                </form>
            </Modal>
        );
    }
}

export default Skrivestotte;
