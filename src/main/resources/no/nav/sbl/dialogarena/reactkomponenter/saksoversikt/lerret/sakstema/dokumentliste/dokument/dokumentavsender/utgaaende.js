import React from 'react';
import { FormattedMessage } from 'react-intl';


const Utgaaende = ({ avsenderUT, mottakerUT }) => {
    const fra = < FormattedMessage id="dokumentinfo.avsender.fra" values={{avsender:avsenderUT}}/>;
    if (mottakerUT === 'SLUTTBRUKER') {
        return fra;
    }
    const til = < FormattedMessage id="dokumentinfo.avsender.til" values={{mottaker:mottakerUT}}/>;
    return <span>{ut}{til}</span>;
}

export default Utgaaende;
