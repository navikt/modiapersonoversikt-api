package no.nav.kodeverk.consumer.fim.kodeverk.to.informasjon;

import no.nav.kjerneinfo.common.domain.Periode;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.List;

public class Kodeverk extends IdentifiserbarEntitet implements Serializable {
	private String eier;
	private Kodeverkskilde kilde;
	private LocalDate versjoneringsdato;
	private String versjonsnummer;
	private List<Periode> gyldighetsperiode;

	public String getEier() {
		return eier;
	}

	public void setEier(String eier) {
		this.eier = eier;
	}

	public Kodeverkskilde getKilde() {
		return kilde;
	}

	public void setKilde(Kodeverkskilde kilde) {
		this.kilde = kilde;
	}

	public LocalDate getVersjoneringsdato() {
		return versjoneringsdato;
	}

	public void setVersjoneringsdato(LocalDate versjoneringsdato) {
		this.versjoneringsdato = versjoneringsdato;
	}

	public String getVersjonsnummer() {
		return versjonsnummer;
	}

	public void setVersjonsnummer(String versjonsnummer) {
		this.versjonsnummer = versjonsnummer;
	}

	public List<Periode> getGyldighetsperiode() {
		return gyldighetsperiode;
	}

	public void setGyldighetsperiode(List<Periode> gyldighetsperiode) {
		this.gyldighetsperiode = gyldighetsperiode;
	}
}
