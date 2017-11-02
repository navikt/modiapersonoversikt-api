import React from 'react';
import PT from 'prop-types';
import { FormattedMessage } from 'react-intl';

const Utgaaende = ({ mottaker, mottakerNavn }) => {
    const dokumentAvsender = (
        <span className="dokument-avsender-nav">
            <FormattedMessage id="avsender.nav" />
        </span>
    );

    const fra = < FormattedMessage id="dokumentinfo.avsender.fra" values={{ avsender: dokumentAvsender }} />;

    const dokumentMottaker = <span className="typo-egennavn"> {mottakerNavn.toLowerCase()}</span>;

    const til = mottaker === 'SLUTTBRUKER' ? <noscript /> :
        <FormattedMessage id="dokumentinfo.avsender.til" values={{ mottaker: dokumentMottaker }} />;

    return <span className="dokument-avsender">{fra} {til}</span>;
};

Utgaaende.propTypes = {
    mottaker: PT.string.isRequired,
    mottakerNavn: PT.string
};

export default Utgaaende;
