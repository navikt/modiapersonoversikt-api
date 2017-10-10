import React, { PropTypes as pt } from 'react';
import Modal, { AriaPropType, defaultHelper } from './../modal/modal-module';

const styling = {
    text: {
        marginTop: 30 + 'px',
        textTransform: 'none !important'
    },
    okKnapp: {
        display: 'flex',
        justifyContent: 'center'
    }
};

class TekniskFeil extends React.Component {

    constructor(props) {
        super(props);
        this.skjul = this.skjul.bind(this);
    }

    vis() {
        this.modaldialog.open();
    }

    skjul() {
        this.modaldialog.close();
    }

    render() {
        const { isOpen, title, closeButton, tekst } = this.props;
        const modalProps = { isOpen, title, closeButton };
        return (
            <Modal
                {...modalProps}
                width={600}
                height={210}
                onClosing={() => (false)}
                ref={(modal) => this.modaldialog = modal}
            >
                <section className="default-error">
                    <h1 className="robust-ikon-feil-strek" style={styling.text}>{tekst}</h1>
                </section>
                <div style={styling.okKnapp}>
                    <a className="knapp-stor" onClick={() => this.skjul()}> Ok </a>
                </div>
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
    isOpen: pt.bool
};

export default TekniskFeil;
