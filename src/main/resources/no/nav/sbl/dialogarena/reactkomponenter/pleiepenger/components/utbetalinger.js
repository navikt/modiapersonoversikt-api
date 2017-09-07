import React from 'react';
import DLElement from '../dlelement';
import { formaterJavaDate, formaterBelop, formaterOptionalProsentVerdi,
    formaterJavaDateTilMoment } from '../formatering-utils';

export const Periode = ({ periode, periodeNummer, tekst }) => {
    const fraOgMed = formaterJavaDate(periode.fraOgMed);
    const vedtakKomponent = periode.vedtakListe.map((vedtak, index) =>
        (<Vedtak key={index} tekst={tekst} vedtak={vedtak} />));

    return (
        <section className="periode">
            <h2>{tekst.periode} { periodeNummer } {fraOgMed} </h2>
            <div className="periode-innhold">
                <div className="periodeinfo">
                    <dl className="pleiepenger-detaljer">
                        <DLElement etikett={tekst.pleiepengegrad} className="halvbredde">
                            {formaterOptionalProsentVerdi(periode.graderingsgrad)}
                        </DLElement>
                        <DLElement etikett={tekst.pleiepengedager} className="halvbredde">
                            {periode.antallPleiepengedager}
                        </DLElement>
                    </dl>
                </div>
                <article className="utbetalinger">
                    <h3
                        className="utbetalinger-header"
                        aria-label="Ekspanderingsliste"
                    >
                        {tekst.kommendeUtbetalinger}
                    </h3>
                    <ul className="vedtaksliste">
                        {vedtakKomponent}
                    </ul>
                </article>
            </div>
        </section>
    );
};

Periode.propTypes = {
    periode: React.PropTypes.shape({
        antallPleiepengedager: React.PropTypes.number.isRequired,
        graderingsgrad: React.PropTypes.number,
        vedtakListe: React.PropTypes.arrayOf(React.PropTypes.object).isRequired
    }).isRequired,
    periodeNummer: React.PropTypes.number.isRequired,
    tekst: React.PropTypes.object.isRequired
};

const Vedtak = ({ vedtak, tekst }) => {
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
                <DLElement etikett={tekst.kompensasjonsgrad} className="halvbredde">
                    {formaterOptionalProsentVerdi(vedtak.kompensasjonsgrad)}
                </DLElement>
            </dl>
        </li>
    );
};

Vedtak.propTypes = {
    vedtak: React.PropTypes.shape({
        periode: React.PropTypes.shape({
            fraOgMed: React.PropTypes.object.isRequired,
            tilOgMed: React.PropTypes.object.isRequired
        }).isRequired,
        anvistUtbetaling: React.PropTypes.object.isRequired,
        bruttoBelop: React.PropTypes.number.isRequired,
        kompensasjonsgrad: React.PropTypes.number
    }),
    tekst: React.PropTypes.object.isRequired
};

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
