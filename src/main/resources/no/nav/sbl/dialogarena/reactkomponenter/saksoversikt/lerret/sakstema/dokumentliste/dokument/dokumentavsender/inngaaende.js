import React from 'react';
import { FormattedMessage } from 'react-intl';

const Inngaaende = ({ brukerNavn, navn, avsenderInn }) => {
    const fraAvsender = avsenderInn === 'SLUTTBRUKER' ? brukerNavn : navn;
    const ingaandeMessage = <FormattedMessage className="avsendertext" id="dokumentinfo.avsender.fra"
                                              values={{ avsender: fraAvsender }}/>;

    return <div className="avsendertext">{ingaandeMessage}</div>;
};

export default Inngaaende;
