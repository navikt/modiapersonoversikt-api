package no.nav.modiapersonoversikt.integration.sykmeldingsperioder.domain.foreldrepenger;

import org.joda.time.LocalDate;

public class Adopsjon extends Foreldrepengerettighet {

    private LocalDate omsorgsovertakelse;

    public Adopsjon() {
    }

    public Adopsjon(LocalDate omsorgsovertakelse) {
        this.omsorgsovertakelse = omsorgsovertakelse;
    }

    public LocalDate getOmsorgsovertakelse() {
        return omsorgsovertakelse;
    }

    public void setOmsorgsovertakelse(LocalDate value) {
        this.omsorgsovertakelse = value;
    }
}
