import React from 'react';
import PT from 'prop-types';

import { endash } from '../../../constants'
import { apningstidType } from '../types';

function formaterTilToSiffer(siffer) {
    if (siffer.length === 1) {
        return `0${siffer}`;
    }
    return siffer;
}

function getValueForUkedag(ukedag) {
    switch (ukedag.toUpperCase()) {
        case 'LORDAG':
            return 'Lørdag';
        case 'SONDAG':
            return 'Søndag';
        default:
            return ukedag;
    }
}

function Apningstid({ apningstid }) {
    const apentFraTime = formaterTilToSiffer(apningstid.apentFra.time);
    const apentFraMinutt = formaterTilToSiffer(apningstid.apentFra.minutt);
    const stengtFraTime = formaterTilToSiffer(apningstid.apentTil.time);
    const stengtFraMinutt = formaterTilToSiffer(apningstid.apentTil.minutt);
    const ukedagValue = getValueForUkedag(apningstid.ukedag);
    return (
        <div className="apningstider">
            <p className="apningstid">{ukedagValue}</p>
            <p className="apningstid">{apentFraTime}.{apentFraMinutt} {endash} {stengtFraTime}.{stengtFraMinutt}</p>
        </div>
    );
}

Apningstid.propTypes = PT.shape({
    apningstid: apningstidType
}).isRequired;

export default Apningstid;
