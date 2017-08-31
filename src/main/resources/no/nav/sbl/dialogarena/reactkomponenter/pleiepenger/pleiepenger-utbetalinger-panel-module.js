import React from 'react';
import moment from 'moment';
import DLElement from './dlelement';
import { formaterBelop } from './formatering-utils';

const formaterJavaDate = (dato) =>
    moment(new Date(dato.year, dato.monthValue - 1, dato.dayOfMonth)).format('DD.MM.YYYY');

const Periode = ({periode, periodeNummer, tekst}) => {
    const fraOgMed = formaterJavaDate(periode.fraOgMed);
    const vedtak = periode.vedtakListe.map((vedtak, index) => (<Vedtak key={index} tekst={tekst} vedtak={vedtak}/>));

    return (
        <section className="periode">
            <h2>{tekst.periode} {periodeNummer} {fraOgMed} </h2>
            <div className="periodeinfo">
                <dl className="pleiepenger-detaljer">
                    <DLElement etikett={tekst.pleiepengegrad} className="halvbredde">
                        {periode.graderingsgrad} %
                    </DLElement>
                    <DLElement etikett={tekst.pleiepengedager} className="halvbredde">
                        {periode.antallPleiepengedager}
                    </DLElement>
                </dl>
            </div>
            <article className="utbetalinger">
                <h3 className="utbetalinger-header" aria-label="Ekspanderingsliste">{tekst.kommendeUtbetalinger}</h3>
                <ul className="vedtaksliste">
                    {vedtak}
                </ul>
            </article>
        </section>
    )
};

const Vedtak = ({vedtak, tekst}) => {
    const fraOgMed = formaterJavaDate(vedtak.periode.fraOgMed);
    const tilOgMed = formaterJavaDate(vedtak.periode.tilOgMed);
    const anvistUtbetaling = formaterJavaDate(vedtak.anvistUtbetaling);
    return (
        <li>
            <dl className="pleiepenger-detaljer">
                <DLElement etikett={tekst.fraOgMedDato} className="halvbredde">
                    {fraOgMed}
                </DLElement>
                <DLElement etikett={tekst.tilOgMedDato} className="halvbredde">
                    {tilOgMed}
                </DLElement>
                <DLElement etikett={tekst.bruttoBelop} className="halvbredde">
                    {formaterBelop(vedtak.bruttoBelop)}
                </DLElement>
                <DLElement etikett={tekst.anvistUtbetaling} className="halvbredde">
                    {anvistUtbetaling}
                </DLElement>
                <DLElement etikett={tekst.dagsats} className="halvbredde">
                    {formaterBelop(vedtak.dagsats)}
                </DLElement>
            </dl>
        </li>
    );
};

const PleiepengerUtbetalingerPanel = ({perioder, tekst}) => {
    const perioderKomponenter = perioder.map((periode, index) =>
        (<Periode key={index} tekst={tekst} periode={periode} periodeNummer={index+1}/>));

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
