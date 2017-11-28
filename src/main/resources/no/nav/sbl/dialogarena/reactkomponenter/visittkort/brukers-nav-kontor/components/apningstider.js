import React from 'react';
import { OrganiasjonType } from '../types';

function Apningstider({ organisasjon }) {
    return (
        <div className="infoblokk">
            <h3 className="overskrift">Åpningstider</h3>
            <p>{ 'Mandag' }</p>
        </div>
    );
}


Apningstider.propTypes = OrganiasjonType;

export default Apningstider;
