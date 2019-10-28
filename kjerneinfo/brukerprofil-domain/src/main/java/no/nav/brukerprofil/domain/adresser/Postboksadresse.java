package no.nav.brukerprofil.domain.adresser;

public class Postboksadresse extends StrukturertAdresse {

	private String postboksnummer;
	private String postboksanlegg;

	public String getPostboksnummer() {
		return postboksnummer;
	}

	public void setPostboksnummer(String value) {
		this.postboksnummer = value;
	}

	public String getPostboksanlegg() {
		return postboksanlegg;
	}

	public void setPostboksanlegg(String postboksanlegg) {
		this.postboksanlegg = postboksanlegg;
	}
}
