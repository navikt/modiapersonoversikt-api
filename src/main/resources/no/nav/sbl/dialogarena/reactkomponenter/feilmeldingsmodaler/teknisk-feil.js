import React, { PropTypes as pt } from 'react';
import Modal, { AriaPropType, defaultHelper } from './../modal/modal-module';

const styling = {
    text: {
        marginTop: 30 + 'px',
        textTransform: 'none !important'
    }
};

function TekniskFeil({isOpen, title, closeButton}) {
    const modalProps = {isOpen, title, closeButton};
    return (
        <Modal {...modalProps} width={600} height={180} onClosing={() => {return false;}} ref="modal">
            <section className="default-error">
                <h1 className="robust-ikon-feil-strek" style={styling.text}>{this.props.tekst}</h1>
            </section>
        </Modal>
    );
}

TekniskFeil.defaultProps = {
    isOpen: false,
    title: defaultHelper('Det skjedde en teknisk feil', false, 'h1.vekk'),
    description: defaultHelper('', false, 'div.vekk'),
    closeButton: defaultHelper('', false, 'span')
};

TekniskFeil.propTypes = {
    tekst: pt.string.isRequired,
    title: AriaPropType,
    description: AriaPropType,
    closeButton: AriaPropType,
    isOpen: pt.bool
};

export default TekniskFeil;
