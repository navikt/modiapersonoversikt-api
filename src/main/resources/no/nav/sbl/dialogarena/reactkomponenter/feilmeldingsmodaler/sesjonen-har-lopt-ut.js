import React from 'react';
import Modal from './../modal/modal-module';
import { autobind } from './../utils/utils-module';
import sendToWicket from './../react-wicket-mixin/wicket-sender';

class SesjonenHarLoptUt extends React.Component {
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

    render() {
        const {isOpen, title, description, closeButton} = this.props;
        const modalProps = {isOpen, title, description, closeButton};
        return (
            <Modal {...modalProps} height={265} width={500} onClosing={() => false} ref="modal">
                <div className="informasjonsboks timeout">
                    <h1 className="diger">{this.props.hovedtekst}</h1>

                    <p className="normal">{this.props.beskrivendeTekst}</p>

                    <a className="knapp-hoved" href={'./' + this.props.fnr}>
                        {this.props.avbryttekst}
                    </a>

                    <a className="knapp-stor" href="../">
                        {this.props.fortsetttekst}
                    </a>
                </div>
            </Modal>
        );
    }
}

SesjonenHarLoptUt.defaultProps = {
    isOpen: false,
    title: {
        text: 'Sesjonen har løpt ut',
        show: true,
        tag: 'h1.vekk'
    },
    description: {
        text: 'Du har vært inaktiv i Modia og sesjonen din har løpt ut. Du kan velge å gå til den brukeren du sist var inne på, eller søke opp en ny bruker.',
        show: true,
        tag: 'div.vekk'
    },
    closeButton: {
        text: '',
        show: false,
        tag: 'span.vekk'
    }
};

SesjonenHarLoptUt.propTypes = {
    hovedtekst: React.PropTypes.string.isRequired,
    avbryttekst: React.PropTypes.string.isRequired,
    fortsetttekst: React.PropTypes.string.isRequired,
    beskrivendeTekst: React.PropTypes.string.isRequired,
    title: React.PropTypes.object,
    description: React.PropTypes.object,
    closeButton: React.PropTypes.object,
    isOpen: React.PropTypes.bool,
    wicketurl: React.PropTypes.string.isRequired,
    wicketcomponent: React.PropTypes.string.isRequired,
    fnr: React.PropTypes.string.isRequired
}
;

export default SesjonenHarLoptUt;
