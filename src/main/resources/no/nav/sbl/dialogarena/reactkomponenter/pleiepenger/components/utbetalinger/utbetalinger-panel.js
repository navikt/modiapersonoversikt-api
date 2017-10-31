import React from 'react';
import PT from 'prop-types';

import { konverterTilMomentDato } from '../../utils';
import Periode from './periode';

export const sorterEtterIdDato = perioder => (
    perioder.sort((a, b) => (
        konverterTilMomentDato(b.fraOgMed).diff(konverterTilMomentDato(a.fraOgMed))
    ))
);

const PleiepengerUtbetalingerPanel = ({ perioder, tekst }) => {
    const perioderKomponenter = sorterEtterIdDato(perioder).map((periode, index) =>
        (<Periode key={index} tekst={tekst} periode={periode} periodeNummer={index + 1} />));

    return (
        <div className="pleiepenger-utbetalinger">
            {perioderKomponenter}
        </div>
    );
};

PleiepengerUtbetalingerPanel.propTypes = {
    perioder: PT.arrayOf(PT.object).isRequired,
    tekst: PT.shape({
        periode: PT.string.isRequired,
        pleiepengegrad: PT.string.isRequired,
        pleiepengedager: PT.string.isRequired,
        kommendeUtbetalinger: PT.string.isRequired,
        anvistUtbetaling: PT.string.isRequired,
        fraOgMedDato: PT.string.isRequired,
        bruttoBelop: PT.string.isRequired,
        dagsats: PT.string.isRequired,
        tilOgMedDato: PT.string.isRequired,
        kompensasjonsgrad: PT.string.isRequired
    }).isRequired
};

export default PleiepengerUtbetalingerPanel;
