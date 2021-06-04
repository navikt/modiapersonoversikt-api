package no.nav.modiapersonoversikt.integration.kodeverk.consumer.fim.kodeverk.to.meldinger;


import java.io.Serializable;

public class HentKodeverkRequest implements Serializable{
	private String navn;
	private String versjonsnummer;
	private String spraak;


	public String getNavn() {
		return navn;
	}

	public void setNavn(String navn) {
		this.navn = navn;
	}

	public String getVersjonsnummer() {
		return versjonsnummer;
	}

	public void setVersjonsnummer(String versjonsnummer) {
		this.versjonsnummer = versjonsnummer;
	}

	public String getSpraak() {
		return spraak;
	}

	public void setSpraak(String spraak) {
		this.spraak = spraak;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
	        return true;
        }
        if (o == null || getClass() != o.getClass()) {
	        return false;
        }

        HentKodeverkRequest that = (HentKodeverkRequest) o;

        if (!navn.equals(that.navn)) {
	        return false;
        }
        if (!spraak.equals(that.spraak)) {
	        return false;
        }
        if (!versjonsnummer.equals(that.versjonsnummer)) {
	        return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = navn.hashCode();
        result = 31 * result + versjonsnummer.hashCode();
        result = 31 * result + spraak.hashCode();
        return result;
    }
}
