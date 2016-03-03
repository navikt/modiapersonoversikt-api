import React, { PropTypes as pt } from 'react';
import { FormattedMessage } from 'react-intl';

const Inngaaende = ({ navn }) => {
    const ingaandeMessage = <FormattedMessage id="dokumentinfo.avsender.fra" values={{ avsender: navn }}/>;

    return <div className="dokument-avsender">{ingaandeMessage}</div>;
};

Inngaaende.propTypes = {
    navn: pt.string,
    avsender: pt.string
};

export default Inngaaende;
