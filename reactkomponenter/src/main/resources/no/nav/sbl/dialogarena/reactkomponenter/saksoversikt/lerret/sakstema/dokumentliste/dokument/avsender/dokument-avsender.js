import React from 'react';
import PT from 'prop-types';
import { FormattedMessage } from 'react-intl';
import Inngaaende from './inngaaende';
import Utgaaende from './utgaaende';
import Intern from './intern';

const DokumentAvsender = ({ retning, avsender, mottaker, brukerNavn, navn, kategoriNotat }) => {
    const ukjentAvsender = <FormattedMessage id="dokumentinfo.avsender.ukjent" />;
    const ukjentMessage = <FormattedMessage id="dokumentinfo.avsender.fra" values={{ avsender: ukjentAvsender }} />;
    const ukjent = (<span className={retning}>/ {ukjentMessage}</span>);

    const tekstBasertPaaRetning = {
        INN: <Inngaaende brukerNavn={brukerNavn} navn={navn} avsender={avsender} />,
        UT: <Utgaaende mottaker={mottaker} mottakerNavn={navn} />,
        INTERN: <Intern kategoriNotat={kategoriNotat} />
    };

    return tekstBasertPaaRetning[retning] || ukjent;
};

DokumentAvsender.propTypes = {
    retning: PT.string.isRequired,
    avsender: PT.string.isRequired,
    mottaker: PT.string,
    brukerNavn: PT.string,
    navn: PT.string
};

export default DokumentAvsender;
