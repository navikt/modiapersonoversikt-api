import React from 'react';
import PT from 'prop-types';

const DLElement = ({ etikett, className = '', children }) => (
    <div className={['blokk-s', className].join(' ')}>
        <dt className="pleiepenger-etikett">{ etikett }</dt>
        <dd className="pleiepenger-verdi">{ children }</dd>
    </div>
);

DLElement.propTypes = {
    etikett: PT.string.isRequired,
    className: PT.string,
    children: PT.node
};

export default DLElement;
