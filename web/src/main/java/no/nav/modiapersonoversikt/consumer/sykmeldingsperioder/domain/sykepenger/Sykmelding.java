package no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.sykepenger;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Periode;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.List;

public class Sykmelding implements Serializable {
    private String sykmelder;
    private LocalDate behandlet;
    private Periode sykmeldt;
    private Double sykmeldingsgrad;
    private List<Gradering> gradAvSykmeldingListe;
    private Yrkesskade gjelderYrkesskade;

    public Sykmelding() {

    }

    public LocalDate getBehandlet() {
        return behandlet;
    }

    public void setBehandlet(LocalDate behandlet) {
        this.behandlet = behandlet;
    }

    public List<Gradering> getGradAvSykmeldingListe() {
        return gradAvSykmeldingListe;
    }

    public void setGradAvSykmeldingListe(List<Gradering> gradAvSykmeldingListe) {
        this.gradAvSykmeldingListe = gradAvSykmeldingListe;
    }

    public String getSykmelder() {
        return sykmelder;
    }

    public void setSykmelder(String sykmelder) {
        this.sykmelder = sykmelder;
    }

    public Double getSykmeldingsgrad() {
        return sykmeldingsgrad;
    }

    public void setSykmeldingsgrad(Double sykmeldingsgrad) {
        this.sykmeldingsgrad = sykmeldingsgrad;
    }

    public Periode getSykmeldt() {
        return sykmeldt;
    }

    public void setSykmeldt(Periode sykmeldt) {
        this.sykmeldt = sykmeldt;
    }

    public Yrkesskade getGjelderYrkesskade() {
        return gjelderYrkesskade;
    }

    public void setGjelderYrkesskade(Yrkesskade gjelderYrkesskade) {
        this.gjelderYrkesskade = gjelderYrkesskade;
    }
}
