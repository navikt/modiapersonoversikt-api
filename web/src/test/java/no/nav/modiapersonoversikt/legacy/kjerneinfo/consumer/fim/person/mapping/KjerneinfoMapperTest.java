package no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.mapping;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Kodeverdi;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.mock.PersonKjerneinfoMockFactory;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.support.KjerneinfoMapper;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.info.Bankkonto;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.info.BankkontoUtland;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.*;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.Person;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.Personnavn;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.UstrukturertAdresse;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.fakta.Familierelasjon;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.fakta.Sikkerhetstiltak;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.fakta.Telefon;
import no.nav.modiapersonoversikt.integration.kodeverk.consumer.fim.kodeverk.support.DefaultKodeverkmanager;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentSikkerhetstiltakResponse;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.List;

import static no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.mock.PersonKjerneinfoMockFactory.*;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class KjerneinfoMapperTest {

    private static final String BANKKODE = "BANKKODE";
    private static final String KONTONUMMER = "KONTONUMMER";
    private static final String BANKNAVN = "DEN URNORSKE BANK";
    private static final String SWIFT = "RASK";
    private static final String VALUTA = "MOR";

    private KjerneinfoMapper mapper;
    private PersonKjerneinfoMockFactory mockFactory;

    @Before
    public void setUp() {
        DefaultKodeverkmanager kodeverk = new DefaultKodeverkmanager(mock(KodeverkPortType.class));
        mapper = new KjerneinfoMapper(kodeverk);
        mockFactory = new PersonKjerneinfoMockFactory();
    }


    @Test
    public void responseMapping() {
        Bruker wsBruker = mockFactory.getBruker("98765498765", true);
        HentPersonResponse wsResponse = new HentPersonResponse()
                .withPerson(wsBruker);

        HentKjerneinformasjonResponse response = mapper.map(wsResponse, HentKjerneinformasjonResponse.class);

        Person person = response.getPerson();
        assertEquals(((PersonIdent) wsBruker.getAktoer()).getIdent().getIdent(), person.getFodselsnummer().getNummer());

        no.nav.tjeneste.virksomhet.person.v3.informasjon.Personnavn fromPersonnavn = wsBruker.getPersonnavn();
        Personnavn toPersonnavn = person.getPersonfakta().getPersonnavn();

        assertEqualsPersonnavn(fromPersonnavn, toPersonnavn);
        assertEqualsBankkontoUtland(((no.nav.tjeneste.virksomhet.person.v3.informasjon.BankkontoUtland) wsBruker.getBankkonto()).getBankkontoUtland(), ((BankkontoUtland) person.getPersonfakta().getBankkonto()));
        assertAdresseEquals(wsBruker, person);
        assertSikkerhetstiltakEquals(wsBruker, person);
        assertEqualsFamilierelasjoner(wsBruker.getHarFraRolleI(), person.getPersonfakta().getHarFraRolleIList());
    }

    private void assertSikkerhetstiltakEquals(Bruker wsPerson, Person person) {
        assertEquals(LocalDate.fromDateFields(wsPerson.getSikkerhetstiltak().getPeriode().getFom().toGregorianCalendar().getTime()), person.getPersonfakta().getSikkerhetstiltak().getPeriode().getFrom());
        assertEquals(LocalDate.fromDateFields(wsPerson.getSikkerhetstiltak().getPeriode().getTom().toGregorianCalendar().getTime()), person.getPersonfakta().getSikkerhetstiltak().getPeriode().getTo());
        assertEquals(wsPerson.getSikkerhetstiltak().getSikkerhetstiltaksbeskrivelse(), person.getPersonfakta().getSikkerhetstiltak().getSikkerhetstiltaksbeskrivelse());
        assertEquals(wsPerson.getSikkerhetstiltak().getSikkerhetstiltakskode(), person.getPersonfakta().getSikkerhetstiltak().getSikkerhetstiltakskode());
    }

    private void assertAdresseEquals(Bruker wsPerson, Person person) {
        assertEqualsGateadresse((Gateadresse) wsPerson.getBostedsadresse().getStrukturertAdresse(), (Adresse) person.getPersonfakta().getBostedsadresse());
        assertEquals(wsPerson.getPostadresse().getUstrukturertAdresse().getAdresselinje1(), ((UstrukturertAdresse) person.getPersonfakta().getPostadresse()).getAdresselinje1());
        assertEquals(wsPerson.getKjoenn().getKjoenn().getValue(), person.getPersonfakta().getKjonn().toString());
        assertEquals(wsPerson.getSivilstand().getSivilstand().getValue(), person.getPersonfakta().getSivilstand().getKodeRef());
        assertEquals(LocalDate.fromDateFields(wsPerson.getSivilstand().getFomGyldighetsperiode().toGregorianCalendar().getTime()), person.getPersonfakta().getSivilstandFom());
        assertEquals(wsPerson.getPersonstatus().getPersonstatus().getValue(), person.getPersonfakta().getBostatus().getKodeRef());
        assertEquals(wsPerson.getStatsborgerskap().getLand().getValue(), person.getPersonfakta().getStatsborgerskap().getKodeRef());
        assertEquals(wsPerson.getFoedested(), person.getPersonfakta().getFodested());
    }

    @Test
    public void brukerMapping() {
        Bruker wsBruker = mockFactory.getBruker("12345612345", true);

        Person person = mapper.map(wsBruker, Person.class);

        assertEquals(((PersonIdent) wsBruker.getAktoer()).getIdent().getIdent(), person.getFodselsnummer().getNummer());
        assertEquals(wsBruker.getFoedested(), person.getPersonfakta().getFodested());
    }

    @Test
    public void personMapping() {
        no.nav.tjeneste.virksomhet.person.v3.informasjon.Person wsPerson = mockFactory.getBruker("12345612345", true);

        Person person = mapper.map(wsPerson, Person.class);

        assert (wsPerson.getBostedsadresse().getStrukturertAdresse() instanceof Gateadresse);
        assert (person.getPersonfakta().getBostedsadresse() instanceof Adresse);
        assertEquals(((Gateadresse) wsPerson.getBostedsadresse().getStrukturertAdresse()).getGatenavn(), ((Adresse) person.getPersonfakta().getBostedsadresse()).getGatenavn());
        assertEquals(wsPerson.getBostedsadresse().getEndretAv(), person.getPersonfakta().getBostedsadresse().getEndringsinformasjon().getEndretAv());
    }

    @Test
    public void personTilWSPerson() {
        Person from = new Person();
        from.setFodselsnummer(new Fodselsnummer("10108000398"));
        from.setPersonId(12345);
        from.setPersonfakta(getPersonfakta());

        no.nav.tjeneste.virksomhet.person.v3.informasjon.Person to = mapper.map(from, no.nav.tjeneste.virksomhet.person.v3.informasjon.Person.class);

        assertEquals(from.getFodselsnummer().getNummer(), ((PersonIdent) to.getAktoer()).getIdent().getIdent());
        assert (from.getPersonfakta().getBostedsadresse() instanceof Adresse);
        assert (from.getPersonfakta().getPostadresse() instanceof Adresse);
    }

    @Test
    public void dodPerson() {
        no.nav.tjeneste.virksomhet.person.v3.informasjon.Person wsPerson = new Bruker().withDoedsdato(new Doedsdato().withDoedsdato(getMockDato("2015-02-02")));

        Person person = mapper.map(wsPerson, Person.class);

        assertEquals(wsPerson.getDoedsdato().getDoedsdato().toGregorianCalendar().getTime(), person.getPersonfakta().getDoedsdato().toDate());
    }

    @Test
    public void wSSikkerhetstiltakToSikkerhetstiltak() {
        String bekrivelse = "Farlig Person.";
        no.nav.tjeneste.virksomhet.person.v3.informasjon.Sikkerhetstiltak fimSikkerhetsTiltak = new no.nav.tjeneste.virksomhet.person.v3.informasjon.Sikkerhetstiltak().withSikkerhetstiltaksbeskrivelse(bekrivelse);
        HentSikkerhetstiltakResponse sikkerhetstiltakResponse = new HentSikkerhetstiltakResponse();
        sikkerhetstiltakResponse.setSikkerhetstiltak(fimSikkerhetsTiltak);

        Sikkerhetstiltak sikkerhetsTiltak = mapper.map(sikkerhetstiltakResponse.getSikkerhetstiltak(), Sikkerhetstiltak.class);

        assertEquals(sikkerhetsTiltak.getSikkerhetstiltaksbeskrivelse(), bekrivelse);
    }

    @Test
    public void telefonnummerMapping() {
        Bruker from = mockFactory.getBruker("12345612345", true);

        Person to = mapper.map(from, Person.class);
        Telefon telefon = to.getPersonfakta().getMobil().get();

        assertThat(telefon.getIdentifikator(), is(MOBIL_TELEFON));
        assertThat(telefon.getRetningsnummer().getKodeRef(), is(RETNINGSNUMMER));
        assertThat(telefon.getType().getKodeRef(), is(KODE_REF_MOBIL));
    }

    @Test
    public void tilrettelagtKommunikasjonMapping() {
        TilrettelagtKommunikasjonbehov wsTilrettelagtKommunikasjon = new TilrettelagtKommunikasjonbehov()
                .withBehov("Ledsager")
                .withTilrettelagtKommunikasjon(new TilrettelagtKommunikasjon().withValue("LESA"));

        Kodeverdi to = mapper.map(wsTilrettelagtKommunikasjon, Kodeverdi.class);

        assertThat(to.getKodeRef(), is("LESA"));
        assertThat(to.getBeskrivelse(), is("Ledsager"));
    }

    @Test
    public void brukerMedTilrettelagtKommunikasjonMapping() {
        Bruker from = mockFactory.getBruker("12345612345", true);

        Person to = mapper.map(from, Person.class);
        List<Kodeverdi> tilrettelagtKommunikasjon = to.getPersonfakta().getTilrettelagtKommunikasjon();

        assertThat(tilrettelagtKommunikasjon.size(), is(2));
        assertThat(tilrettelagtKommunikasjon.get(0).getBeskrivelse(), is((not(nullValue()))));
    }

    @Test
    public void bankkontoMapping() {
        Bruker from = mockFactory.getBruker("123456789123", false);
        from.setBankkonto(new BankkontoNorge()
                .withBankkonto(new Bankkontonummer()
                        .withBankkontonummer(KONTONUMMER)
                        .withBanknavn(BANKNAVN))
                .withEndretAv(ENDRET_AV)
                .withEndringstidspunkt(getMockDato(ENDRINGSTIDSPUNKT)));

        Person to = mapper.map(from, Person.class);
        Bankkonto bankkonto = to.getPersonfakta().getBankkonto();

        assertThat(bankkonto.getKontonummer(), is(KONTONUMMER));
        assertThat(bankkonto.getBanknavn(), is(BANKNAVN));
        assertThat(bankkonto.getEndringsinformasjon().getEndretAv(), is(ENDRET_AV));
        assertThat(bankkonto.getEndringsinformasjon().getSistOppdatert().toLocalDate().toString(), is(ENDRINGSTIDSPUNKT));
    }

    @Test
    public void bankkontoUtlandMapping() {
        Bruker from = mockFactory.getBruker("123456789123", false);
        from.setBankkonto(new no.nav.tjeneste.virksomhet.person.v3.informasjon.BankkontoUtland()
                .withBankkontoUtland(new BankkontonummerUtland()
                        .withBankadresse(new no.nav.tjeneste.virksomhet.person.v3.informasjon.UstrukturertAdresse().withAdresselinje1("Adresse 1"))
                        .withBankkode(BANKKODE)
                        .withBankkontonummer(KONTONUMMER)
                        .withBanknavn(BANKNAVN)
                        .withLandkode(new Landkoder().withValue(LANDKODE))
                        .withSwift(SWIFT)
                        .withValuta(new Valutaer().withValue(VALUTA)))
                .withEndretAv(ENDRET_AV)
                .withEndringstidspunkt(getMockDato(ENDRINGSTIDSPUNKT)));

        Person to = mapper.map(from, Person.class);
        BankkontoUtland bankkontoUtland = (BankkontoUtland) to.getPersonfakta().getBankkonto();

        assertThat(bankkontoUtland.getBankadresse().getAdresselinje1(), is("Adresse 1"));
        assertThat(bankkontoUtland.getBankkode(), is(BANKKODE));
        assertThat(bankkontoUtland.getKontonummer(), is(KONTONUMMER));
        assertThat(bankkontoUtland.getBanknavn(), is(BANKNAVN));
        assertThat(bankkontoUtland.getLandkode().getKodeRef(), is(LANDKODE));
        assertThat(bankkontoUtland.getSwift(), is(SWIFT));
        assertThat(bankkontoUtland.getValuta().getKodeRef(), is(VALUTA));
        assertThat(bankkontoUtland.getEndringsinformasjon().getEndretAv(), is(ENDRET_AV));
        assertThat(bankkontoUtland.getEndringsinformasjon().getSistOppdatert().toLocalDate().toString(), is(ENDRINGSTIDSPUNKT));
    }

    @Test
    public void geografiskTilknytningBydelMapping() {
        Bruker from = mockFactory.getBruker("123456789123", false);
        from.setGeografiskTilknytning(new Bydel().withGeografiskTilknytning("133337"));

        Person to = mapper.map(from, Person.class);

        assertThat(to.getPersonfakta().getGeografiskTilknytning().getType(), is(GeografiskTilknytningstyper.BYDEL));
        assertThat(to.getPersonfakta().getGeografiskTilknytning().getValue(), is("133337"));
    }

    @Test
    public void geografiskTilknytningLandMapping() {
        Bruker from = mockFactory.getBruker("123456789123", false);
        from.setGeografiskTilknytning(new Land().withGeografiskTilknytning("FIN"));

        Person to = mapper.map(from, Person.class);

        assertThat(to.getPersonfakta().getGeografiskTilknytning().getType(), is(GeografiskTilknytningstyper.LAND));
        assertThat(to.getPersonfakta().getGeografiskTilknytning().getValue(), is("FIN"));
    }

    @Test
    public void geografiskTilknytningKommuneMapping() {
        Bruker from = mockFactory.getBruker("123456789123", false);
        from.setGeografiskTilknytning(new Kommune().withGeografiskTilknytning("2080"));

        Person to = mapper.map(from, Person.class);

        assertThat(to.getPersonfakta().getGeografiskTilknytning().getType(), is(GeografiskTilknytningstyper.KOMMUNE));
        assertThat(to.getPersonfakta().getGeografiskTilknytning().getValue(), is("2080"));
    }

    @Test
    public void utenGeografiskTilknytningMapping() {
        Bruker from = mockFactory.getBruker("123456789123", false);
        from.setGeografiskTilknytning(null);

        Person to = mapper.map(from, Person.class);

        assertThat(to.getPersonfakta().getGeografiskTilknytning(), is(nullValue()));
    }

    @Test
    public void diskresjonskodeFortroligMapping() {
        Bruker from = mockFactory.getBruker("123456789123", false);
        from.setDiskresjonskode(new no.nav.tjeneste.virksomhet.person.v3.informasjon.Diskresjonskoder().withValue("SPFO"));

        Person to = mapper.map(from, Person.class);

        assertThat(to.getPersonfakta().getDiskresjonskode().getKodeRef(), is("SPFO"));
    }

    @Test
    public void utenDiskresjonskodeFortroligMapping() {
        Bruker from = mockFactory.getBruker("123456789123", false);

        Person to = mapper.map(from, Person.class);

        assertThat(to.getPersonfakta().getDiskresjonskode(), is(nullValue()));
    }

    private Personfakta getPersonfakta() {
        Personfakta personfakta = new Personfakta();
        personfakta.setAdresse(getAdresse());
        personfakta.setPostadresse(getAdresse());
        return personfakta;
    }

    private Adresse getAdresse() {
        Adresse adresse = new Adresse();
        adresse.setBolignummer("H001");
        adresse.setGatenavn("Apalveien");
        adresse.setGatenummer("1");
        adresse.setPostnummer("1234");
        adresse.setPoststednavn("Andeby");
        adresse.setTilleggsadresse("Ingen");
        Endringsinformasjon endringsinformasjon = new Endringsinformasjon();
        endringsinformasjon.setEndretAv("AdresseEndrer");
        endringsinformasjon.setSistOppdatert(LocalDateTime.now());
        adresse.setEndringsinformasjon(endringsinformasjon);
        return adresse;
    }

    private void assertEqualsPersonnavn(no.nav.tjeneste.virksomhet.person.v3.informasjon.Personnavn fromPersonnavn, Personnavn toPersonnavn) {
        assertEquals(fromPersonnavn.getFornavn(), toPersonnavn.getFornavn());
        assertEquals(fromPersonnavn.getMellomnavn(), toPersonnavn.getMellomnavn());
        assertEquals(fromPersonnavn.getEtternavn(), toPersonnavn.getEtternavn());
    }

    private void assertEqualsFamilierelasjoner(List<no.nav.tjeneste.virksomhet.person.v3.informasjon.Familierelasjon> fromRoller, List<Familierelasjon> toRoller) {
        assert (!fromRoller.isEmpty());
        assertEquals(fromRoller.size(), toRoller.size());
        no.nav.tjeneste.virksomhet.person.v3.informasjon.Familierelasjon fromFamilierelasjon = fromRoller.get(0);
        Familierelasjon toFamilierelasjon = toRoller.get(0);
        assertEquals(fromFamilierelasjon.getTilRolle().getKodeRef(), toFamilierelasjon.getTilRolle());
        assertEquals(((PersonIdent) fromFamilierelasjon.getTilPerson().getAktoer()).getIdent().getIdent(), toFamilierelasjon.getTilPerson().getFodselsnummer().getNummer());
        assertTrue(toFamilierelasjon.getHarSammeBosted());
        assertEqualsPersonnavn(fromFamilierelasjon.getTilPerson().getPersonnavn(), toFamilierelasjon.getTilPerson().getPersonfakta().getPersonnavn());
    }

    private void assertEqualsBankkontoUtland(BankkontonummerUtland from, BankkontoUtland to) {
        assertEquals(from.getBankkontonummer(), to.getKontonummer());
        assertEquals(from.getBankkode(), to.getBankkode());
        assertEquals(from.getSwift(), to.getSwift());
    }

    private void assertEqualsGateadresse(Gateadresse from, Adresse to) {
        assertEquals(from.getBolignummer(), to.getBolignummer());
        assertEquals(from.getGatenavn(), to.getGatenavn());
        assertEquals(from.getHusnummer().toString(), to.getGatenummer());
        assertEquals(from.getPoststed().getValue(), to.getPostnummer());
        assertEquals(from.getTilleggsadresseType() + " " + from.getTilleggsadresse(), to.getTilleggsadresseMedType());
    }

    private static XMLGregorianCalendar getMockDato(String date) {
        GregorianCalendar cal = GregorianCalendar.from((java.time.LocalDate.parse(date).atStartOfDay(ZoneId.systemDefault())));
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
