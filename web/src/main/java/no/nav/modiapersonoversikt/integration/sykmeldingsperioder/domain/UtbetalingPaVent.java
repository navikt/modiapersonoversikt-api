package no.nav.modiapersonoversikt.integration.sykmeldingsperioder.domain;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Periode;

public class UtbetalingPaVent extends Utbetaling {

    private Kodeverkstype oppgjoerstype;
    private Kodeverkstype arbeidskategori;
    private Kodeverkstype stansaarsak;

    private Periode ferie1;
    private Periode ferie2;
    private Periode sanksjon;
    private Periode sykmeldt;

    public UtbetalingPaVent() {
    }

    public Kodeverkstype getOppgjoerstype() {
        return oppgjoerstype;
    }

    public void setOppgjoerstype(Kodeverkstype oppgjoerstype) {
        this.oppgjoerstype = oppgjoerstype;
    }

    public Kodeverkstype getArbeidskategori() {
        return arbeidskategori;
    }

    public void setArbeidskategori(Kodeverkstype arbeidskategori) {
        this.arbeidskategori = arbeidskategori;
    }

    public Kodeverkstype getStansaarsak() {
        return stansaarsak;
    }

    public void setStansaarsak(Kodeverkstype stansaarsak) {
        this.stansaarsak = stansaarsak;
    }

    public Periode getFerie1() {
        return ferie1;
    }

    public void setFerie1(Periode ferie1) {
        this.ferie1 = ferie1;
    }

    public Periode getFerie2() {
        return ferie2;
    }

    public void setFerie2(Periode ferie2) {
        this.ferie2 = ferie2;
    }

    public Periode getSanksjon() {
        return sanksjon;
    }

    public void setSanksjon(Periode sanksjon) {
        this.sanksjon = sanksjon;
    }

    public Periode getSykmeldt() {
        return sykmeldt;
    }

    public void setSykmeldt(Periode sykmeldt) {
        this.sykmeldt = sykmeldt;
    }
}