import React, { PropTypes as pt } from 'react';
import { FormattedMessage } from 'react-intl';

const Intern = ({ kategoriNotat }) => {
    const notat = kategoriNotat === 'FORVALTNINGSNOTAT' ?
        <FormattedMessage id="dokumentinfo.forvaltningsnotat" /> :
        <FormattedMessage id="dokumentinfo.internnotat" />;

    return (
        <span className="dokument-avsender">
            {notat}
        </span>
    );
};

Intern.propTypes = {
    kategoriNotat: pt.string.isRequired
};

export default Intern;
