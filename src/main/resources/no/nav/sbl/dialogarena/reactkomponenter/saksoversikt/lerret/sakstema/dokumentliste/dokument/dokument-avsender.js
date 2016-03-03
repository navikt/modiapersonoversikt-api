import React, { PropTypes as pt } from 'react';
import { FormattedMessage } from 'react-intl';
import Inngaaende from './dokumentavsender/inngaaende';
import Utgaaende from './dokumentavsender/utgaaende';
import Intern from './dokumentavsender/intern';

const DokumentAvsender = ({ retning, avsender, mottaker, brukerNavn, navn }) => {
    const ukjentAvsender = <FormattedMessage id="dokumentinfo.avsender.ukjent"/>;
    const ukjentMessage = <FormattedMessage id="dokumentinfo.avsender.fra" values={ { avsender: ukjentAvsender } }/>;
    const UKJENT = (<span className={retning}>/ {ukjentMessage}</span>);

    const tekstBasertPaaRetning = {
        INN: <Inngaaende brukerNavn={brukerNavn} navn={navn} avsender={avsender}/>,
        UT: <Utgaaende avsender={avsender} mottaker={mottaker}/>,
        INTERN: <Intern />
    };

    return tekstBasertPaaRetning[retning] || UKJENT;
};

DokumentAvsender.propTypes = {
    retning: pt.string.isRequired,
    avsender: pt.string.isRequired,
    mottaker: pt.string,
    brukerNavn: pt.string,
    navn: pt.string
};

export default DokumentAvsender;
