import React from 'react';

const DLElement = ({ etikett, className = '', children }) => (
    <div className={ ['blokk-s', className].join(' ') }>
        <dt className="pleiepenger-etikett">{ etikett }</dt>
        <dd className="pleiepenger-verdi">{ children }</dd>
    </div>
);

export default DLElement;
