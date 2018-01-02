import React from 'react';
import Modal from "../modal/modal-module";
import Forhandsvisning from "../meldinger-sok/forhandsvisning";
import MeldingerSok from "../meldinger-sok/meldinger-sok-module";
import ScrollPortal from "../utils/scroll-portal";

const modalConfig = {
    title: {
        text: 'Svar-flere-modal',
        show: false,
        tag: 'h1.vekk'
    },
    description: {
        text: '',
        show: false,
        tag: 'div.vekk'
    },
    closeButton: {
        text: 'Lukk svar-flere-modal',
        show: true,
        tag: 'span.vekk'
    }
};

class SvarFlere extends MeldingerSok {

    // getInitialState() {
    //     this.store = new MeldingerSokStore($.extend({}, {
    //         fritekst: '',
    //         traader: [],
    //         valgteTraader: [],
    //         traadMarkupIds: {},
    //         listePanelId: Utils.generateId('sok-liste-'),
    //         forhandsvisningsPanelId: Utils.generateId('sok-forhandsvisningsPanelId-')
    //     }, this.props));
    //     return this.store.getState();
    // }
    lagSokVisning(erTom, tekstlistekomponenter) {
        return (
            <div className={'svar-flere-visning ' + (erTom ? 'hidden' : '')}>
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
                    <Forhandsvisning traad={this.state.valgtTraad}/>
                </div>
            </div>
        );
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
                {sokVisning}
                {tomVisning}
            </Modal>
        );
    }

}

export default SvarFlere;