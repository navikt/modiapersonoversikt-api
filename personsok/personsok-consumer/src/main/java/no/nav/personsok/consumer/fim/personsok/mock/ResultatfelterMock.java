package no.nav.personsok.consumer.fim.personsok.mock;

public class ResultatfelterMock {
	private String row;
	private String fodselsnummer;
	private String navn;
	private String kommunenr;
	private String diskresjonskode;
	private String css;
	private String link;

	public ResultatfelterMock() {
		row = "";
		fodselsnummer = "";
		navn = "";
		kommunenr = "";
		diskresjonskode = "";
	}

	public String getDiskresjonskode() {
		return diskresjonskode;
	}

	public void setDiskresjonskode(String diskresjonskode) {
		this.diskresjonskode = diskresjonskode;
	}

	public String getRow() {
		return row;
	}

	public void setRow(String row) {
		this.row = row;
	}

	public String getFodselsnummer() {
		return fodselsnummer;
	}

	public void setFodselsnummer(String fodselsnummer) {
		this.fodselsnummer = fodselsnummer;
	}

	public String getNavn() {
		return navn;
	}

	public void setNavn(String navn) {
		this.navn = navn;
	}

	public String getKommunenr() {
		return kommunenr;
	}

	public void setKommunenr(String kommunenr) {
		this.kommunenr = kommunenr;
	}

	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
}
