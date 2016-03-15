import React, { PropTypes as pt } from 'react';
import { FormattedMessage } from 'react-intl';

const Inngaaende = ({ brukerNavn, navn, avsender }) => {
    const dokumentAvsender = avsender === 'SLUTTBRUKER' ? brukerNavn : navn;
    const ingaandeMessage = <FormattedMessage id="dokumentinfo.avsender.fra" values={{ avsender: dokumentAvsender }}/>;

    return <span className="dokument-avsender">{ingaandeMessage}</span>;
};

Inngaaende.propTypes = {
    brukerNavn: pt.string,
    navn: pt.string,
    avsender: pt.string
};

export default Inngaaende;
