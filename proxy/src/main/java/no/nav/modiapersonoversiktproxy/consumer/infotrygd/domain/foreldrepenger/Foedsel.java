package no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.foreldrepenger;

import org.joda.time.LocalDate;

public class Foedsel extends Foreldrepengerettighet {

    private LocalDate termin;

    public Foedsel() {
    }

    public Foedsel(LocalDate termindato) {
        this.termin = termindato;
    }

    public LocalDate getTermin() {
        return termin;
    }

    public void setTermin(LocalDate value) {
        this.termin = value;
    }
}
