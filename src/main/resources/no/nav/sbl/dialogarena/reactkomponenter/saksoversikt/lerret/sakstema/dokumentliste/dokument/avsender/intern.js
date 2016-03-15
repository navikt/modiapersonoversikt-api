import React from 'react';
import { FormattedMessage } from 'react-intl';

const Intern = ({ kategoriNotat }) => {
    const notat = kategoriNotat === 'FORVALTNINGSNOTAT' ?
        <FormattedMessage id="dokumentinfo.forvaltningsnotat"/> :
        <FormattedMessage id="dokumentinfo.internnotat"/>;

    return (
        <span className="dokument-avsender">
            {notat}
        </span>
    );
};

export default Intern;
