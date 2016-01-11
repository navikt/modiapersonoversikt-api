import React, { PropTypes as pt } from 'react';
import Modal, { AriaPropType, defaultHelper } from './../modal/modal-module';

function FlereApneVinduer({isOpen, title, description, closeButton}) {
    const modalProps = { isOpen, title, description, closeButton };

    return (
        <Modal {...modalProps} width={700} height={300} onClosing={() => false}>
            <section className="bekreft-dialog" style={FlereApneVinduer.styling.container}>
                <h1 className="medium-ikon-hjelp-strek">{this.props.hovedtekst}</h1>
                <ul>
                    <li>
                        <button className="knapp-stor" onClick={this.props.avbrytCallback}>
                            {this.props.avbryttekst}
                        </button>
                    </li>
                    <li>
                        <a href="#" onClick={this.props.fortsettCallback}>{this.props.fortsetttekst}</a>
                    </li>
                </ul>
            </section>
        </Modal>
    );
}

FlereApneVinduer.styling = {
    container: {
        padding: '2rem'
    }
};

FlereApneVinduer.defaultProps = {
    isOpen: false,
    title: defaultHelper('Flere Modia-vinduer åpne', false, 'h1.vekk'),
    description: defaultHelper('', false, 'div.vekk'),
    closeButton: defaultHelper('Lukk feilmeldingsmodal, og nåværende tab.', false, 'span.vekk')
};

FlereApneVinduer.propTypes = {
    hovedtekst: pt.string.isRequired,
    avbryttekst: pt.string.isRequired,
    fortsetttekst: pt.string.isRequired,
    title: AriaPropType,
    description: AriaPropType,
    closeButton: AriaPropType,
    isOpen: pt.bool,
    fortsettCallback: pt.func.isRequired,
    avbrytCallback: pt.func.isRequired
};

export default FlereApneVinduer;
