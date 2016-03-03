import React, { PropTypes as pt } from 'react';
import { FormattedMessage } from 'react-intl';

const Inngaaende = ({ brukerNavn, navn, avsender }) => {
    const avsender = avsender === 'SLUTTBRUKER' ? brukerNavn : navn;
    const ingaandeMessage = <FormattedMessage id="dokumentinfo.avsender.fra" values={{ avsender }}/>;

    return <div className="dokument-avsender">{ingaandeMessage}</div>;
};

Inngaaende.propTypes = {
    brukerNavn: pt.string,
    navn: pt.string,
    avsender: pt.string
};

export default Inngaaende;
