import React from 'react';
import Modal from './../modal/modal-module';

class FlereApneVinduer extends React.Component {
    render() {
        const {isOpen, title, description, closeButton} = this.props;
        const modalProps = { isOpen, title, description, closeButton };

        return (
            <Modal {...modalProps} ref="modal" width={700} height={300} onClosing={() => false}>
                <div className="bekreft-dialog" style={FlereApneVinduer.styling.container}>
                    <h1 className="medium-ikon-hjelp-strek">{this.props.hovedtekst}</h1>
                    <ul>
                        <li>
                            <button className="knapp-stor" onClick={this.props.avbrytCallback}>{this.props.avbryttekst}</button>
                        </li>
                        <li>
                            <a href="#" onClick={this.props.fortsettCallback}>{this.props.fortsetttekst}</a>
                        </li>
                    </ul>
                </div>
            </Modal>
        );
    }
}

FlereApneVinduer.styling = {
    container: {
        padding: '2rem'
    }
};

FlereApneVinduer.defaultProps = {
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

FlereApneVinduer.propTypes = {
    hovedtekst: React.PropTypes.string.isRequired,
    avbryttekst: React.PropTypes.string.isRequired,
    fortsetttekst: React.PropTypes.string.isRequired,
    title: React.PropTypes.object,
    description: React.PropTypes.object,
    closeButton: React.PropTypes.object,
    isOpen: React.PropTypes.bool,
    fortsettCallback: React.PropTypes.func.isRequired,
    avbrytCallback: React.PropTypes.func.isRequired
};

export default FlereApneVinduer;
