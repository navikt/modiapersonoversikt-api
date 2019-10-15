package no.nav.personsok.consumer.fim.personsok.mock;

/**
 * En samling av de dataene som er relevante for oss å hente ut informasjon om en person
 */
public class PersonMock {

	private String idnummer;
	private String identtype;
	private String sammensattNavn;
	private String fornavn;
	private String mellomnavn;
	private String etternavn;
	private String enhet;
	private String kjonn;
	private GateadresseMock bostedsadresse;
	private UstrukturertadresseMock postadresse;
	private UstrukturertadresseMock midlertidigadresse;
	private String diskresjonskode;
	private String personstatus;

	public PersonMock() {
	}

	public UstrukturertadresseMock getMidlertidigadresse() {
		return midlertidigadresse;
	}

	public void setMidlertidigadresse(UstrukturertadresseMock midlertidigadresse) {
		this.midlertidigadresse = midlertidigadresse;
	}

	public String getDiskresjonskode() {
		return diskresjonskode;
	}

	public void setDiskresjonskode(String diskresjonskode) {
		this.diskresjonskode = diskresjonskode;
	}

	public String getPersonstatus() {
		return personstatus;
	}

	public void setPersonstatus(String personstatus) {
		this.personstatus = personstatus;
	}

	public GateadresseMock getBostedsadresse() {
		return bostedsadresse;
	}

	public void setBostedsadresse(GateadresseMock bostedsadresse) {
		this.bostedsadresse = bostedsadresse;
	}

	public UstrukturertadresseMock getPostadresse() {
		return postadresse;
	}

	public void setPostadresse(UstrukturertadresseMock postadresse) {
		this.postadresse = postadresse;
	}

	public String getIdnummer() {
		return idnummer;
	}

	public void setIdnummer(String idnummer) {
		this.idnummer = idnummer;
	}

	public String getSammensattNavn() {
		return sammensattNavn;
	}

	public void setSammensattNavn(String sammensattNavn) {
		this.sammensattNavn = sammensattNavn;
	}

	public String getFornavn() {
		return fornavn;
	}

	public void setFornavn(String fornavn) {
		this.fornavn = fornavn;
	}

	public String getMellomnavn() {
		return mellomnavn;
	}

	public void setMellomnavn(String mellomnavn) {
		this.mellomnavn = mellomnavn;
	}

	public String getEtternavn() {
		return etternavn;
	}

	public void setEtternavn(String etternavn) {
		this.etternavn = etternavn;
	}

	public String getEnhet() {
		return enhet;
	}

	public void setEnhet(String enhet) {
		this.enhet = enhet;
	}

	public String getKjonn() {
		return kjonn;
	}

	public void setKjonn(String kjonn) {
		this.kjonn = kjonn;
	}

	public String getIdenttype() {
		return identtype;
	}

	public void setIdenttype(String identtype) {
		this.identtype = identtype;
	}
}
