import React from 'react';
import Modal from './../modal/modal-module';

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

function MeldingerSokModal(props) {
    return (
        <Modal
            ref={modal => props.setRef(modal)}
            title={modalConfig.title}
            description={modalConfig.description}
            closeButton={modalConfig.closeButton}
        >
            {props.children}
        </Modal>
    );
}

export default MeldingerSokModal;
