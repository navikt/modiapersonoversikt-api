package no.nav.brukerprofil.domain;

import no.nav.kjerneinfo.common.domain.Kodeverdi;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;

import java.io.Serializable;

public class Telefon implements Serializable {

	private String identifikator;
	private Kodeverdi retningsnummer;
	private Kodeverdi type;
	private LocalDateTime endringstidspunkt;
	private String endretAv;

	public String getIdentifikator() {
		return identifikator;
	}

	public void setIdentifikator(String value) {
		this.identifikator = value;
	}

	public Kodeverdi getRetningsnummer() {
		return retningsnummer;
	}

	public void setRetningsnummer(Kodeverdi retningsnummer) {
		this.retningsnummer = retningsnummer;
	}

	public Kodeverdi getType() {
		return type;
	}

	public void setType(Kodeverdi type) {
		this.type = type;
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

	public Telefon withIdentifikator(String identifikator) {
		this.identifikator = identifikator;
		return this;
	}

	public Telefon withType(Kodeverdi type) {
		this.type = type;
		return this;
	}

	public Telefon withRetningsnummer(Kodeverdi retningsnummer) {
		this.retningsnummer = retningsnummer;
		return this;
	}

	public String getTelefonnummerMedRetningsnummer() {
		if (retningsnummer == null || StringUtils.isEmpty(retningsnummer.getKodeRef())) {
			return identifikator;
		}
		return retningsnummer.getKodeRef() + " " + identifikator;
	}

}
