import React from 'react';
import PT from 'prop-types';
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
    kategoriNotat: PT.string.isRequired
};

export default Intern;
