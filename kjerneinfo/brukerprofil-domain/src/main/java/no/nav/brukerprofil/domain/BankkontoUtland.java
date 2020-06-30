package no.nav.brukerprofil.domain;

import no.nav.brukerprofil.domain.adresser.UstrukturertAdresse;
import no.nav.kjerneinfo.common.domain.Kodeverdi;

public class BankkontoUtland extends Bankkonto {

    private String swift;
    private Kodeverdi landkode;
    private String bankkode;
    private UstrukturertAdresse bankadresse;
    private Kodeverdi valuta;

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
}
