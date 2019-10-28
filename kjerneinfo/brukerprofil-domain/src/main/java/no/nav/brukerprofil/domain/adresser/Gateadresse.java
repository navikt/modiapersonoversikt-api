package no.nav.brukerprofil.domain.adresser;

public class Gateadresse extends StrukturertAdresse {

	private String gatenavn;
	private String husnummer;
	private String husbokstav;
	private String bolignummer;

	public void setGatenavn(String gatenavn) {
		this.gatenavn = gatenavn;
	}

	public String getGatenavn() {
		return gatenavn;
	}

	public void setHusnummer(String husnummer) {
		this.husnummer = husnummer;
	}

	public String getHusnummer() {
		return husnummer;
	}

	public void setHusbokstav(String husbokstav) {
		this.husbokstav = husbokstav;
	}

	public String getHusbokstav() {
		return husbokstav;
	}

	public String getBolignummer() {
		return bolignummer;
	}

	public void setBolignummer(String value) {
		this.bolignummer = value;
	}
}

