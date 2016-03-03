import React, { PropTypes as pt } from 'react';
import { FormattedMessage } from 'react-intl';

const Utgaaende = ({ avsender, mottaker }) => {
    const dokumentAvsender = <strong className="dokument-avsender-nav">{avsender}</strong>;
    const fra = < FormattedMessage id="dokumentinfo.avsender.fra" values={ { avsender: dokumentAvsender } }/>;
    const til = mottaker === 'SLUTTBRUKER' ? <noscript/> :
        <FormattedMessage id="dokumentinfo.avsender.til" values={ { mottaker } }/>;

    return <div className="dokument-avsender">{fra}{til}</div>;
};

Utgaaende.propTypes = {
    mottaker: pt.string.isRequired,
    avsender: pt.string.isRequired
};

export default Utgaaende;
