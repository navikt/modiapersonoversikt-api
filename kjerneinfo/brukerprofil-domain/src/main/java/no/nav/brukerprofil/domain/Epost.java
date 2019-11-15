package no.nav.brukerprofil.domain;

import org.joda.time.LocalDateTime;

import java.io.Serializable;

public class Epost implements Serializable {

	private String identifikator;
	private boolean foretrukket;
	private LocalDateTime endringstidspunkt;
	private String endretAv;

	public String getIdentifikator() {
		return identifikator;
	}

	public void setIdentifikator(String value) {
		this.identifikator = value;
	}

	public boolean getForetrukket() {
		return foretrukket;
	}

	public void setForetrukket(boolean foretrukket) {
		this.foretrukket = foretrukket;
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
}
