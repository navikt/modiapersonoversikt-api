import React from 'react';
import { organisasjonType } from '../types';

function NavKontorHeader({ organisasjon }) {
    return (
        <div>
            <h3 className="overskrift">Brukers navkontor</h3>
            <p>{ `${organisasjon.enhetId} ${organisasjon.enhetNavn}` }</p>
        </div>
    );
}

NavKontorHeader.propTypes = {
    organisasjon: organisasjonType.isRequired
};

export default NavKontorHeader;
