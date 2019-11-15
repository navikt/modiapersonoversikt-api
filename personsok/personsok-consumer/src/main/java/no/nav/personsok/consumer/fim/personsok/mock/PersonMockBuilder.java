package no.nav.personsok.consumer.fim.personsok.mock;

public class PersonMockBuilder {
	private String fodselsnummer;
	private String identtype;
	private String fornavn;
	private String mellomnavn;
	private String etternavn;
	private GateadresseMock bostedsadresse;
	private UstrukturertadresseMock postadresse;
	private UstrukturertadresseMock utenlandsadresse;
	private String enhet;
	private String kjonn;
	private String diskresjonskode;
	private String personstatus;

	public PersonMockBuilder setFodselsnummer(String fodselsnummer) {
		this.fodselsnummer = fodselsnummer;
		return this;
	}

	public PersonMockBuilder setIdenttype(String identtype) {
		this.identtype = identtype;
		return this;
	}

	public PersonMockBuilder setFornavn(String fornavn) {
		this.fornavn = fornavn;
		return this;
	}

	public PersonMockBuilder setMellomnavn(String mellomnavn) {
		this.mellomnavn = mellomnavn;
		return this;
	}

	public PersonMockBuilder setEtternavn(String etternavn) {
		this.etternavn = etternavn;
		return this;
	}

	public PersonMockBuilder setBostedsadresse(GateadresseMock bostedsadresse) {
		this.bostedsadresse = bostedsadresse;
		return this;
	}

	public PersonMockBuilder setPostadresse(UstrukturertadresseMock postadresse) {
		this.postadresse = postadresse;
		return this;
	}

	public PersonMockBuilder setUtenlandsadresse(UstrukturertadresseMock utenlandsadresse) {
		this.utenlandsadresse = utenlandsadresse;
		return this;
	}

	public PersonMockBuilder setEnhet(String enhet) {
		this.enhet = enhet;
		return this;
	}

	public PersonMockBuilder setKjonn(String kjonn) {
		this.kjonn = kjonn;
		return this;
	}

	public PersonMockBuilder setDiskresjonskode(String diskresjonskode) {
		this.diskresjonskode = diskresjonskode;
		return this;
	}

	public PersonMockBuilder setPersonstatus(String personstatus) {
		this.personstatus = personstatus;
		return this;
	}

	public PersonMock createPersonMock() {
		PersonMock personMock = new PersonMock();
		personMock.setIdnummer(fodselsnummer);
		personMock.setIdenttype(identtype);
		personMock.setFornavn(fornavn);
		personMock.setMellomnavn(mellomnavn);
		personMock.setEtternavn(etternavn);
		personMock.setBostedsadresse(bostedsadresse);
		personMock.setPostadresse(postadresse);
		personMock.setMidlertidigadresse(utenlandsadresse);
		personMock.setEnhet(enhet);
		personMock.setKjonn(kjonn);
		personMock.setDiskresjonskode(diskresjonskode);
		personMock.setPersonstatus(personstatus);
		return personMock;
	}
}