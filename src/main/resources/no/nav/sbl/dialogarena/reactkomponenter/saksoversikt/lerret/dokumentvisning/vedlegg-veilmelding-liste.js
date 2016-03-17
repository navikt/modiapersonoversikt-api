import React from 'react';
import VedleggFeilmelding from './vedlegg-feilmelding'


const VedleggFeilmeldingListe = ({feilmeldinger}) => {
    const feilliste = feilmeldinger.map((feilmelding, index) => (
        <VedleggFeilmelding name={feilmelding.feilmeldingEnonicKey + index} key={feilmelding.tittel} feilmelding={feilmelding} />
    ));

    return (
        <div>
            {feilliste}
        </div>
    );
};

export default VedleggFeilmeldingListe;
