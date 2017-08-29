import React from 'react';
import moment from 'moment';

const javaLocalDateTimeToJSDate = (dato) =>
    new Date(dato.year, dato.monthValue - 1, dato.dayOfMonth);

class PleiepengerUtbetalingerPanel extends React.Component {
    constructor(props) {
        super(props);
        console.log(props);
        this.state = {
            saker: []
        };

    }

    render() {

        const lagVedtak = (vedtak, index) => {
            const fraOgMed = moment(javaLocalDateTimeToJSDate(vedtak.periode.fraOgMed)).format('DD.MM.YYYY');
            const tilOgMed = moment(javaLocalDateTimeToJSDate(vedtak.periode.tilOgMed)).format('DD.MM.YYYY');

            return (
                <li key={index}>
                    <dl>
                        <dt>Fra og med</dt>
                        <dd>{fraOgMed}</dd>
                        <dt>Til og Med</dt>
                        <dd>{tilOgMed}</dd>
                        <dt>Brutto beløp</dt>
                        <dd>6 777 kr</dd>
                        <dt>Anvist utbetaling</dt>
                        <dd>17.08.2017</dd>
                    </dl>
                </li>)
        };

        const lagPerioder = (periode, index) => {
            const fraOgMed = moment(javaLocalDateTimeToJSDate(periode.fraOgMed)).format('DD.MM.YYYY');
            const vedtak = periode.vedtakListe.map(lagVedtak);
            return (
                <section key={index}>
                    <h1>Periode {index + 1} {fraOgMed} </h1>
                    <article className="kommende-utbetalinger liste">
                        <h1 aria-label="Ekspanderingsliste">Kommende utbetalinger</h1>
                        <ul>
                            {vedtak}
                        </ul>
                    </article>
                </section>
            )
        };

        const perioder = this.props.perioder.map(lagPerioder);
        return (
            <div>
                {perioder}
            </div>
        );
    }
}

PleiepengerUtbetalingerPanel.propTypes = {
};

export default PleiepengerUtbetalingerPanel;
