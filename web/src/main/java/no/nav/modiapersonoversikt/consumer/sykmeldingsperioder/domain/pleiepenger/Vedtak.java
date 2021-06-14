package no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.pleiepenger;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Vedtak implements Serializable {

    private Periode periode;
    private Integer kompensasjonsgrad;
    private int utbetalingsgrad;
    private LocalDate anvistUtbetaling;
    private BigDecimal bruttoBelop;
    private BigDecimal dagsats;
    private Integer pleiepengegrad;

    public Vedtak withPeriode(Periode peridoe) {
        this.periode = peridoe;
        return this;
    }

    public Periode getPeriode() {
        return periode;
    }

    public Integer getKompensasjonsgrad() {
        return kompensasjonsgrad;
    }

    public Vedtak withKompensasjonsgrad(Integer kompensasjonsgrad) {
        this.kompensasjonsgrad = kompensasjonsgrad;
        return this;
    }

    public int getUtbetalingsgrad() {
        return utbetalingsgrad;
    }

    public Vedtak withUtbetalingsgrad(int utbetalingsgrad) {
        this.utbetalingsgrad = utbetalingsgrad;
        return this;
    }

    public LocalDate getAnvistUtbetaling() {
        return anvistUtbetaling;
    }

    public Vedtak withAnvistUtbetaling(LocalDate anvistUtbetaling) {
        this.anvistUtbetaling = anvistUtbetaling;
        return this;
    }

    public BigDecimal getBruttoBelop() {
        return bruttoBelop;
    }

    public Vedtak withBruttoBelop(BigDecimal bruttoBelop) {
        this.bruttoBelop = bruttoBelop;
        return this;
    }

    public BigDecimal getDagsats() {
        return dagsats;
    }

    public Vedtak withDagsats(BigDecimal dagsats) {
        this.dagsats = dagsats;
        return this;
    }

    public Integer getPleiepengegrad() {
        return pleiepengegrad;
    }

    public Vedtak withPleiepengegrad(Integer pleiepengegrad) {
        this.pleiepengegrad = pleiepengegrad;
        return this;
    }

}
