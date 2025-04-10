package no.nav.modiapersonoversikt.consumer.infotrygd.domain;


import no.nav.modiapersonoversikt.commondomain.Periode;

import java.io.Serializable;

public class Utbetaling implements Serializable {
    private Periode vedtak;
    private Double utbetalingsgrad;

    public Double getUtbetalingsgrad() {
        return utbetalingsgrad;
    }

    public void setUtbetalingsgrad(Double utbetalingsgrad) {
        this.utbetalingsgrad = utbetalingsgrad;
    }

    public Utbetaling withUtbetalingsgrad(double utbetalingsgrad) {
        setUtbetalingsgrad(utbetalingsgrad);
        return this;
    }

    public Periode getVedtak() {
        return vedtak;
    }

    public void setVedtak(Periode vedtak) {
        this.vedtak = vedtak;
    }

    public Utbetaling withVedtak(Periode periode) {
        setVedtak(periode);
        return this;
    }
}
