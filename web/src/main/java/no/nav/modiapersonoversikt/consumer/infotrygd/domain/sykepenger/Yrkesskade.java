package no.nav.modiapersonoversikt.consumer.infotrygd.domain.sykepenger;

import no.nav.modiapersonoversikt.consumer.infotrygd.domain.Kodeverkstype;
import org.joda.time.LocalDate;

import java.io.Serializable;

public class Yrkesskade implements Serializable {
    private Kodeverkstype yrkesskadeart;
    private LocalDate skadet;
    private LocalDate vedtatt;

    public Yrkesskade() {
    }

    public Yrkesskade(LocalDate skadetdato, LocalDate vedtaksdato, Kodeverkstype yrkesskadeart) {
        this.skadet = skadetdato;
        this.vedtatt = vedtaksdato;
        this.yrkesskadeart = yrkesskadeart;
    }

    public LocalDate getSkadet() {
        return skadet;
    }

    public void setSkadet(LocalDate skadet) {
        this.skadet = skadet;
    }

    public LocalDate getVedtatt() {
        return vedtatt;
    }

    public void setVedtatt(LocalDate vedtatt) {
        this.vedtatt = vedtatt;
    }

    public Kodeverkstype getYrkesskadeart() {
        return yrkesskadeart;
    }

    public void setYrkesskadeart(Kodeverkstype yrkesskadeart) {
        this.yrkesskadeart = yrkesskadeart;
    }
}
