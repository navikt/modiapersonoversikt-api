import React from 'react';
import moment from 'moment';

const formaterJavaDate = (dato) =>
    moment(new Date(dato.year, dato.monthValue - 1, dato.dayOfMonth)).format('DD.MM.YYYY');

const Periode = ({periode, periodeNummer}) => {
    const fraOgMed = formaterJavaDate(periode.fraOgMed);
    const vedtak = periode.vedtakListe.map((vedtak, index) => (<Vedtak key={index} vedtak={vedtak}/>));

    return (
        <section className="periode">
            <h2>Periode {periodeNummer} {fraOgMed} </h2>
            <div className="periodeinfo">
                <dl>
                    <dt>Pleiepengergrad</dt>
                    <dd>50 %</dd>
                    <dt>Pleiepengerdager</dt>
                    <dd>17</dd>
                </dl>
            </div>
            <article className="utbetalinger">
                <h3 className="utbetalinger-header" aria-label="Ekspanderingsliste">Kommende utbetalinger</h3>
                <ul className="vedtaksliste">
                    {vedtak}
                </ul>
            </article>
        </section>
    )
};

const Vedtak = ({vedtak}) => {
    const fraOgMed = formaterJavaDate(vedtak.periode.fraOgMed);
    const tilOgMed = formaterJavaDate(vedtak.periode.tilOgMed);
    const anvistUtbetaling = formaterJavaDate(vedtak.anvistUtbetaling);

    return (
        <li>
            <dl>
                <dt>Fra og med</dt>
                <dd>{fraOgMed}</dd>
                <dt>Til og Med</dt>
                <dd>{tilOgMed}</dd>
                <dt>Brutto beløp</dt>
                <dd>{vedtak.bruttoBelop}</dd>
                <dt>Anvist utbetaling</dt>
                <dd>{anvistUtbetaling}</dd>
            </dl>
        </li>
    );
};

const PleiepengerUtbetalingerPanel = ({perioder}) => {
    const perioderKomponenter = perioder.map((periode, index) =>
        (<Periode key={index} periode={periode} periodeNummer={index+1}/>));

    return (
        <div>
            {perioderKomponenter}
        </div>
    );
};

PleiepengerUtbetalingerPanel.propTypes = {
};

export default PleiepengerUtbetalingerPanel;
