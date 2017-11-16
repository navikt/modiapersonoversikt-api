import React from 'react';
import PT from 'prop-types';
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
        this.vis = this.vis.bind(this);
        this.state = { isOpen: this.props.isOpen };
    }

    vis() {
        this.setState({ isOpen: true });
        this.modaldialog.open();
    }

    skjul() {
        this.setState({ isOpen: false });
        if (this.props.closeButtonCallback) {
            this.props.closeButtonCallback();
        }
    }

    render() {
        const { title, closeButton, tekst } = this.props;
        const modalProps = {
            isOpen: this.state.isOpen, title, closeButton
        };

        if (!this.state.isOpen) {
            return <div />;
        }

        return (
            <Modal
                {...modalProps}
                width={600}
                height={210}
                onClosing={() => (false)}
                ref={(modal) => { this.modaldialog = modal; return true; }}
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
    tekst: PT.string.isRequired,
    title: AriaPropType,
    description: AriaPropType,
    closeButton: AriaPropType,
    isOpen: PT.bool,
    closeButtonCallback: PT.func
};

export default TekniskFeil;
