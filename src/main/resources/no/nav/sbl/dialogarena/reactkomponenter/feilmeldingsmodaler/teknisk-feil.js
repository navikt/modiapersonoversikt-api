import React from 'react';
import Modal from './../modal/modal-module';
import { autobind } from './../utils/utils-module';
import sendToWicket from './../react-wicket-mixin/wicket-sender';

class TekniskFeil extends React.Component {
    constructor(props) {
        super(props);
        autobind(this);
        this.sendToWicket = sendToWicket.bind(this, this.props.wicketurl, this.props.wicketcomponent);
    }

    render() {
        const {isOpen, title, closeButton} = this.props;
        const modalProps = {isOpen, title, closeButton};
        return (
            <Modal {...modalProps} width={600} height={180} onClosing={() => {return false;}} ref="modal">
                <section className="default-error">
                    <h1 className="robust-ikon-feil-strek" style={TekniskFeil.styling.text}>{this.props.tekst}</h1>
                </section>
            </Modal>
        );
    }
}

TekniskFeil.styling = {
    text: {
        marginTop: 30 + 'px',
        textTransform: 'none !important'
    }
};

TekniskFeil.defaultProps = {
    isOpen: false,
    title: {
        text: 'Det skjedde en teknisk feil.',
        show: false,
        tag: 'h1.vekk'
    },
    description: {
        text: '',
        show: false,
        tag: 'div.vekk'
    },
    closeButton: {
        text: '',
        show: false,
        tag: 'span'
    }
};

TekniskFeil.propTypes = {
    tekst: React.PropTypes.string.isRequired,
    title: React.PropTypes.object,
    wicketurl: React.PropTypes.string.isRequired,
    wicketcomponent: React.PropTypes.string.isRequired,
    closeButton: React.PropTypes.object,
    isOpen: React.PropTypes.bool
};


export default TekniskFeil;
