import React from 'react';
import PT from 'prop-types';

import { apningstidType } from '../types';

function formaterTilToSiffer(siffer) {
    if (siffer.length === 1) {
        return `0${siffer}`;
    }
    return siffer;
}

function Apningstid({ apningstid }) {
    const apentFraTime = formaterTilToSiffer(apningstid.apentFra.time);
    const apentFraMinutt = formaterTilToSiffer(apningstid.apentFra.minutt);
    const stengtFraTime = formaterTilToSiffer(apningstid.apentTil.time);
    const stengtFraMinutt = formaterTilToSiffer(apningstid.apentTil.minutt);
    return (
        <div className="apningstider">
            <p className="apningstid">{apningstid.ukedag}</p>
            <p className="apningstid">{apentFraTime}.{apentFraMinutt} - {stengtFraTime}.{stengtFraMinutt}</p>
        </div>
    );
}

Apningstid.propTypes = PT.shape({
    apningstid: apningstidType
}).isRequired;

export default Apningstid;
