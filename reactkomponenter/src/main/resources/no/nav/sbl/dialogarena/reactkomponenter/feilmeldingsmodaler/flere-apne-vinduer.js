import React from 'react';
import PT from 'prop-types';
import Modal, { AriaPropType, defaultHelper } from './../modal/modal-module';

class FlereApneVinduer extends React.Component {
    render() {
        const {
            isOpen, title, description, closeButton,
            hovedtekst, avbrytCallback, avbryttekst,
            fortsettCallback, fortsetttekst
            } = this.props;
        const modalProps = { isOpen, title, description, closeButton };

        return (
            <Modal {...modalProps} width={700} height={300} onClosing={() => false}>
                <section className="bekreft-dialog" style={FlereApneVinduer.styling.container}>
                    <h1 className="medium-ikon-hjelp-strek">{hovedtekst}</h1>
                    <ul>
                        <li>
                            <button className="knapp-stor" onClick={avbrytCallback}>
                                {avbryttekst}
                            </button>
                        </li>
                        <li>
                            <a href="#" onClick={fortsettCallback}>{fortsetttekst}</a>
                        </li>
                    </ul>
                </section>
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
    title: defaultHelper('Flere Modia-vinduer åpne', false, 'h1.vekk'),
    description: defaultHelper('', false, 'div.vekk'),
    closeButton: defaultHelper('Lukk feilmeldingsmodal, og nåværende tab.', false, 'span.vekk')
};

FlereApneVinduer.propTypes = {
    hovedtekst: PT.string.isRequired,
    avbryttekst: PT.string.isRequired,
    fortsetttekst: PT.string.isRequired,
    title: AriaPropType,
    description: AriaPropType,
    closeButton: AriaPropType,
    isOpen: PT.bool,
    fortsettCallback: PT.func.isRequired,
    avbrytCallback: PT.func.isRequired
};

export default FlereApneVinduer;
