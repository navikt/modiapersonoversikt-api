import React from 'react';
import { FormattedMessage } from 'react-intl';

const Inngaaende = ({ brukerNavn, navn, avsenderInn }) => {
    const fraAvsender = avsenderInn === 'SLUTTBRUKER' ? brukerNavn : navn;
    return <FormattedMessage id="dokumentinfo.avsender.fra" values={{avsender: fraAvsender}}/>;
}

export default Inngaaende;
