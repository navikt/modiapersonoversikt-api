package no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.info;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.Endringsinformasjon;

import java.io.Serializable;

public class Bankkonto implements Serializable {

    private String kontonummer;
    private String banknavn;
    private Endringsinformasjon endringsinformasjon;

    public String getKontonummer() {
        return kontonummer;
    }

    public void setKontonummer(String kontonummer) {
        this.kontonummer = kontonummer;
    }

    public String getBanknavn() {
        return banknavn;
    }

    public void setBanknavn(String banknavn) {
        this.banknavn = banknavn;
    }

    public Endringsinformasjon getEndringsinformasjon() {
        return endringsinformasjon;
    }

    public void setEndringsinformasjon(Endringsinformasjon endringsinformasjon) {
        this.endringsinformasjon = endringsinformasjon;
    }
}
