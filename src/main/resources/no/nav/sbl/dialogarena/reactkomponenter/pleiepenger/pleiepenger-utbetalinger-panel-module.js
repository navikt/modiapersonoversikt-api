import React from 'react';
import moment from 'moment';
import DLElement from './dlelement';

const formaterJavaDate = (dato) =>
    moment(new Date(dato.year, dato.monthValue - 1, dato.dayOfMonth)).format('DD.MM.YYYY');

const Periode = ({periode, periodeNummer}) => {
    const fraOgMed = formaterJavaDate(periode.fraOgMed);
    const vedtak = periode.vedtakListe.map((vedtak, index) => (<Vedtak key={index} vedtak={vedtak}/>));

    return (
        <section className="periode">
            <h2>Periode {periodeNummer} {fraOgMed} </h2>
            <div className="periodeinfo">
                <dl className="pleiepenger-detaljer">
                    <DLElement etikett={"Pleiepengergrad"} className="halvbredde">
                        {periode.graderingsgrad} %
                    </DLElement>
                    <DLElement etikett={"Pleiepengerdager"} className="halvbredde">
                        {periode.antallPleiepengedager} %
                    </DLElement>
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
            <dl className="pleiepenger-detaljer">
                <DLElement etikett={"Fra og med"} className="halvbredde">
                    {fraOgMed}
                </DLElement>
                <DLElement etikett={"Til og Med"} className="halvbredde">
                    {tilOgMed}
                </DLElement>
                <DLElement etikett={"Brutto beløp"} className="halvbredde">
                    {vedtak.bruttoBelop}
                </DLElement>
                <DLElement etikett={"Anvist utbetaling"} className="halvbredde">
                    {anvistUtbetaling}
                </DLElement>
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
    perioder: React.PropTypes.arrayOf(React.PropTypes.shape({
        antallPleiepengedager: React.PropTypes.number.isRequired,
        graderingsgrad: React.PropTypes.number,
        vedtakListe: React.PropTypes.arrayOf(React.PropTypes.shape({
            periode: React.PropTypes.shape({
                fraOgMed: React.PropTypes.object.isRequired,
                tilOgMed: React.PropTypes.object.isRequired
            }).isRequired,
            anvistUtbetaling: React.PropTypes.object.isRequired,
            bruttoBelop: React.PropTypes.number.isRequired
        })).isRequired
    })).isRequired
};

export default PleiepengerUtbetalingerPanel;
