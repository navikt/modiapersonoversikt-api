import React from 'react';
import VedleggFeilmelding from './VedleggFeilmelding'


const VedleggFeilmeldingListe = ({feilmeldinger}) => {
    const feilliste = feilmeldinger.map((feilmelding) => (
        <VedleggFeilmelding key={feilmelding.tittel} feilmelding={feilmelding} />
    ));

    return (
        <div>
            {feilliste}
        </div>
    );
};

export default VedleggFeilmeldingListe;
