import React from 'react';
import PT from 'prop-types';

import { UKEDAGER } from '../../../constants';
import { apningstiderType } from '../types';
import Apningstid from './apningstid';

function getApningstiderNoder(apningstider) {
    const apningstiderNoder = [];
    UKEDAGER.forEach(ukedag => {
        let apningstidForUkedag = apningstider.apningstider.find(apningstid => apningstid.ukedag === ukedag);
        if (apningstidForUkedag) {
            apningstiderNoder.push(<Apningstid key={ukedag} apningstid={apningstidForUkedag} />);
        }
    });
    return apningstiderNoder;
}

function Apningstider({ apningstider }) {
    const apningstiderNoder = getApningstiderNoder(apningstider);
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
