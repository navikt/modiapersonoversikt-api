import React from 'react';
import PT from 'prop-types';
import Modal, { AriaPropType, defaultHelper } from './../modal/modal-module';
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
        const { isOpen, title, description, closeButton } = this.props;
        const modalProps = { isOpen, title, description, closeButton };

        return (
            <Modal {...modalProps} ref="modal" width={600} height={280} onClosing={() => false}>
                <section className="bekreft-dialog" style={ForkasteEndringer.styling.container}>
                    <h1 className="medium-ikon-hjelp-strek">{this.props.hovedtekst}</h1>
                    <ul>
                        <li>{this.props.beskrivendeTekst}</li>
                        <li>
                            <button className="knapp-stor" onClick={this.avbrytCallback}>
                                {this.props.avbryttekst}
                            </button>
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
    title: defaultHelper('Forkaste endringer modal', false, 'h1.vekk'),
    description: defaultHelper('', false, 'div.vekk'),
    closeButton: defaultHelper('Lukk modal', false, 'span.vekk')
};

ForkasteEndringer.propTypes = {
    hovedtekst: PT.string.isRequired,
    avbryttekst: PT.string.isRequired,
    fortsetttekst: PT.string.isRequired,
    beskrivendeTekst: PT.string,
    title: AriaPropType,
    description: AriaPropType,
    closeButton: AriaPropType,
    isOpen: PT.bool,
    wicketurl: PT.string.isRequired,
    wicketcomponent: PT.string.isRequired,
    discardCallback: PT.string.isRequired,
    confirmCallback: PT.string.isRequired
};

export default ForkasteEndringer;
