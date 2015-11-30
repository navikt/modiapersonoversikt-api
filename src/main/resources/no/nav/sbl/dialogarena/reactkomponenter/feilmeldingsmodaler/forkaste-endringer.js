import React from 'react';
import Modal from './../modal/modal-module';
import { autobind } from './../utils/utils-module';
import sendToWicket from './../react-wicket-mixin/wicket-sender';

class ForkasteEndringer extends React.Component {
    constructor(props) {
        super(props);

        autobind(this);
        this.sendToWicket = sendToWicket.bind(this, this.props.wicketurl, this.props.wicketcomponent);
    }

    vis() {
        this.refs.modal.open();
    }

    skjul() {
        this.refs.modal.close();
    }

    avbrytCallback() {
        this.sendToWicket(this.props.discardCallback);
    }

    fortsettCallback() {
        this.sendToWicket(this.props.confirmCallback);
    }

    render() {
        const {isOpen, title, description, closeButton} = this.props;
        const modalProps = { isOpen, title, description, closeButton };

        return (
            <Modal {...modalProps} ref="modal" width={600} height={280} onClosing={() => false}>
                <section className="bekreft-dialog" style={ForkasteEndringer.styling.container}>
                    <h1 className="medium-ikon-hjelp-strek">{this.props.hovedtekst}</h1>
                    <ul>
                        <li>{this.props.beskrivendeTekst}</li>
                        <li>
                            <button className="knapp-stor"
                                    onClick={this.avbrytCallback}>{this.props.avbryttekst}</button>
                        </li>
                        <li>
                            <a href="#" onClick={this.fortsettCallback}>{this.props.fortsetttekst}</a>
                        </li>
                    </ul>
                </section>
            </Modal>
        );
    }
}

ForkasteEndringer.styling = {
    container: {
        padding: '2rem'
    }
};

ForkasteEndringer.defaultProps = {
    isOpen: false,
    title: {
        text: 'Flere Modia-vinduer åpne',
        show: false,
        tag: 'h1.vekk'
    },
    description: {
        text: '',
        show: false,
        tag: 'div.vekk'
    },
    closeButton: {
        text: 'Lukk feilmeldingsmodal, og nåværende tab.',
        show: false,
        tag: 'span.vekk'
    }
};

ForkasteEndringer.propTypes = {
    hovedtekst: React.PropTypes.string.isRequired,
    avbryttekst: React.PropTypes.string.isRequired,
    fortsetttekst: React.PropTypes.string.isRequired,
    beskrivendeTekst: React.PropTypes.string,
    title: React.PropTypes.object,
    description: React.PropTypes.object,
    closeButton: React.PropTypes.object,
    isOpen: React.PropTypes.bool,
    wicketurl: React.PropTypes.string.isRequired,
    wicketcomponent: React.PropTypes.string.isRequired,
    discardCallback: React.PropTypes.string.isRequired,
    confirmCallback: React.PropTypes.string.isRequired
};

export default ForkasteEndringer;
