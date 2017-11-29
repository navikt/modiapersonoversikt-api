import React from 'react';
import { OrganiasjonType } from '../types';

function NavKontorHeader({ organisasjon }) {
    return (
        <div>
            <h3 className="overskrift">Brukers navkontor</h3>
            <p>{ `${organisasjon.enhetId} ${organisasjon.enhetNavn}` }</p>
        </div>
    );
}

NavKontorHeader.propTypes = OrganiasjonType;

export default NavKontorHeader;