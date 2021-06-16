package no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Periode;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.predicate.AdresseUtils;
import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class Matrikkeladresse extends Adresselinje implements Tilleggsadresse {

    private String poststed;
    private String postnummer;
    private String eiendomsnavn;
    private String gaardsnummer;
    private String bruksnummer;
    private String festenummer;
    private String seksjonsnummer;
    private String undernummer;
    private String tilleggsadresse;
    private String tilleggsadressetype;
    private Periode postleveringsPeriode;

    public String getEiendomsnavn() {
        return eiendomsnavn;
    }

    public void setEiendomsnavn(String eiendomsnavn) {
        this.eiendomsnavn = eiendomsnavn;
    }

    public String getPostnummer() {
        return postnummer;
    }

    public void setPostnummer(String postnummer) {
        this.postnummer = postnummer;
    }

    public String getPoststed() {
        return poststed;
    }

    public void setPoststed(String poststed) {
        this.poststed = poststed;
    }

    public String getGaardsnummer() {
        return this.gaardsnummer;
    }

    public void setGaardsnummer(String gaardsnummer) {
        this.gaardsnummer = gaardsnummer;
    }

    public String getBruksnummer() {
        return this.bruksnummer;
    }

    public void setBruksnummer(String bruksnummer) {
        this.bruksnummer = bruksnummer;
    }

    public String getFestenummer() {
        return this.festenummer;
    }

    public void setFestenummer(String festenummer) {
        this.festenummer = festenummer;
    }

    public String getSeksjonsnummer() {
        return this.seksjonsnummer;
    }

    public void setSeksjonsnummer(String seksjonsnummer) {
        this.seksjonsnummer = seksjonsnummer;
    }

    public String getUndernummer() {
        return this.undernummer;
    }

    public void setUndernummer(String undernummer) {
        this.undernummer = undernummer;
    }

    @Override
    public String getTilleggsadresseMedType() {
        if (StringUtils.isBlank(tilleggsadresse)) {
            return StringUtils.EMPTY;
        } else if (StringUtils.isBlank(tilleggsadressetype)) {
            return tilleggsadresse;
        } else if (TILLEGGSADRESSETYPE_MATRIKKELADRESSE.equalsIgnoreCase(tilleggsadressetype)
                || TILLEGGSADRESSETYPE_OFFISIELL_ADRESSE.equalsIgnoreCase(tilleggsadressetype)) {
            return tilleggsadresse;
        }
        return tilleggsadressetype + " " + tilleggsadresse;
    }

    @Override
    public String getTilleggsadresseType() {
        return tilleggsadressetype;
    }

    @Override
    public String getTilleggsadresse() {
        return tilleggsadresse;
    }

    public void setTilleggsadresse(String tilleggsadresse) {
        this.tilleggsadresse = tilleggsadresse;
    }

    public void setTilleggsadressetype(String tilleggsadressetype) {
        this.tilleggsadressetype = tilleggsadressetype;
    }

    public void setPostleveringsPeriode(Periode postleveringsPeriode) {
        this.postleveringsPeriode = postleveringsPeriode;
    }

    public Periode getPostleveringsPeriode() {
        return postleveringsPeriode;
    }

    public Matrikkeladresse withEiendomsnavn(String eiendomsnavn) {
        setEiendomsnavn(eiendomsnavn);
        return this;
    }

    public Matrikkeladresse withPostnummer(String postnummer) {
        setPostnummer(postnummer);
        return this;
    }

    public Matrikkeladresse withPoststed(String poststed) {
        setPoststed(poststed);
        return this;
    }

    public Matrikkeladresse withTilleggsadresse(String tilleggsadresse) {
        setTilleggsadresse(tilleggsadresse);
        return this;
    }

    public Matrikkeladresse withTilleggsadressetype(String tilleggsadressetype) {
        setTilleggsadressetype(tilleggsadressetype);
        return this;
    }

    public Matrikkeladresse withPostleveringsPeriode(Periode postleveringsPeriode) {
        setPostleveringsPeriode(postleveringsPeriode);
        return this;
    }

    public Matrikkeladresse withGaardsnummer(String gaardsnummer) {
        setGaardsnummer(gaardsnummer);
        return this;
    }

    public Matrikkeladresse withBruksnummer(String bruksnummer) {
        setBruksnummer(bruksnummer);
        return this;
    }

    public Matrikkeladresse withFestenummer(String festenummer) {
        setFestenummer(festenummer);
        return this;
    }

    public Matrikkeladresse withSeksjonsnummer(String seksjonsnummer) {
        setSeksjonsnummer(seksjonsnummer);
        return this;
    }

    public Matrikkeladresse withUndernummer(String undernummer) {
        setUndernummer(undernummer);
        return this;
    }

    @Override
    public String getAdresselinje() {
        if (isBlank(getPostnummer()) && isBlank(getPoststed())) {
            return eiendomsnavn;
        }

        return AdresseUtils.seperatorAppend(", ",
                eiendomsnavn,
                AdresseUtils.spaceAppend(
                        postnummer,
                        poststed
                )
        );
    }

    public String getMatrikkeladresse() {
        return AdresseUtils.seperatorAppend("\n",
                AdresseUtils.spaceAppend("Gårdsnr:", gaardsnummer),
                isBlank(bruksnummer) ? null : AdresseUtils.spaceAppend("Bruksnr:", bruksnummer),
                isBlank(festenummer) ? null : AdresseUtils.spaceAppend("Festenr:", festenummer),
                isBlank(seksjonsnummer) ? null : AdresseUtils.spaceAppend("Seksjonsnr:", seksjonsnummer),
                isBlank(undernummer) ? null : AdresseUtils.spaceAppend("Undernr:", undernummer)
        );
    }

}
