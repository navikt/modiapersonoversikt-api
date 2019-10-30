package no.nav.personsok.consumer.fim.personsok.mock;

public class SorterFelterMock {

	private String fodselsnummerDirection;
	private String navnDirection;
	private String kommunenrDirection;
	private String diskresjonskode;
	private String forste;
	private String forrige;
	private String neste;
	private String siste;

	public String getDiskresjonskode() {
		return diskresjonskode;
	}

	public void setDiskresjonskode(String diskresjonskode) {
		this.diskresjonskode = diskresjonskode;
	}

	public String getForste() {
		return forste;
	}

	public void setForste(String forste) {
		this.forste = forste;
	}

	public String getForrige() {
		return forrige;
	}

	public void setForrige(String forrige) {
		this.forrige = forrige;
	}

	public String getNeste() {
		return neste;
	}

	public void setNeste(String neste) {
		this.neste = neste;
	}

	public String getSiste() {
		return siste;
	}

	public void setSiste(String siste) {
		this.siste = siste;
	}

	public String getFodselsnummer() {
		return fodselsnummerDirection;
	}

	public void setFodselsnummer(String direction) {
		this.fodselsnummerDirection = direction;
	}

	public String getNavn() {
		return navnDirection;
	}

	public void setNavn(String direction) {
		this.navnDirection = direction;
	}

	public String getKommunenr() {
		return kommunenrDirection;
	}

	public void setKommunenr(String direction) {
		this.kommunenrDirection = direction;
	}
}
