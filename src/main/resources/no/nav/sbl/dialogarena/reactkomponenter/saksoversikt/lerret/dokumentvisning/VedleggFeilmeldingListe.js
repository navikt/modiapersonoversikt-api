import React from 'react';
import VedleggFeilmelding from './VedleggFeilmelding'


const VedleggFeilmeldingListe = ({feilmeldinger}) => {
    const feilliste = feilmeldinger.map((feilmelding) => (
        <VedleggFeilmelding feilmelding={feilmelding} />
    ));

    return (
        <div>
            {feilliste}
        </div>
    );
};

export default VedleggFeilmeldingListe;
