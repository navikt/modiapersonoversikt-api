import React from 'react';
import { FormattedMessage } from 'react-intl';
import Inngaaende from './dokumentavsender/inngaaende';
import Utgaaende from './dokumentavsender/utgaaende';
import Intern from './dokumentavsender/intern';

const DokumentAvsender = ({ retning, avsender, mottaker, brukerNavn, navn}) => {
    const UKJENT = <span className={retning }>/ <FormattedMessage id="dokumentinfo.avsender.fra" values={ { avsender: <FormattedMessage id="dokumentinfo.avsender.ukjent"/> } }/></span>;

    const tekstBasertPaaRetning = {
        INN: <Inngaaende brukerNavn={brukerNavn} navn={navn} avsenderInn={avsender}/>,
        UT: <Utgaaende avsenderUT={avsender} mottakerUT={mottaker}/>,
        INTERN: <Intern />
    };

    return tekstBasertPaaRetning[retning] || UKJENT;
};

DokumentAvsender.propTypes = {
    avsender: React.PropTypes.string.isRequired,
    retning: React.PropTypes.string.isRequired,
    navn: React.PropTypes.string
};

export default DokumentAvsender;
