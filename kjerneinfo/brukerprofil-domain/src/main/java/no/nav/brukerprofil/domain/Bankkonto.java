package no.nav.brukerprofil.domain;

import org.joda.time.LocalDateTime;

import java.io.Serializable;

public class Bankkonto implements Serializable {

	private String kontonummer;
	private String banknavn;
	private LocalDateTime endringstidspunkt;
	private String endretAv;

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
