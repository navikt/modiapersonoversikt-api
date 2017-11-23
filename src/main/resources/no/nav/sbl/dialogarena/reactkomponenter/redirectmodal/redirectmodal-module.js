import React, { Component, PropTypes as PT } from 'react';
import Modal, { AriaPropType, defaultHelper } from './../modal/modal-module';
import Alertstripe from './../alertstriper/alertstripe-module';

const tekst = "Du har endret bruker i et annet vindu. Di kan ikke jobbe med 2 brukere samtidig.\n" +
    "                        Velger du å endre bruker mister du arbeidet du ikke har lagret.";

class Redirectmodal extends Component {
    constructor(props) {
        super(props);
        this.state = {
            fnr: null,
            endreCallback: () => {},
            beholdCallback: () => {}
        };
    }

    vis(fnr, endreCallback, beholdCallback) {
        this.setState({ fnr, endreCallback, beholdCallback });
        this.refs.modal.open();
    }

    skjul() {
        this.refs.modal.close();
    }

    render() {
        const { isOpen, title, description, closeButton } = this.props;
        const modalProps = { isOpen, title, description, closeButton };

        return (
            <Modal {...modalProps} ref="modal" width={700} height={248} onClosing={() => false}>
                <section className="redirect-dialog">
                    <h1 className="blokk-xs">Du har endret bruker</h1>
                    <Alertstripe tekst={tekst} type="info" />
                    <p>Ønsker du å endre bruker til {this.state.fnr}?</p>
                    <button className="knapp-hoved knapp-stor" onClick={this.state.endreCallback}>Endre</button>
                    <button className="knapp-stor" onClick={this.state.beholdCallback}>Behold</button>
                </section>
            </Modal>
        );
    }
}

Redirectmodal.defaultProps = {
    isOpen: false,
    title: defaultHelper('Endret bruker i kontekst', false, 'h1.vekk'),
    description: defaultHelper('', false, 'div.vekk'),
    closeButton: defaultHelper('Avbryter endringen', false, 'span.vekk')
};

Redirectmodal.propTypes = {
    title: AriaPropType,
    description: AriaPropType,
    closeButton: AriaPropType,
    isOpen: PT.bool,
};

export default Redirectmodal;
