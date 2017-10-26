import React from 'react';
import PT from 'prop-types';
import { FormattedMessage } from 'react-intl';

const Inngaaende = ({ brukerNavn, navn, avsender }) => {
    const dokumentAvsender = avsender === 'SLUTTBRUKER'
        ? brukerNavn
        : <span className="typo-egennavn"> {navn.toLowerCase()}</span>;
    const ingaandeMessage = <FormattedMessage id="dokumentinfo.avsender.fra" values={{ avsender: dokumentAvsender }} />;

    return <span className="dokument-avsender">{ingaandeMessage}</span>;
};

Inngaaende.propTypes = {
    brukerNavn: PT.string,
    navn: PT.string,
    avsender: PT.string
};

export default Inngaaende;
