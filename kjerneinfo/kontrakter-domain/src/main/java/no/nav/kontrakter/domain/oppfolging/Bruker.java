package no.nav.kontrakter.domain.oppfolging;

import org.joda.time.LocalDate;

import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class Bruker implements Serializable {
    private Boolean meldeplikt;
    private String formidlingsgruppe;
    private String innsatsgruppe;
    private String rettighetsgruppe;
    private LocalDate sykmeldtFrom;

    public Bruker() {
    }

    public String getFormidlingsgruppe() {
        return formidlingsgruppe;
    }

    public void setFormidlingsgruppe(String formidlingsgruppe) {
        this.formidlingsgruppe = formidlingsgruppe;
    }

    public String getInnsatsgruppe() {
        return innsatsgruppe;
    }

    public void setInnsatsgruppe(String innsatsgruppe) {
        this.innsatsgruppe = innsatsgruppe;
    }

    public Boolean getMeldeplikt() {
        return meldeplikt;
    }

    public void setMeldeplikt(Boolean meldeplikt) {
        this.meldeplikt = meldeplikt;
    }

    public LocalDate getSykmeldtFrom() {
        return sykmeldtFrom;
    }

    public void setSykmeldtFrom(LocalDate sykmeldtFrom) {
        this.sykmeldtFrom = sykmeldtFrom;
    }

    public String getRettighetsgruppe() {
        return rettighetsgruppe;
    }

    public void setRettighetsgruppe(String rettighetsgruppe) {
        this.rettighetsgruppe = rettighetsgruppe;
    }

    public boolean erTom() {
        return meldeplikt == null
                && isBlank(formidlingsgruppe)
                && isBlank(innsatsgruppe)
                && isBlank(rettighetsgruppe)
                && sykmeldtFrom != null;
    }
}
