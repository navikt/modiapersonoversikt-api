import React, { PropTypes as pt } from 'react';
import { FormattedMessage } from 'react-intl';

const Utgaaende = ({ avsender, mottakerNavn}) => {
    const dokumentAvsender = <strong className="dokument-avsender-nav">NAV</strong>;
    const fra = < FormattedMessage id="dokumentinfo.avsender.fra" values={ { avsender: dokumentAvsender } }/>;
    const til = <FormattedMessage id="dokumentinfo.avsender.til" values={ { mottaker: mottakerNavn } }/>;

    return <div className="dokument-avsender">{fra}{til}</div>;
};

Utgaaende.propTypes = {
    mottakerNavn: pt.string.isRequired,
    avsender: pt.string.isRequired
};

export default Utgaaende;
