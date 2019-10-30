package no.nav.brukerprofil.domain;

import no.nav.brukerprofil.domain.adresser.StrukturertAdresse;
import no.nav.brukerprofil.domain.adresser.UstrukturertAdresse;
import no.nav.kjerneinfo.common.domain.Kodeverdi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class Bruker implements Serializable {

    private String ident;
    private StrukturertAdresse bostedsadresse;
    private UstrukturertAdresse postadresse;
    private StrukturertAdresse midlertidigadresseNorge;
    private UstrukturertAdresse midlertidigadresseUtland;
    private Kodeverdi gjeldendePostadresseType;
    private Bankkonto bankkonto;
    private Telefon mobil;
    private Telefon hjemTlf;
    private Telefon jobbTlf;
    private String poststedsnavn;
    private List<Kodeverdi> tilrettelagtKommunikasjon;

    private Navn fornavn;
    private Navn mellomnavn;
    private Navn etternavn;
    private Epost dkifEpost;
    private Telefon dkifMobiltelefon;
    private String dkifReservasjon;

    public Navn getFornavn() {
        return isNull(fornavn) ? new Navn("") : fornavn;
    }

    public void setFornavn(Navn fornavn) {
        this.fornavn = isNull(fornavn) ? new Navn("") : fornavn;
    }

    public Navn getMellomnavn() {
        return isNull(mellomnavn) ? new Navn("") : mellomnavn;
    }

    public void setMellomnavn(Navn mellomnavn) {
        this.mellomnavn = isNull(mellomnavn) ? new Navn("") : mellomnavn;
    }

    public Navn getEtternavn() {
        return isNull(etternavn) ? new Navn("") : etternavn;
    }

    public void setEtternavn(Navn etternavn) {
        this.etternavn = isNull(etternavn) ? new Navn("") : etternavn;
    }

    public String getPoststedsnavn() {
        return poststedsnavn;
    }

    public void setPoststedsnavn(String poststedsnavn) {
        this.poststedsnavn = poststedsnavn;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public StrukturertAdresse getBostedsadresse() {
        return bostedsadresse;
    }

    public void setBostedsadresse(StrukturertAdresse bostedsadresse) {
        this.bostedsadresse = bostedsadresse;
    }

    public UstrukturertAdresse getPostadresse() {
        return postadresse;
    }

    public void setPostadresse(UstrukturertAdresse postadresse) {
        this.postadresse = postadresse;
    }

    public StrukturertAdresse getMidlertidigadresseNorge() {
        return midlertidigadresseNorge;
    }

    public void setMidlertidigadresseNorge(StrukturertAdresse midlertidigadresseNorge) {
        this.midlertidigadresseNorge = midlertidigadresseNorge;
    }

    public UstrukturertAdresse getMidlertidigadresseUtland() {
        return midlertidigadresseUtland;
    }

    public void setMidlertidigadresseUtland(UstrukturertAdresse midlertidigadresseUtland) {
        this.midlertidigadresseUtland = midlertidigadresseUtland;
    }

    public Kodeverdi getGjeldendePostadresseType() {
        return gjeldendePostadresseType;
    }

    public void setGjeldendePostadresseType(Kodeverdi gjeldendePostadresseType) {
        this.gjeldendePostadresseType = gjeldendePostadresseType;
    }

    public Bankkonto getBankkonto() {
        return bankkonto;
    }

    public void setBankkonto(Bankkonto bankkonto) {
        this.bankkonto = bankkonto;
    }

    public Telefon getMobil() {
        return mobil;
    }

    public void setMobil(Telefon mobil) {
        this.mobil = mobil;
    }

    public Telefon getHjemTlf() {
        return hjemTlf;
    }

    public void setHjemTlf(Telefon hjemTlf) {
        this.hjemTlf = hjemTlf;
    }

    public Telefon getJobbTlf() {
        return jobbTlf;
    }

    public void setJobbTlf(Telefon jobbTlf) {
        this.jobbTlf = jobbTlf;
    }

    public Epost getDkifEpost() {
        return dkifEpost;
    }

    public void setDkifEpost(Epost dkifEpost) {
        this.dkifEpost = dkifEpost;
    }

    public Telefon getDkifMobiltelefon() {
        return dkifMobiltelefon;
    }

    public void setDkifMobiltelefon(Telefon dkifMobiltelefon) {
        this.dkifMobiltelefon = dkifMobiltelefon;
    }

    public String getDkifReservasjon() {
        return dkifReservasjon;
    }

    public void setDkifReservasjon(String dkifReservasjon) {
        this.dkifReservasjon = dkifReservasjon;
    }

    public boolean erReservert() {
        return isNotBlank(dkifReservasjon) && "true".equals(dkifReservasjon);
    }

    public String hentReservasjon() {
        return dkifReservasjon;
    }

    public List<Kodeverdi> getTilrettelagtKommunikasjon() {
        if (tilrettelagtKommunikasjon == null) {
            return new ArrayList<>();
        }
        return tilrettelagtKommunikasjon;
    }

    public void setTilrettelagtKommunikasjon(List<Kodeverdi> tilrettelagtKommunikasjon) {
        this.tilrettelagtKommunikasjon = tilrettelagtKommunikasjon;
    }
}
