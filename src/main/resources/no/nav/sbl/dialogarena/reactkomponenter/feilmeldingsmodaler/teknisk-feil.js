import React, { PropTypes as pt } from 'react';
import Modal, { AriaPropType, defaultHelper } from './../modal/modal-module';
import { autobind } from './../utils/utils-module';
import sendToWicket from './../react-wicket-mixin/wicket-sender';

const styling = {
    text: {
        marginTop: 30 + 'px',
        textTransform: 'none !important'
    },
    okKnapp: {
       marginLeft: 18 + 'rem',
    }
};

class TekniskFeil extends React.Component {

    vis() {
        this.refs.modal.open();
    }

    skjul() {
        this.refs.modal.close();
    }

    render() {
        const { isOpen, title, closeButton, tekst, fnr} = this.props;
        console.log('rn', fnr);
        const modalProps = { isOpen, title, closeButton, fnr };
        return (
            <Modal {...modalProps} width={600} height={180} onClosing={() => (false)} ref="modal">
                <section className="default-error">
                    <h1 className="robust-ikon-feil-strek" style={styling.text}>{tekst}</h1>
                </section>
                <a className="knapp-stor" style={styling.okKnapp} href={'/modiabrukerdialog/person/'+fnr}> Ok </a>
            </Modal>
        );
    }
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
    isOpen: pt.bool,
    fnr: pt.string.isRequired
};

export default TekniskFeil;
