package no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.info;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Kodeverdi;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.Endringsinformasjon;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.UstrukturertAdresse;

public class BankkontoUtland extends Bankkonto {
    private String swift;
    private Kodeverdi landkode;
    private String bankkode;
    private UstrukturertAdresse bankadresse;
    private Kodeverdi valuta;
    private Endringsinformasjon endringsinformasjon;

    public String getSwift() {
        return swift;
    }

    public void setSwift(String swift) {
        this.swift = swift;
    }

    public Kodeverdi getLandkode() {
        return landkode;
    }

    public void setLandkode(Kodeverdi landkode) {
        this.landkode = landkode;
    }

    public String getBankkode() {
        return bankkode;
    }

    public void setBankkode(String bankkode) {
        this.bankkode = bankkode;
    }

    public UstrukturertAdresse getBankadresse() {
        return bankadresse;
    }

    public void setBankadresse(UstrukturertAdresse bankadresse) {
        this.bankadresse = bankadresse;
    }

    public Kodeverdi getValuta() {
        return valuta;
    }

    public void setValuta(Kodeverdi valuta) {
        this.valuta = valuta;
    }

    public Endringsinformasjon getEndringsinformasjon() {
        return endringsinformasjon;
    }

    public void setEndringsinformasjon(Endringsinformasjon endringsinformasjon) {
        this.endringsinformasjon = endringsinformasjon;
    }
}
