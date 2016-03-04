import React, { PropTypes as pt } from 'react';
import { FormattedMessage } from 'react-intl';

const Utgaaende = ({ mottaker, mottakerNavn}) => {
    const dokumentAvsender = <span className="dokument-avsender-nav">
        <FormattedMessage id="avsender.nav"/>
    </span>;

    const fra = < FormattedMessage id="dokumentinfo.avsender.fra" values={ { avsender: dokumentAvsender } }/>;

    const til = mottaker === 'SLUTTBRUKER' ? <noscript/> :
        <FormattedMessage id="dokumentinfo.avsender.til" values={ { mottaker: mottakerNavn } }/>;

    return <div className="dokument-avsender">{fra} {til}</div>;
};

Utgaaende.propTypes = {
    mottaker: pt.string.isRequired,
    mottakerNavn: pt.string
};

export default Utgaaende;
