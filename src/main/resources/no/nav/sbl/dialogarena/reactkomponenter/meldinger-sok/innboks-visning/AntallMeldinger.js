import React from 'react';
import PT from 'prop-types';

const AntallMeldinger = ({ antall }) => {
    const antallCls = antall === 1 ? 'antall-ikon antall-en' : 'antall-ikon antall-flere';
    let antallTekst = antall === 1 ? '' : antall;
    let flereMeldingerAriaLabel = '';

    if (antall > 1) {
        if (antall < 10) {
            antallTekst = antall;
        } else {
            antallTekst = '9+';
        }

        flereMeldingerAriaLabel = <span className="vekk">{`${antallTekst} meldinger i tråden`}</span>;
    }

    return (
        <div className={antallCls}>
            <span aria-hidden="true">{antallTekst}</span>
            {flereMeldingerAriaLabel}
        </div>
    );
};

AntallMeldinger.propTypes = {
    antall: PT.number
};

export default AntallMeldinger;
