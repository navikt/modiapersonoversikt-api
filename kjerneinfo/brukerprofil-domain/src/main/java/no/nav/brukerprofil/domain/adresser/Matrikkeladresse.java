package no.nav.brukerprofil.domain.adresser;

public class Matrikkeladresse extends StrukturertAdresse {

	private String eiendomsnavn;
	private String bolignummer;
	private String gaardsnummer;
	private String bruksnummer;
	private String festenummer;
	private String seksjonsnummer;
	private String undernummer;

	public String getEiendomsnavn() {
		return eiendomsnavn;
	}

	public void setEiendomsnavn(String value) {
		this.eiendomsnavn = value;
	}

	public String getBolignummer() {
		return bolignummer;
	}

	public void setBolignummer(String value) {
		this.bolignummer = value;
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
}