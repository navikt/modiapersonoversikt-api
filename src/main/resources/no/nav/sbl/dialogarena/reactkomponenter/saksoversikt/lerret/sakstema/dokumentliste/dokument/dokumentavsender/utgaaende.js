import React, { PropTypes as pt } from 'react';
import { FormattedMessage } from 'react-intl';

const Utgaaende = ({ mottakerNavn}) => {
    const dokumentAvsender = <span className="dokument-avsender-nav">
        <FormattedMessage id="avsender.nav"/>
    </span>;

    const fra = < FormattedMessage id="dokumentinfo.avsender.fra" values={ { avsender: dokumentAvsender } }/>;
    const til = <FormattedMessage id="dokumentinfo.avsender.til" values={ { mottaker: mottakerNavn } }/>;

    return <div className="dokument-avsender">{fra} {til}</div>;
};

Utgaaende.propTypes = {
    mottakerNavn: pt.string
};

export default Utgaaende;
