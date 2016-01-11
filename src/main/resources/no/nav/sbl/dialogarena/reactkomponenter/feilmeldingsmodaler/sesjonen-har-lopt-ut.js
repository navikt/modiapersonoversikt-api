import React, { PropTypes as pt } from 'react';
import Modal, { AriaPropType, defaultHelper } from './../modal/modal-module';

class SesjonenHarLoptUt extends React.Component {
    render() {
        const {
            isOpen, title, description, closeButton,
            hovedtekst, beskrivendeTekst, fnr,
            avbryttekst, fortsetttekst
            } = this.props;
        const modalProps = { isOpen, title, description, closeButton };
        return (
            <Modal {...modalProps} height={265} width={500} onClosing={() => false}>
                <div className="informasjonsboks timeout">
                    <h1 className="diger">{hovedtekst}</h1>

                    <p className="normal">{beskrivendeTekst}</p>

                    <a className="knapp-hoved" href={'./' + fnr}>
                        {avbryttekst}
                    </a>

                    <a className="knapp-stor" href="../">
                        {fortsetttekst}
                    </a>
                </div>
            </Modal>
        );
    }
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
