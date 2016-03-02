import React, { PropTypes as pt } from 'react';
import { FormattedMessage } from 'react-intl';

const Inngaaende = ({ brukerNavn, navn, avsenderInn }) => {
    const avsender = avsenderInn === 'SLUTTBRUKER' ? brukerNavn : navn;
    const ingaandeMessage = <FormattedMessage id="dokumentinfo.avsender.fra" values={{ avsender }}/>;

    return <div className="dokument-avsender">{ingaandeMessage}</div>;
};

Inngaaende.propTypes = {
    brukerNavn: pt.string,
    navn: pt.string,
    avsenderInn: pt.string
};

export default Inngaaende;
