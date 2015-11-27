import React from 'react';
import Modal from './../modal/modal-module';

class FlereApneVinduer extends React.Component {
    vis() {
        this.refs.modal.open();
    }

    skjul() {
        this.refs.modal.close();
    }

    render() {
        const {isOpen, title, description, closeButton} = this.props;
        const modalProps = { isOpen, title, description, closeButton };

        return (
            <Modal {...modalProps} width={700} height={300}>
                <div className="bekreft-dialog" style={FlereApneVinduer.styling.container}>
                    <h1 className="medium-ikon-hjelp-strek">{this.props.hovedtekst}</h1>
                    <ul>
                        <li>
                            <button className="knapp-stor">{this.props.avbryttekst}</button>
                        </li>
                        <li>
                            <a href="#">{this.props.fortsetttekst}</a>
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
        show: true,
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
    isOpen: React.PropTypes.boolean
};

export default FlereApneVinduer;
