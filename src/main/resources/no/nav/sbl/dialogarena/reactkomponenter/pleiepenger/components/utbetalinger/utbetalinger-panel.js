import React from 'react';

import { formaterJavaDateTilMoment } from '../../formatering-utils';
import Periode from './periode';

export const sorterEtterIdDato = perioder => (
    perioder.sort((a, b) => (
        formaterJavaDateTilMoment(b.fraOgMed).diff(formaterJavaDateTilMoment(a.fraOgMed))
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
    perioder: React.PropTypes.arrayOf(React.PropTypes.object).isRequired,
    tekst: React.PropTypes.shape({
        periode: React.PropTypes.string.isRequired,
        pleiepengegrad: React.PropTypes.string.isRequired,
        pleiepengedager: React.PropTypes.string.isRequired,
        kommendeUtbetalinger: React.PropTypes.string.isRequired,
        anvistUtbetaling: React.PropTypes.string.isRequired,
        fraOgMedDato: React.PropTypes.string.isRequired,
        bruttoBelop: React.PropTypes.string.isRequired,
        dagsats: React.PropTypes.string.isRequired,
        tilOgMedDato: React.PropTypes.string.isRequired,
        kompensasjonsgrad: React.PropTypes.string.isRequired
    }).isRequired
};

export default PleiepengerUtbetalingerPanel;
