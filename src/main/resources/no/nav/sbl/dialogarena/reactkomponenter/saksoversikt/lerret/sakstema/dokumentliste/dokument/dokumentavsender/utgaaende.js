import React, { PropTypes as pt } from 'react';
import { FormattedMessage } from 'react-intl';

const Utgaaende = ({ avsenderUT, mottakerUT }) => {
    const avsender = <strong className="dokument-avsender-nav">{avsenderUT}</strong>;
    const fra = < FormattedMessage id="dokumentinfo.avsender.fra" values={ { avsender } }/>;
    const til = mottakerUT === 'SLUTTBRUKER' ? <noscript/> :
        <FormattedMessage id="dokumentinfo.avsender.til" values={ { mottaker: mottakerUT } }/>;

    return <div className="dokument-avsender">{fra}{til}</div>;
};

Utgaaende.propTypes = {
    brukerNavn: pt.string,
    navn: pt.string,
    avsenderInn: pt.string.isRequired
};

export default Utgaaende;
