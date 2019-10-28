package no.nav.kjerneinfo.consumer.fim.person.mock;

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

    public WSPerson getPerson() {
        return getPerson(new WSPerson());
    }

    public WSPerson getPerson(WSPerson person) {
        person.setAktoer(lagAktoer("10108000398"));

        WSPersonnavn fromPersonnavn = getMockPersonnavn("Donald", "Fauntleroy", "Duck");
        person.setPersonnavn(fromPersonnavn);

        person.setBostedsadresse(getMockBostedsadresse());
        person.setPostadresse(lagPostadresse());
        person.setKjoenn(new WSKjoenn().withKjoenn(new WSKjoennstyper().withValue("K")));
        person.setSivilstand(new WSSivilstand().withSivilstand(new WSSivilstander()
                .withValue("UGIF"))
                .withFomGyldighetsperiode(getCurrentXmlGregorianCalendar()));
        person.setStatsborgerskap(getMockStatsborgerskap("Andeby"));
        person.setPersonstatus(new WSPersonstatus().withPersonstatus(new WSPersonstatuser().withKodeRef(PERSONSTATUS_BOSATT).withKodeverksRef(PERSONSTATUSER)));

        return person;
    }

    private WSPostadresse lagPostadresse() {
        return new WSPostadresse()
                .withUstrukturertAdresse(getUstrukturertAdresse(ADR_GATEADRESSE, ADR_GATEADRESSE + "2", ADR_GATEADRESSE + "3", "", ""))
                .withEndringstidspunkt(getMockDato("2015-02-02"))
                .withEndretAv(ENDRET_AV);
    }

    private WSPersonIdent lagAktoer(String fodselsnummer) {
        return new WSPersonIdent().withIdent(getNorskIdent(fodselsnummer));
    }

    private WSUstrukturertAdresse getUstrukturertAdresse(String adresselinje1, String adresselinje2,
                                                         String adresselinje3, String adresselinje4, String landkode) {
        return new WSUstrukturertAdresse()
                .withAdresselinje1(adresselinje1)
                .withAdresselinje2(adresselinje2)
                .withAdresselinje3(adresselinje3)
                .withAdresselinje4(adresselinje4)
                .withLandkode(new WSLandkoder().withValue(landkode));
    }

    private WSGateadresse getStrukturertAdresse(WSGateadresse gateadresse, String gatenavn) {
        setStedsadresseinfo(gateadresse);
        gateadresse.setGatenavn(gatenavn);
        gateadresse.setGatenummer(1234567);
        gateadresse.setHusbokstav("A");
        gateadresse.setHusnummer(1);
        gateadresse.setPoststed(new WSPostnummer().withValue(POSTNUMMER));
        return gateadresse;
    }

    private void setStedsadresseinfo(WSStedsadresseNorge adresse) {
        adresse.setBolignummer("H001");
        adresse.setKommunenummer("1234");
        adresse.setPoststed(new WSPostnummer().withKodeRef("0002"));
        adresse.setTilleggsadresse(TILLEGGSADRESSE);
        adresse.setTilleggsadresseType(TILLEGGSADRESSE_TYPE);
        adresse.setLandkode(new WSLandkoder().withKodeRef("NO"));
    }

    private WSNorskIdent getNorskIdent(String foedselnummer) {
        return new WSNorskIdent().withIdent(foedselnummer);
    }

    public WSBruker getBruker(String fodselsnummer, boolean genererKomplettBruker) {
        WSBruker bruker = new WSBruker();
        bruker = ((WSBruker) getPerson(bruker))
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
            bruker.setGeografiskTilknytning(new WSBydel().withGeografiskTilknytning("123456"));
            bruker.setBankkonto(getMockBankkontoUtland());

            bruker.getHarFraRolleI().add(getMockFamilieRelasjon("Samboer", FNR_SAMBOER));
            bruker.getHarFraRolleI().add(getMockFamilieRelasjon("Adoptivmor", FNR_ADOPTIVMOR));

            leggTilBarn(bruker);

            WSSikkerhetstiltak sikkerhetstiltak = new WSSikkerhetstiltak();
            WSPeriode periode = new WSPeriode();
            periode.setFom(getCurrentXmlGregorianCalendar());
            periode.setTom(getCurrentXmlGregorianCalendar());
            sikkerhetstiltak.setPeriode(periode);
            sikkerhetstiltak.setSikkerhetstiltaksbeskrivelse("sikkerhetsbeskrivelse");
            sikkerhetstiltak.setSikkerhetstiltakskode("KODE");
            bruker.setSikkerhetstiltak(sikkerhetstiltak);

            bruker.setMidlertidigPostadresse(lagMidlertidigPostadresseNorge());
            bruker.setGjeldendePostadressetype(new WSPostadressetyper().withValue(MIDLERTIDIG_POSTADRESSE_NORGE).withKodeRef("TEST"));

            lagMockTelefon(bruker);
            lagMockTilrettelagtKommunikasjon(bruker);

        }

        return bruker;
    }

    private void leggTilBarn(WSBruker bruker) {
        bruker.getHarFraRolleI().add(lagFamilieRelasjonBarn().withTilPerson(lagBarn(BARN)));
        bruker.getHarFraRolleI().add(lagFamilieRelasjonBarn().withTilPerson(lagBarn(BARN)));
        bruker.getHarFraRolleI().add(lagFamilieRelasjonBarn().withTilPerson(lagBarn(BARN)));
        bruker.getHarFraRolleI().add(lagFamilieRelasjonBarn()
                .withHarSammeBosted(false)
                .withTilPerson(lagBarn("12345678910")
                        .withDiskresjonskode(new WSDiskresjonskoder()
                                .withValue("SPFO")
                                .withKodeRef("SPFO"))
                        .withPersonnavn(getMockPersonnavn("Hemmelig", "X", "Hemligsen"))));
    }

    private WSFamilierelasjon lagFamilieRelasjonBarn() {
        WSFamilierelasjon relasjon = getFamilieReleasjon("BARN");
        relasjon.setHarSammeBosted(true);
        return relasjon;
    }

    private WSBruker lagBarn(String fodselsnummer) {
        return new WSBruker()
                .withPersonnavn(getMockPersonnavn("Jokke", "Olsen", "Nilsen"))
                .withAktoer(new WSPersonIdent().withIdent(new WSNorskIdent().withIdent(fodselsnummer)));
    }

    private WSMidlertidigPostadresse lagMidlertidigPostadresseNorge() {
        return new WSMidlertidigPostadresseNorge()
                .withStrukturertAdresse(getStrukturertAdresse(new WSGateadresse(), ADR_GATEADRESSE))
                .withEndretAv(ENDRET_AV)
                .withEndringstidspunkt(getMockDato(ENDRINGSTIDSPUNKT))
                .withPostleveringsPeriode(new WSGyldighetsperiode()
                        .withFom(getMockDato("2015-02-02"))
                        .withTom(getMockDato(POSTLEVERINGSPERIODE_TOM)));
    }

    private void lagMockTilrettelagtKommunikasjon(WSBruker bruker) {
        bruker.withTilrettelagtKommunikasjon(
                new WSTilrettelagtKommunikasjonbehov()
                        .withBehov("Tolkehjelp")
                        .withTilrettelagtKommunikasjon(
                                new WSTilrettelagtKommunikasjon().withValue("TOHJ")),
                new WSTilrettelagtKommunikasjonbehov()
                        .withBehov("Ledsager")
                        .withTilrettelagtKommunikasjon(
                                new WSTilrettelagtKommunikasjon().withValue("LESA")));
    }

    private void lagMockTelefon(WSBruker bruker) {
        bruker.withKontaktinformasjon(new WSTelefonnummer()
                .withIdentifikator(MOBIL_TELEFON)
                .withType(new WSTelefontyper()
                        .withValue(KODE_REF_MOBIL)
                        .withKodeRef(KODE_REF_MOBIL))
                .withRetningsnummer(new WSRetningsnumre().withValue(RETNINGSNUMMER)));
    }

    private WSFamilierelasjon getFamilieReleasjon(String familierelasjonstype) {
        WSFamilierelasjon familierelasjon = new WSFamilierelasjon();
        familierelasjon.setTilRolle(new WSFamilierelasjoner()
                .withKodeRef(familierelasjonstype)
                .withValue(familierelasjonstype));
        return familierelasjon;
    }

    public WSFamilierelasjon getMockFamilieRelasjon(String familierelasjonstype, String fnr) {
        WSFamilierelasjon familierelasjon = new WSFamilierelasjon();
        familierelasjon.setHarSammeBosted(Boolean.TRUE);
        familierelasjon.setTilRolle(new WSFamilierelasjoner().withKodeRef(familierelasjonstype).withValue(familierelasjonstype));
        familierelasjon.setTilPerson(getBruker(fnr, false));
        return familierelasjon;
    }

    private WSBankkontoUtland getMockBankkontoUtland() {
        WSBankkontoUtland bankkontoUtland = new WSBankkontoUtland();
        WSBankkontonummerUtland bankkontonummerUtland = new WSBankkontonummerUtland();
        bankkontonummerUtland.setSwift("SPTRNO22");
        bankkontonummerUtland.setBankkontonummer("9876.98.98765");
        bankkontonummerUtland.setValuta(new WSValutaer().withValue("USD").withKodeRef("USD"));
        bankkontonummerUtland.setLandkode(new WSLandkoder().withValue("USA").withKodeRef("USA"));
        bankkontonummerUtland.setBanknavn("Chase");
        bankkontonummerUtland.setBankadresse(new WSUstrukturertAdresse()
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

    private WSBostedsadresse getMockBostedsadresse() {
        WSBostedsadresse bostedsadresse = new WSBostedsadresse();
        bostedsadresse.setStrukturertAdresse(getStrukturertAdresse(new WSGateadresse(), "Apalveien"));
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

    private WSStatsborgerskap getMockStatsborgerskap(String land) {
        WSStatsborgerskap statsborgerskap = new WSStatsborgerskap();
        statsborgerskap.setLand(new WSLandkoder().withKodeRef(land).withValue(land));

        statsborgerskap.setEndretAv("Petter Smart");
        statsborgerskap.setEndringstidspunkt(getCurrentXmlGregorianCalendar());
        statsborgerskap.setEndringstype(Endringstyper.NY);
        return statsborgerskap;
    }

    private WSPersonnavn getMockPersonnavn(String fornavn, String mellomnavn, String etternavn) {
        WSPersonnavn personnavn = new WSPersonnavn();
        personnavn.setFornavn(fornavn);
        personnavn.setMellomnavn(mellomnavn);
        personnavn.setEtternavn(etternavn);
        personnavn.setSammensattNavn(fornavn + " " + mellomnavn + " " + etternavn);
        return personnavn;
    }

}
