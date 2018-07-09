import React from 'react';
import Modal from './../modal/modal-module';
import PT from 'prop-types';

function getModalConfig(props) {
    return {
        title: {
            text: `${props.moduleName} modal`,
            show: false,
            tag: 'h1.vekk'
        },
        description: {
            text: '',
            show: false,
            tag: 'div.vekk'
        },
        closeButton: {
            text: `Lukk ${props.moduleName.toLowerCase()} modal`,
            show: true,
            tag: 'span.vekk'
        }
    };
}

function MeldingerSokModal(props) {
    const modalConfig = getModalConfig(props);
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

MeldingerSokModal.propTypes = {
    moduleName: PT.string.isRequired,
    setRef: PT.func.isRequired
};

export default MeldingerSokModal;
