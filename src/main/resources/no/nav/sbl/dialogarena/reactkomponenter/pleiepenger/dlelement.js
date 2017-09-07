import React from 'react';

const DLElement = ({ etikett, className = '', children }) => (
    <div className={['blokk-s', className].join(' ')}>
        <dt className="pleiepenger-etikett">{ etikett }</dt>
        <dd className="pleiepenger-verdi">{ children }</dd>
    </div>
);

DLElement.propTypes = {
    etikett: React.PropTypes.string.isRequired,
    className: React.PropTypes.string,
    children: React.PropTypes.node
};

export default DLElement;
