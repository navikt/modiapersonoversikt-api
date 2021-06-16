package no.nav.modiapersonoversikt.consumer.personsok.domain;

import java.io.Serializable;

public class Kodeverkstype implements Serializable {
	private String kode;
	private String koderef;
	private String kodeverkref;

	public Kodeverkstype() {
	}

	public Kodeverkstype(String kode, String koderef, String kodeverkref) {
		this.kode = kode;
		this.koderef = koderef;
		this.kodeverkref = kodeverkref;
	}

	public String getKode() {
		return kode;
	}

	public void setKode(String kode) {
		this.kode = kode;
	}

	public String getKoderef() {
		return koderef;
	}

	public void setKoderef(String koderef) {
		this.koderef = koderef;
	}

	public String getKodeverkref() {
		return kodeverkref;
	}

	public void setKodeverkref(String kodeverkref) {
		this.kodeverkref = kodeverkref;
	}

	@Override
	public String toString() {
		return kode;
	}
}
