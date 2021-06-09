package no.nav.kjerneinfo.domain.person;

import no.nav.kjerneinfo.common.domain.Periode;
import no.nav.kjerneinfo.domain.person.predicate.AdresseUtils;
import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class Adresse extends Adresselinje implements Tilleggsadresse {

    private String gatenavn;
    private String gatenummer;
    private String bolignummer;
    private String postnummer;
    private String poststed;
    private String tilleggsadresse;
    private String tilleggsadresseType;
    private String husbokstav;
    private Periode postleveringsPeriode;

    @Override
    public String getAdresselinje() {
        if (isBlank(getGateadresseLinje()) || isBlank(getPostadresseLinje())) {
            return AdresseUtils.append(getGateadresseLinje(), getPostadresseLinje());
        }
        return AdresseUtils.append(getGateadresseLinje(), ", ", getPostadresseLinje());
    }

    @Override
    public String toString() {
        return "Adresse [gatenavn=" + gatenavn + ", postnummer=" + getPostnummer()
                + ", poststed=" + poststed + "]";
    }

    public String getGatenavn() {
        return gatenavn;
    }

    public void setGatenavn(String gatenavn) {
        this.gatenavn = gatenavn;
    }

    public String getPostnummer() {
        return postnummer;
    }

    public void setPostnummer(String postnummer) {
        this.postnummer = postnummer;
    }

    public String getPoststednavn() {
        return poststed;
    }

    public void setPoststednavn(String poststed) {
        this.poststed = poststed;
    }

    public String getGatenummer() {
        return gatenummer;
    }

    public void setGatenummer(String gatenummer) {
        this.gatenummer = gatenummer;
    }

    public String getBolignummer() {
        return bolignummer;
    }

    public void setBolignummer(String bolignummer) {
        this.bolignummer = bolignummer;
    }

    public void setTilleggsadresse(String tilleggsadresse) {
        this.tilleggsadresse = tilleggsadresse;
    }

    @Override
    public String getTilleggsadresseType() {
        return tilleggsadresseType;
    }

    @Override
    public String getTilleggsadresse() {
        return tilleggsadresse;
    }

    public void setTilleggsadresseType(String tilleggsadresseType) {
        this.tilleggsadresseType = tilleggsadresseType;
    }

    public String getGateadresseLinje() {
        String adresseLinje = gatenavn == null ? "" : gatenavn;
        adresseLinje += gatenummer == null ? "" : " " + gatenummer;
        adresseLinje += husbokstav == null ? "" : husbokstav;
        adresseLinje += bolignummer == null ? "" : " " + bolignummer;
        return adresseLinje;
    }

    public String getPostadresseLinje() {
        String adresseLinje = this.getPostnummer() == null ? "" : getPostnummer() + " ";
        adresseLinje += getPoststednavn() == null ? "" : getPoststednavn();
        return adresseLinje;
    }

    public String getHusbokstav() {
        return husbokstav;
    }

    public void setHusbokstav(String husbokstav) {
        this.husbokstav = husbokstav;
    }

    public void setPostleveringsPeriode(Periode postleveringsPeriode) {
        this.postleveringsPeriode = postleveringsPeriode;
    }

    public Periode getPostleveringsPeriode() {
        return postleveringsPeriode;
    }

    @Override
    public String getTilleggsadresseMedType() {
        if (StringUtils.isBlank(tilleggsadresse)) {
            return StringUtils.EMPTY;
        } else if (StringUtils.isBlank(tilleggsadresseType)) {
            return tilleggsadresse;
        } else if (TILLEGGSADRESSETYPE_OFFISIELL_ADRESSE.equalsIgnoreCase(tilleggsadresseType)) {
            return tilleggsadresse;
        }
        return tilleggsadresseType + " " + tilleggsadresse;
    }
}
