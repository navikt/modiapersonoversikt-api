import React from 'react';
import Modal from './../modal/modal-module';

class ForkateEndringer extends React.Component {
    render() {
        const {isOpen, title, description, closeButton} = this.props;
        const modalProps = { isOpen, title, description, closeButton };

        return (
            <Modal {...modalProps} ref="modal" width={700} height={300} onClosing={() => false}>
                <section className="bekreft-dialog" style={ForkateEndringer.styling.container}>
                    <h1 className="medium-ikon-hjelp-strek">{this.props.hovedtekst}</h1>
                    <ul>
                        <li>{this.props.beskrivendeTekst}</li>
                        <li>
                            <button className="knapp-stor" onClick={this.props.avbrytCallback}>{this.props.avbryttekst}</button>
                        </li>
                        <li>
                            <a href="#" onClick={this.props.fortsettCallback}>{this.props.fortsetttekst}</a>
                        </li>
                    </ul>
                </section>
            </Modal>
        );
    }
}

ForkateEndringer.styling = {
    container: {
        padding: '2rem'
    }
};

ForkateEndringer.defaultProps = {
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

ForkateEndringer.propTypes = {
    hovedtekst: React.PropTypes.string.isRequired,
    avbryttekst: React.PropTypes.string.isRequired,
    fortsetttekst: React.PropTypes.string.isRequired,
    beskrivendeTekst: React.PropTypes.string,
    title: React.PropTypes.object,
    description: React.PropTypes.object,
    closeButton: React.PropTypes.object,
    isOpen: React.PropTypes.bool,
    fortsettCallback: React.PropTypes.func.isRequired,
    avbrytCallback: React.PropTypes.func.isRequired
};

export default ForkateEndringer;
