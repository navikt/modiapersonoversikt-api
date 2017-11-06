import React from 'react';
import PT from 'prop-types';

import { konverterTilMomentDato } from '../../utils';
import Periode from './periode';

export const sorterEtterIdDato = perioder => (
    perioder.sort((a, b) => (
        konverterTilMomentDato(a.fraOgMed).diff(konverterTilMomentDato(b.fraOgMed))
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
        anvisteUtbetalinger: PT.string.isRequired,
        anvistUtbetaling: PT.string.isRequired,
        fraOgMedDato: PT.string.isRequired,
        bruttoBelop: PT.string.isRequired,
        dagsats: PT.string.isRequired,
        tilOgMedDato: PT.string.isRequired,
        kompensasjonsgrad: PT.string.isRequired
    }).isRequired
};

export default PleiepengerUtbetalingerPanel;
