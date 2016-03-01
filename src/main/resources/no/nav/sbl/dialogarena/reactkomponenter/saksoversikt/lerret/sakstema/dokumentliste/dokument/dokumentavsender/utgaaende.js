import React from 'react';
import { FormattedMessage } from 'react-intl';

const Utgaaende = ({ avsenderUT, mottakerUT }) => {
    const fra = < FormattedMessage id="dokumentinfo.avsender.fra"
                                   values={  { avsender: <strong className={'avsendernavtext'}>{avsenderUT}</strong> }}/>
        ;
    const til = mottakerUT === 'SLUTTBRUKER' ? <noscript/> :
        < FormattedMessage id="dokumentinfo.avsender.til" values={ { mottaker: mottakerUT } }/>;

    return <div className="dokument-avsender">{fra}{til}</div>;
};

export default Utgaaende;
