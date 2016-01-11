import React, { PropTypes as pt } from 'react';
import Modal, { AriaPropType, defaultHelper } from './../modal/modal-module';

function SesjonenHarLoptUt({isOpen, title, description, closeButton}) {
    const modalProps = { isOpen, title, description, closeButton };
    return (
        <Modal {...modalProps} height={265} width={500} onClosing={() => false}>
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

SesjonenHarLoptUt.defaultProps = {
    isOpen: false,
    title: defaultHelper('Sesjonen har løpt ut', true, 'h1.vekk'),
    description: defaultHelper('Du har vært inaktiv i Modia og sesjonen din har løpt ut. Du kan velge å gå til den brukeren du sist var inne på, eller søke opp en ny bruker.', true, 'div.vekk'),
    closeButton: defaultHelper('', false, 'span.vekk')
};

SesjonenHarLoptUt.propTypes = {
    hovedtekst: pt.string.isRequired,
    avbryttekst: pt.string.isRequired,
    fortsetttekst: pt.string.isRequired,
    beskrivendeTekst: pt.string.isRequired,
    title: AriaPropType,
    description: AriaPropType,
    closeButton: AriaPropType,
    isOpen: pt.bool,
    fnr: pt.string.isRequired
};

export default SesjonenHarLoptUt;
