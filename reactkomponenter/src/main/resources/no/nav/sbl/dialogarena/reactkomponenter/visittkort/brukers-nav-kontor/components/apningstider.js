import React from 'react';
import PT from 'prop-types';

import { UKEDAGER } from '../../../constants';
import { apningstiderType } from '../types';
import Apningstid from './apningstid';

function getApningstiderNoder(apningstider) {
    const apningstiderNoder = UKEDAGER.map(ukedag => {
        let apningstidForUkedag = apningstider.apningstider.find(apningstid => apningstid.ukedag === ukedag);
        if (apningstidForUkedag) {
            return <Apningstid key={ukedag} apningstid={apningstidForUkedag} />;
        }
        return null;
    });
    return apningstiderNoder.filter(node => node);
}

function Apningstider({ apningstider }) {
    let apningstiderNoder = getApningstiderNoder(apningstider);
    if (apningstiderNoder.length === 0) {
        apningstiderNoder = <p>Ingen åpningstider registrert</p>;
    }
    return (
        <div className="infoblokk">
            <h3 className="overskrift">Åpningstider</h3>
            {apningstiderNoder}
        </div>
    );
}

Apningstider.propTypes = PT.shape({
    apningstid: apningstiderType
}).isRequired;

export default Apningstider;
