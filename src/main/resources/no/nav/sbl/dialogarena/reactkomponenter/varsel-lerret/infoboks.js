import React from 'react';
import PT from 'prop-types';

function Infoboks({ tekst, style }) {
    return (
        <div className="feilmelding" style={style}>
            <div className="robust-ikon-feil-gra"></div>
            <span className="stor">{tekst}</span>
        </div>
    );
}

Infoboks.propTypes = {
    tekst: PT.string.isRequired,
    style: PT.object
};

export default Infoboks;
