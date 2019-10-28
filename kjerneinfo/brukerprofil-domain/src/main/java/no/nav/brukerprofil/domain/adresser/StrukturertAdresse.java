package no.nav.brukerprofil.domain.adresser;

import no.nav.kjerneinfo.common.domain.Periode;
import org.joda.time.LocalDateTime;

import java.io.Serializable;

public class StrukturertAdresse implements Serializable {

    private String landkode;
    private String tilleggsadresseType;
    private String tilleggsadresse;
    private Periode postleveringsPeriode;
    private LocalDateTime endringstidspunkt;
    private String endretAv;
    private String poststedsnavn;
	private String poststed;

    public String getTilleggsadresse() {
        return tilleggsadresse;
    }

    public void setTilleggsadresse(String tilleggsadresse) {
        if (tilleggsadresse != null) {
            this.tilleggsadresse = fjernCOPrefix(tilleggsadresse);
        } else {
            this.tilleggsadresse = tilleggsadresse;
        }
    }

    public String getPoststedsnavn() {
        return poststedsnavn;
    }

    public void setPoststedsnavn(String poststedsnavn) {
        this.poststedsnavn = poststedsnavn;
    }

    public void setLandkode(String landkode) {
        this.landkode = landkode;
    }

    public String getLandkode() {
        return landkode;
    }

    public void setTilleggsadresseType(String tilleggsadresseType) {
        this.tilleggsadresseType = tilleggsadresseType;
    }

    public String getTilleggsadresseType() {
        return tilleggsadresseType;
    }

    public Periode getPostleveringsPeriode() {
        return postleveringsPeriode;
    }

    public void setPostleveringsPeriode(Periode value) {
        this.postleveringsPeriode = value;
    }

    public LocalDateTime getEndringstidspunkt() {
        return endringstidspunkt;
    }

    public void setEndringstidspunkt(LocalDateTime value) {
        this.endringstidspunkt = value;
    }

    public String getEndretAv() {
        return endretAv;
    }

    public void setEndretAv(String value) {
        this.endretAv = value;
    }

	public String getPoststed() {
		return poststed;
	}

	public void setPoststed(String poststed) {
		this.poststed = poststed;
	}

    private String fjernCOPrefix(String tilleggsadresse) {
        String streng = tilleggsadresse.toLowerCase().replace(" ", "");
        if (streng.startsWith("c/o")) {
            int coEndIndex = tilleggsadresse.toLowerCase().indexOf('o');
            return tilleggsadresse.substring(coEndIndex + 1).trim();
        } else {
            return tilleggsadresse;
        }
    }
}
