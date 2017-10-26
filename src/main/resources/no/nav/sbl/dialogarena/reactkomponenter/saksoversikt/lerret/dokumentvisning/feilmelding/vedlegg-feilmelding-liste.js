import React from 'react';
import PT from 'prop-types';
import VedleggFeilmelding from './vedlegg-feilmelding';

const VedleggFeilmeldingListe = ({ feilmeldinger }) => {
    const feilliste = feilmeldinger.map((feilmelding, index) => (
        <VedleggFeilmelding
            name={feilmelding.feilmeldingEnonicKey + index}
            key={feilmelding.tittel + index}
            feilmelding={feilmelding}
        />
    ));

    return (
        <div>
            {feilliste}
        </div>
    );
};

VedleggFeilmeldingListe.propTypes = {
    feilmeldinger: PT.array.isRequired
};

export default VedleggFeilmeldingListe;
