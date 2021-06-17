package no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.mock;

import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import no.nav.tjeneste.virksomhet.person.v3.metadata.Endringstyper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class PersonKjerneinfoMockFactory {

    public static final String MOBIL_TELEFON = "99556677";
    public static final String KODE_REF_MOBIL = "MOBI";
    public static final String RETNINGSNUMMER = "+47";
    public static final String ENDRET_AV = "Tom Jones";
    public static final String ENDRINGSTIDSPUNKT = "2015-02-02";
    public static final String LANDKODE = "SWE";

    private static final String TILLEGGSADRESSE = "MittTillegg";
    private static final String TILLEGGSADRESSE_TYPE = "C/O";
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonKjerneinfoMockFactory.class);
    private static final String POSTNUMMER = "0356";
    private static final String MIDLERTIDIG_POSTADRESSE_NORGE = "MIDLERTIDIG_POSTADRESSE_NORGE";
    private static final String POSTLEVERINGSPERIODE_TOM = "2018-02-02";
    private static final String ADR_GATEADRESSE = "gateadresse";
    private static final String FNR_SAMBOER = "33333333333";
    private static final String FNR_ADOPTIVMOR = "55555555555";
    private static final String BARN = "12345678910";
    private static final String PERSONSTATUSER = "Personstatuser";
    private static final String PERSONSTATUS_BOSATT = "BOSA";

    public Person getPerson() {
        return getPerson(new Person());
    }

    public Person getPerson(Person person) {
        person.setAktoer(lagAktoer("10108000398"));

        Personnavn fromPersonnavn = getMockPersonnavn("Donald", "Fauntleroy", "Duck");
        person.setPersonnavn(fromPersonnavn);

        person.setBostedsadresse(getMockBostedsadresse());
        person.setPostadresse(lagPostadresse());
        person.setKjoenn(new Kjoenn().withKjoenn(new Kjoennstyper().withValue("K")));
        person.setSivilstand(new Sivilstand().withSivilstand(new Sivilstander()
                .withValue("UGIF"))
                .withFomGyldighetsperiode(getCurrentXmlGregorianCalendar()));
        person.setStatsborgerskap(getMockStatsborgerskap("Andeby"));
        person.setPersonstatus(new Personstatus().withPersonstatus(new Personstatuser().withKodeRef(PERSONSTATUS_BOSATT).withKodeverksRef(PERSONSTATUSER)));

        return person;
    }

    private Postadresse lagPostadresse() {
        return new Postadresse()
                .withUstrukturertAdresse(getUstrukturertAdresse(ADR_GATEADRESSE, ADR_GATEADRESSE + "2", ADR_GATEADRESSE + "3", "", ""))
                .withEndringstidspunkt(getMockDato("2015-02-02"))
                .withEndretAv(ENDRET_AV);
    }

    private PersonIdent lagAktoer(String fodselsnummer) {
        return new PersonIdent().withIdent(getNorskIdent(fodselsnummer));
    }

    private UstrukturertAdresse getUstrukturertAdresse(String adresselinje1, String adresselinje2,
                                                       String adresselinje3, String adresselinje4, String landkode) {
        return new UstrukturertAdresse()
                .withAdresselinje1(adresselinje1)
                .withAdresselinje2(adresselinje2)
                .withAdresselinje3(adresselinje3)
                .withAdresselinje4(adresselinje4)
                .withLandkode(new Landkoder().withValue(landkode));
    }

    private Gateadresse getStrukturertAdresse(Gateadresse gateadresse, String gatenavn) {
        setStedsadresseinfo(gateadresse);
        gateadresse.setGatenavn(gatenavn);
        gateadresse.setGatenummer(1234567);
        gateadresse.setHusbokstav("A");
        gateadresse.setHusnummer(1);
        gateadresse.setPoststed(new Postnummer().withValue(POSTNUMMER));
        return gateadresse;
    }

    private void setStedsadresseinfo(StedsadresseNorge adresse) {
        adresse.setBolignummer("H001");
        adresse.setKommunenummer("1234");
        adresse.setPoststed(new Postnummer().withKodeRef("0002"));
        adresse.setTilleggsadresse(TILLEGGSADRESSE);
        adresse.setTilleggsadresseType(TILLEGGSADRESSE_TYPE);
        adresse.setLandkode(new Landkoder().withKodeRef("NO"));
    }

    private NorskIdent getNorskIdent(String foedselnummer) {
        return new NorskIdent().withIdent(foedselnummer);
    }

    public Bruker getBruker(String fodselsnummer, boolean genererKomplettBruker) {
        Bruker bruker = new Bruker();
        bruker = ((Bruker) getPerson(bruker))
                .withFoedested("Andeby")
                .withAktoer(lagAktoer(fodselsnummer));

        switch (fodselsnummer) {
            case FNR_SAMBOER:
                bruker.setPersonnavn(getMockPersonnavn("Donald", "", "Duck"));
                break;
            case FNR_ADOPTIVMOR:
                bruker.setPersonnavn(getMockPersonnavn("Elvira", "\"Bestemor\"", "Duck"));
                break;
            default:
                bruker.setPersonnavn(getMockPersonnavn("Donald", "NAV-bruker", "Duck"));
                break;
        }

        if (genererKomplettBruker) {
            bruker.setGeografiskTilknytning(new Bydel().withGeografiskTilknytning("123456"));
            bruker.setBankkonto(getMockBankkontoUtland());

            bruker.getHarFraRolleI().add(getMockFamilieRelasjon("Samboer", FNR_SAMBOER));
            bruker.getHarFraRolleI().add(getMockFamilieRelasjon("Adoptivmor", FNR_ADOPTIVMOR));

            leggTilBarn(bruker);

            Sikkerhetstiltak sikkerhetstiltak = new Sikkerhetstiltak();
            Periode periode = new Periode();
            periode.setFom(getCurrentXmlGregorianCalendar());
            periode.setTom(getCurrentXmlGregorianCalendar());
            sikkerhetstiltak.setPeriode(periode);
            sikkerhetstiltak.setSikkerhetstiltaksbeskrivelse("sikkerhetsbeskrivelse");
            sikkerhetstiltak.setSikkerhetstiltakskode("KODE");
            bruker.setSikkerhetstiltak(sikkerhetstiltak);

            bruker.setMidlertidigPostadresse(lagMidlertidigPostadresseNorge());
            bruker.setGjeldendePostadressetype(new Postadressetyper().withValue(MIDLERTIDIG_POSTADRESSE_NORGE).withKodeRef("TEST"));

            lagMockTelefon(bruker);
            lagMockTilrettelagtKommunikasjon(bruker);

        }

        return bruker;
    }

    private void leggTilBarn(Bruker bruker) {
        bruker.getHarFraRolleI().add(lagFamilieRelasjonBarn().withTilPerson(lagBarn(BARN)));
        bruker.getHarFraRolleI().add(lagFamilieRelasjonBarn().withTilPerson(lagBarn(BARN)));
        bruker.getHarFraRolleI().add(lagFamilieRelasjonBarn().withTilPerson(lagBarn(BARN)));
        bruker.getHarFraRolleI().add(lagFamilieRelasjonBarn()
                .withHarSammeBosted(false)
                .withTilPerson(lagBarn("12345678910")
                        .withDiskresjonskode(new Diskresjonskoder()
                                .withValue("SPFO")
                                .withKodeRef("SPFO"))
                        .withPersonnavn(getMockPersonnavn("Hemmelig", "X", "Hemligsen"))));
    }

    private Familierelasjon lagFamilieRelasjonBarn() {
        Familierelasjon relasjon = getFamilieReleasjon("BARN");
        relasjon.setHarSammeBosted(true);
        return relasjon;
    }

    private Bruker lagBarn(String fodselsnummer) {
        return new Bruker()
                .withPersonnavn(getMockPersonnavn("Jokke", "Olsen", "Nilsen"))
                .withAktoer(new PersonIdent().withIdent(new NorskIdent().withIdent(fodselsnummer)));
    }

    private MidlertidigPostadresse lagMidlertidigPostadresseNorge() {
        return new MidlertidigPostadresseNorge()
                .withStrukturertAdresse(getStrukturertAdresse(new Gateadresse(), ADR_GATEADRESSE))
                .withEndretAv(ENDRET_AV)
                .withEndringstidspunkt(getMockDato(ENDRINGSTIDSPUNKT))
                .withPostleveringsPeriode(new Gyldighetsperiode()
                        .withFom(getMockDato("2015-02-02"))
                        .withTom(getMockDato(POSTLEVERINGSPERIODE_TOM)));
    }

    private void lagMockTilrettelagtKommunikasjon(Bruker bruker) {
        bruker.withTilrettelagtKommunikasjon(
                new TilrettelagtKommunikasjonbehov()
                        .withBehov("Tolkehjelp")
                        .withTilrettelagtKommunikasjon(
                                new TilrettelagtKommunikasjon().withValue("TOHJ")),
                new TilrettelagtKommunikasjonbehov()
                        .withBehov("Ledsager")
                        .withTilrettelagtKommunikasjon(
                                new TilrettelagtKommunikasjon().withValue("LESA")));
    }

    private void lagMockTelefon(Bruker bruker) {
        bruker.withKontaktinformasjon(new Telefonnummer()
                .withIdentifikator(MOBIL_TELEFON)
                .withType(new Telefontyper()
                        .withValue(KODE_REF_MOBIL)
                        .withKodeRef(KODE_REF_MOBIL))
                .withRetningsnummer(new Retningsnumre().withValue(RETNINGSNUMMER)));
    }

    private Familierelasjon getFamilieReleasjon(String familierelasjonstype) {
        Familierelasjon familierelasjon = new Familierelasjon();
        familierelasjon.setTilRolle(new Familierelasjoner()
                .withKodeRef(familierelasjonstype)
                .withValue(familierelasjonstype));
        return familierelasjon;
    }

    public Familierelasjon getMockFamilieRelasjon(String familierelasjonstype, String fnr) {
        Familierelasjon familierelasjon = new Familierelasjon();
        familierelasjon.setHarSammeBosted(Boolean.TRUE);
        familierelasjon.setTilRolle(new Familierelasjoner().withKodeRef(familierelasjonstype).withValue(familierelasjonstype));
        familierelasjon.setTilPerson(getBruker(fnr, false));
        return familierelasjon;
    }

    private BankkontoUtland getMockBankkontoUtland() {
        BankkontoUtland bankkontoUtland = new BankkontoUtland();
        BankkontonummerUtland bankkontonummerUtland = new BankkontonummerUtland();
        bankkontonummerUtland.setSwift("SPTRNO22");
        bankkontonummerUtland.setBankkontonummer("9876.98.98765");
        bankkontonummerUtland.setValuta(new Valutaer().withValue("USD").withKodeRef("USD"));
        bankkontonummerUtland.setLandkode(new Landkoder().withValue("USA").withKodeRef("USA"));
        bankkontonummerUtland.setBanknavn("Chase");
        bankkontonummerUtland.setBankadresse(new UstrukturertAdresse()
                .withAdresselinje1("Fifth avenue")
                .withAdresselinje2("New York")
                .withAdresselinje3("New York")
                .withAdresselinje4("USA"));
        bankkontonummerUtland.setBankkode("kode");
        bankkontoUtland.setBankkontoUtland(bankkontonummerUtland);
        bankkontoUtland.setEndringstidspunkt(getMockDato(ENDRINGSTIDSPUNKT));
        bankkontoUtland.setEndretAv(ENDRET_AV);
        return bankkontoUtland;
    }

    private Bostedsadresse getMockBostedsadresse() {
        Bostedsadresse bostedsadresse = new Bostedsadresse();
        bostedsadresse.setStrukturertAdresse(getStrukturertAdresse(new Gateadresse(), "Apalveien"));
        bostedsadresse.setEndretAv("Adresseendrer");
        bostedsadresse.setEndringstidspunkt(getMockDato("2015-02-02"));
        return bostedsadresse;
    }

    private static XMLGregorianCalendar getCurrentXmlGregorianCalendar() {
        try {
            GregorianCalendar cal = new GregorianCalendar();
            cal.add(Calendar.DAY_OF_MONTH, -1);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException e) {
            LOGGER.warn("DatatypeConfigurationException.:" + "getCurrentXmlGregorianCalendar()");
        }

        return null;
    }

    private static XMLGregorianCalendar getMockDato(String date) {
        GregorianCalendar cal = GregorianCalendar.from(LocalDate.parse(date).atStartOfDay(ZoneId.systemDefault()));
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Statsborgerskap getMockStatsborgerskap(String land) {
        Statsborgerskap statsborgerskap = new Statsborgerskap();
        statsborgerskap.setLand(new Landkoder().withKodeRef(land).withValue(land));

        statsborgerskap.setEndretAv("Petter Smart");
        statsborgerskap.setEndringstidspunkt(getCurrentXmlGregorianCalendar());
        statsborgerskap.setEndringstype(Endringstyper.NY);
        return statsborgerskap;
    }

    private Personnavn getMockPersonnavn(String fornavn, String mellomnavn, String etternavn) {
        Personnavn personnavn = new Personnavn();
        personnavn.setFornavn(fornavn);
        personnavn.setMellomnavn(mellomnavn);
        personnavn.setEtternavn(etternavn);
        personnavn.setSammensattNavn(fornavn + " " + mellomnavn + " " + etternavn);
        return personnavn;
    }

}
