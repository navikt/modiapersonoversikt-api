package no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain;

import org.joda.time.LocalDate;

import java.util.List;

public abstract class CommonFakta {

    private LocalDate slutt;
    private List<Arbeidsforhold> arbeidsforholdListe;
    private boolean erArbeidsgiverperiode;
    private Kodeverkstype arbeidskategori;

    public LocalDate getSlutt() {
        return slutt;
    }

    public void setSlutt(LocalDate slutt) {
        this.slutt = slutt;
    }

    public List<Arbeidsforhold> getArbeidsforholdListe() {
        return arbeidsforholdListe;
    }

    public void setArbeidsforholdListe(List<Arbeidsforhold> arbeidsforholdListe) {
        this.arbeidsforholdListe = arbeidsforholdListe;
    }

    public Kodeverkstype getArbeidskategori() {
        return arbeidskategori;
    }

    public void setArbeidskategori(Kodeverkstype arbeidskategori) {
        this.arbeidskategori = arbeidskategori;
    }

    public boolean getErArbeidsgiverperiode() {
        return erArbeidsgiverperiode;
    }

    public void setErArbeidsgiverperiode(boolean erArbeidsgiverperiode) {
        this.erArbeidsgiverperiode = erArbeidsgiverperiode;
    }
}
