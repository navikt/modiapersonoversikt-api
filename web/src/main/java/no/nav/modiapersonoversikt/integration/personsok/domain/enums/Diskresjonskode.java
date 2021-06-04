package no.nav.modiapersonoversikt.integration.personsok.domain.enums;

public enum Diskresjonskode {
	// Blir sannsynligvis erstattet av kodeverkstjenesten i fremtiden
	KODE_0("0", ""),
	KODE_1("1", ""),
	KODE_2("2", ""),
	KODE_3("3", ""),
	KODE_4("4", ""),
	KODE_5("5", "Egen ansatt eller ansattes familie"),
	KODE_6("6", "Strengt fortrolig adresse"),
	KODE_7("7", "Fortrolig adresse");


	private String kode;
	private String beskrivelse;

	Diskresjonskode() {
	}

	Diskresjonskode(String kode, String beskrivelse) {
		this.kode = kode;
		this.beskrivelse = beskrivelse;
	}




	@Override
	public String toString() {
		return kode;

	}

	public String getBeskrivelse() {
		return beskrivelse;
	}

	public static Diskresjonskode withKode(String kode) {
		for (Diskresjonskode diskresjonskode : values()) {
			if (diskresjonskode.kode.equals(kode)) {
				return diskresjonskode;
			}
		}
		return null;
	}

	public String getKode() {
		return kode;
	}

	public boolean isKode(String kode) {
		return this.kode.equals(kode);
	}
}
