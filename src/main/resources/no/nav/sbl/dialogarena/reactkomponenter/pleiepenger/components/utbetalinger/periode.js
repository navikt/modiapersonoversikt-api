import React from 'react';
import DLElement from '../dlelement';
import Vedtak from './vedtak.js';
import { formaterJavaDate, konverterTilMomentDato, formaterOptionalProsentVerdi } from '../../utils';

export const sorterVedtak = vedtakListe => (
    vedtakListe.sort((a, b) => (
        konverterTilMomentDato(b.periode.tilOgMed).diff(konverterTilMomentDato(a.periode.tilOgMed))
    ))
);

const getSorterteVedtak = (vedtaksListe, tekst) => {
    return sorterVedtak(vedtaksListe).map((vedtak, index) =>
        (<Vedtak key={index} tekst={tekst} vedtak={vedtak} />));
};

const Periode = ({ periode, periodeNummer, tekst }) => {
    const vedtakKomponent = getSorterteVedtak(periode.vedtakListe, tekst);
    const fraOgMed = formaterJavaDate(periode.fraOgMed);
    return (
        <section className="periode">
            <h2>{tekst.periode} { periodeNummer } {fraOgMed} </h2>
            <div className="periode-innhold">
                <div className="periodeinfo">
                    <dl className="pleiepenger-detaljer">
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
        vedtakListe: React.PropTypes.arrayOf(React.PropTypes.object).isRequired
    }).isRequired,
    periodeNummer: React.PropTypes.number.isRequired,
    tekst: React.PropTypes.object.isRequired
};

export default Periode;
