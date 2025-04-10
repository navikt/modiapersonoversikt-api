package no.nav.modiapersonoversikt.arena.oppfolgingskontrakt;

import org.joda.time.LocalDate;

import java.io.Serializable;


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
}
