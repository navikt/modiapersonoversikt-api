package no.nav.kjerneinfo.domain.person;

import no.nav.kjerneinfo.common.domain.Periode;
import no.nav.kjerneinfo.domain.person.predicate.AdresseUtils;
import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class Postboksadresse extends Adresselinje implements Tilleggsadresse {

    private String postboksnummer;
    private String postboksanlegg;
    private String poststed;
    private String poststednavn;
    private Periode postleveringsPeriode;
    private String tilleggsadresse;
    private String tilleggsadresseType;

    public String getPostboksnummer() {
        return postboksnummer;
    }

    public void setPostboksnummer(String postboksnummer) {
        this.postboksnummer = postboksnummer;
    }

    public String getPostboksanlegg() {
        return postboksanlegg;
    }

    public void setPostboksanlegg(String postboksanlegg) {
        this.postboksanlegg = postboksanlegg;
    }

    public String getPoststed() {
        return poststed;
    }

    public void setPoststed(String poststed) {
        this.poststed = poststed;
    }

    public String getPoststednavn() {
        return poststednavn;
    }

    public void setPoststednavn(String poststednavn) {
        this.poststednavn = poststednavn;
    }

    public Periode getPostleveringsPeriode() {
        return postleveringsPeriode;
    }

    public void setPostleveringsPeriode(Periode periode) {
        this.postleveringsPeriode = periode;
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

    @Override
    public String getAdresselinje() {
        if (isBlank(getPoststed()) && isBlank(getPoststednavn())) {
            return AdresseUtils.spaceAppend(getPostboksanlegg(), "Postboks ", getPostboksnummer(), getPoststed(), getPoststednavn());
        }
        return AdresseUtils.spaceAppend(getPostboksanlegg(), "Postboks ", getPostboksnummer() + ", ", getPoststed(), getPoststednavn());
    }
}
