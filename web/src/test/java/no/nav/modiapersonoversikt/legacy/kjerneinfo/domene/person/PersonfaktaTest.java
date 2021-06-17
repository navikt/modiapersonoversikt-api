package no.nav.modiapersonoversikt.legacy.kjerneinfo.domene.person;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Kodeverdi;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.info.Bankkonto;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.info.BankkontoUtland;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.*;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.fakta.Familierelasjon;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.fakta.Familierelasjonstype;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.fakta.Telefon;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domene.factory.PersonDoFactory;
import org.hamcrest.CoreMatchers;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class PersonfaktaTest {

    private Personfakta personfakta;
    private Personfakta emptyPersonfakta;
    private String BOLIGNUMMER = "2a";
    private String GATENAVN = "gatenavn";
    private String GATENUMMER = "3";
    private int ID = 3456;
    private String POSTNUMMER = "4620";
    private String POSTSTED = "Kristiansand";
    private String POSTADRESSE = "postadresse";
    private LocalDate FOM = LocalDate.fromDateFields(new Date(34535433));
    private LocalDate TOM = LocalDate.fromDateFields(new Date(34242563));
    private String ADRLINJE1 = "adrlinje1";
    private String ADRLINJE2 = "adrlinje2";
    private String ADRLINJE3 = "adrlinje3";
    private String BANKNAVN = "banknavn1";
    private String KONTONUMMER = "kontnr1";
    private Kodeverdi BOSTATUS = new Kodeverdi.With().kodeRef("bostatus1").done();
    private String DISKRESJONSKODE = "diskresjonskode";
    private String GJELDMEDLSTATUS = "gjeldendeMedlemskapstatus";
    private String ENHET = "enhet";
    private String FODESTED = "fodested";
    private String KJONN = "kjonn";
    private int PERSONFAKTAID = 26475;
    private String FORNAVN = "fornavn";
    private String MELLOMNAVN = "mellomnavn";
    private String ETTERNAVN = "etternavn";
    private int PERSONNAVNID = 5678;
    private LocalDateTime ENDRET = LocalDateTime.fromDateFields(new Date(34242888));
    private String BANKKODE = "bankkode";
    private String LANDKODE = "landkode";
    private String SWIFT = "swift";
    private String VALUTA = "aud";
    private Kodeverdi SIVILSTAND_GIFT = new Kodeverdi.With().kodeRef("GIFT").done();
	private String TIL_ROLLE_GIFT = "GIFT";
	private String SIKKERHETSTILTAK_BESKRIVELSE = "Farlig person";
    private String SIKKERHETSTILTAK_KODE = "FPE";

    @Before
    public void setUp() {
        personfakta = PersonDoFactory.createPersonfakta(BOLIGNUMMER, GATENAVN, GATENUMMER, ID,
                POSTNUMMER, POSTSTED, POSTADRESSE, FOM, TOM, ADRLINJE1, ADRLINJE2, ADRLINJE3,
                BANKNAVN, KONTONUMMER, BOSTATUS, DISKRESJONSKODE, GJELDMEDLSTATUS,
                ENHET, FODESTED, KJONN, PERSONFAKTAID, FORNAVN, MELLOMNAVN, ETTERNAVN,
                PERSONNAVNID, ENDRET, BANKKODE, LANDKODE, SWIFT,
                VALUTA, SIVILSTAND_GIFT, TIL_ROLLE_GIFT, SIKKERHETSTILTAK_BESKRIVELSE, SIKKERHETSTILTAK_KODE);

        emptyPersonfakta = new Personfakta();
    }

    @Test
    public void sjekkCreatePersonfakta() {
        assertEquals("Personfakta [personfaktaId=" + PERSONFAKTAID + ", personnavn="
                + FORNAVN + " " + MELLOMNAVN + " " + ETTERNAVN + ", sivilstand=" + SIVILSTAND_GIFT.getKodeRef()
                + ", adresse=" + "Adresse [gatenavn=" + GATENAVN + ", postnummer=" + POSTNUMMER
                + ", poststed=" + POSTSTED + "]" + "]", personfakta.toString());

        personfakta.getPersonnavn().setMellomnavn(null);
        assertEquals("Personfakta [personfaktaId=" + PERSONFAKTAID + ", personnavn="
                + FORNAVN + " " + ETTERNAVN + ", sivilstand=" + SIVILSTAND_GIFT.getKodeRef()
                + ", adresse=" + "Adresse [gatenavn=" + GATENAVN + ", postnummer=" + POSTNUMMER
                + ", poststed=" + POSTSTED + "]" + "]", personfakta.toString());
    }

    @Test
    public void sjekkNorskBankkonto() {
        Bankkonto bankkonto = new Bankkonto();
        emptyPersonfakta.setBankkonto(bankkonto);
        assert (emptyPersonfakta.isBankkontoINorge());
        bankkonto = new BankkontoUtland();
        emptyPersonfakta.setBankkonto(bankkonto);
        assertFalse(emptyPersonfakta.isBankkontoINorge());
    }

    @Test
    public void sjekkOmLever() {
        assertFalse(personfakta.isDoed());
        assertFalse(emptyPersonfakta.isDoed());
        emptyPersonfakta.setDoedsdato(null);
        assertFalse(emptyPersonfakta.isDoed());
        emptyPersonfakta.setDoedsdato(new LocalDateTime());
        assert (emptyPersonfakta.isDoed());
    }

    @Test
    public void sjekkDiskresjonskode() {
        assertFalse(emptyPersonfakta.isHarDiskresjonskode());
        assert (personfakta.isHarDiskresjonskode());
    }

    @Test
    public void sjekkTomDiskresjonskodeHarDiskresjonskode6Eller7() {
        emptyPersonfakta.setDiskresjonskode(new Kodeverdi(null, null));

        assertThat(emptyPersonfakta.isHarDiskresjonskode6Eller7(), is (false));
    }

    @Test
    public void diskresjonskodeSPFOGirRiktigKode() {
        emptyPersonfakta.setDiskresjonskode(new Kodeverdi("SPFO", "Sperret adresse, fortrolig"));

        assertThat(emptyPersonfakta.getDiskresjonskodeBeskrivelse(), is("7"));
    }

    @Test
    public void diskresjonskodeSPSFGirRiktigKode() {
        emptyPersonfakta.setDiskresjonskode(new Kodeverdi("SPSF", "Sperret adresse, strengt fortrolig"));

        assertThat(emptyPersonfakta.getDiskresjonskodeBeskrivelse(), is("6"));
    }

    @Test
    public void alternativMatrikkelAdresseRegnesSomNorsk() {
        Personfakta personfakta = new Personfakta();
        personfakta.setAlternativAdresse(new Matrikkeladresse());

        assertThat("Matrikkeladresse skal føre til at personfakta.isAlternativAdresseINorge() returnerer true", personfakta.isAlternativAdresseINorge(), CoreMatchers.is(true));
        assertThat("Matrikkeladresse skal føre til at personfakta.isAlternativAdresseIUtland() returnerer false", personfakta.isAlternativAdresseIUtland(), CoreMatchers.is(false));
    }

    @Test
    public void alternativPostboksAdresseRegnesSomNorsk() {
        Personfakta personfakta = new Personfakta();
        personfakta.setAlternativAdresse(new Postboksadresse());

        assertThat("Postboksadresse skal føre til at personfakta.isAlternativAdresseINorge() returnerer true", personfakta.isAlternativAdresseINorge(), CoreMatchers.is(true));
        assertThat("Postboksadresse skal føre til at personfakta.isAlternativAdresseIUtland() returnerer false", personfakta.isAlternativAdresseIUtland(), CoreMatchers.is(false));
    }

    @Test
    public void alternativAdresseRegnesSomNorsk() {
        Personfakta personfakta = new Personfakta();
        personfakta.setAlternativAdresse(new Adresse());

        assertThat("Adresse skal føre til at personfakta.isAlternativAdresseINorge() returnerer true", personfakta.isAlternativAdresseINorge(), CoreMatchers.is(true));
        assertThat("Adresse skal føre til at personfakta.isAlternativAdresseIUtland() returnerer false", personfakta.isAlternativAdresseIUtland(), CoreMatchers.is(false));
    }

    @Test
    public void alternativAdresseUtlandRegnesSomUtland() {
        Personfakta personfakta = new Personfakta();
        personfakta.setAlternativAdresse(new AlternativAdresseUtland());

        assertThat("AlternativAdresseUtland skal føre til at personfakta.isAlternativAdresseINorge() returnerer false", personfakta.isAlternativAdresseINorge(), CoreMatchers.is(false));
        assertThat("AlternativAdresseUtland skal føre til at personfakta.isAlternativAdresseIUtland() returnerer true", personfakta.isAlternativAdresseIUtland(), CoreMatchers.is(true));
    }

    @Test
    public void tomAlternativAdresseRegnesSomIkkeNorsk() {
        Personfakta personfakta = new Personfakta();

        assertThat("Tom alternativ adresse skal føre til at personfakta.isAlternativAdresseINorge() returnerer false", personfakta.isAlternativAdresseINorge(), CoreMatchers.is(false));
        assertThat("Tom alternativ adresse skal føre til at personfakta.isAlternativAdresseIUtland() returnerer false", personfakta.isAlternativAdresseIUtland(), CoreMatchers.is(false));
    }

    @Test
    public void sjekkFamilierelasjoner() {
        assertEquals(0, personfakta.getBarn().size());
        assertEquals(0, personfakta.getAntallBarn());
        assertNull(personfakta.getFamilierelasjon(Familierelasjonstype.SAMBOER));

        // familierelasjon er lagt inn men uten tilPerson som eneste, dvs element 0
        Person giftMed = new Person();
        personfakta.getHarFraRolleIList().get(0).setTilPerson(giftMed);
        assertEquals(giftMed, personfakta.getFamilierelasjon(Familierelasjonstype.GIFT));

        Familierelasjon rel1 = new Familierelasjon();
        rel1.setTilRolle(Familierelasjonstype.BARN.name());
        rel1.setTilPerson(new Person.With().fodselsnummer("01011050081").done());

        Familierelasjon rel2 = new Familierelasjon();
        rel2.setTilRolle(Familierelasjonstype.BARN.name());
        rel2.setTilPerson(new Person.With().fodselsnummer("01011050243").done());

        Familierelasjon rel3 = new Familierelasjon();
        rel3.setTilRolle(Familierelasjonstype.GIFT.name());
        rel3.setTilPerson(giftMed);

        assertNotNull(emptyPersonfakta.getHarFraRolleIList());

        List<Familierelasjon> relasjoner = new ArrayList<>();
        relasjoner.add(rel1);
        relasjoner.add(rel2);
        relasjoner.add(rel3);
        emptyPersonfakta.setHarFraRolleIList(relasjoner);

        assertEquals(2, emptyPersonfakta.getAntallBarn());
        assertEquals(giftMed, emptyPersonfakta.getFamilierelasjon(Familierelasjonstype.GIFT));
        assertEquals(2, emptyPersonfakta.getBarn().size());
    }

	@Test
	public void sjekkHarSikkerhetstiltak() {
		assertTrue(personfakta.isHarSikkerhetstiltak());
		assertEquals(personfakta.getSikkerhetstiltak().getSikkerhetstiltaksbeskrivelse(), SIKKERHETSTILTAK_BESKRIVELSE);
		assertEquals(personfakta.getSikkerhetstiltak().getSikkerhetstiltakskode(), SIKKERHETSTILTAK_KODE);
	}

	@Test
	public void sjekkHarIkkeSikkerhetstiltak() {
		assertFalse(emptyPersonfakta.isHarSikkerhetstiltak());
	}

    @Test
    public void sjekkSortBarnByAge() {
        List<Familierelasjon> barn = new ArrayList<>();
        Familierelasjon r1 = new Familierelasjon();
        r1.setTilRolle(Familierelasjonstype.BARN.name());
        r1.setTilPerson(new Person.With().fodselsnummer("01011050081").done());
        Familierelasjon r2 = new Familierelasjon();
        r2.setTilRolle(Familierelasjonstype.BARN.name());
        r2.setTilPerson(new Person.With().fodselsnummer("01011150264").done());
        barn.add(r1);
        barn.add(r2);
        emptyPersonfakta.setHarFraRolleIList(barn);
        assertEquals(2, emptyPersonfakta.getBarn().size());

        assertEquals(r1, emptyPersonfakta.getBarn().get(1));
        assertEquals(r2, emptyPersonfakta.getBarn().get(0));
    }

    @Test
    public void getBarn() {
        List<Familierelasjon> familierelasjonList = new ArrayList<>();
        Familierelasjon ektefelle = new Familierelasjon();
        ektefelle.setTilRolle(Familierelasjonstype.EKTE.name());
        ektefelle.setTilPerson(new Person.With().fodselsnummer("99063375483").done());
        Familierelasjon barnUtenPerson = new Familierelasjon();
        barnUtenPerson.setTilRolle(Familierelasjonstype.BARN.name());
        Familierelasjon riktigBarn = new Familierelasjon();
        riktigBarn.setTilRolle(Familierelasjonstype.BARN.name());
        riktigBarn.setTilPerson(new Person.With().fodselsnummer("01011050243").done());
        Familierelasjon riktigBarn2 = new Familierelasjon();
        riktigBarn2.setTilRolle(Familierelasjonstype.BARN.name());
        riktigBarn2.setTilPerson(new Person.With().fodselsnummer("01011050081").done());
        Familierelasjon barnUtenFoedselsnummer = new Familierelasjon();
        barnUtenFoedselsnummer.setTilRolle(Familierelasjonstype.BARN.name());
        barnUtenFoedselsnummer.setTilPerson(new Person());
        Familierelasjon barnOver21 = new Familierelasjon();
        barnOver21.setTilRolle(Familierelasjonstype.BARN.name());
        barnOver21.setTilPerson(new Person.With().fodselsnummer("10108000398").done());
        familierelasjonList.add(ektefelle);
        familierelasjonList.add(barnUtenPerson);
        familierelasjonList.add(riktigBarn);
        familierelasjonList.add(riktigBarn2);
        familierelasjonList.add(barnUtenFoedselsnummer);
        familierelasjonList.add(barnOver21);
        emptyPersonfakta.setHarFraRolleIList(familierelasjonList);
        assertEquals(emptyPersonfakta.getBarn().size(), 2);
    }

    @Test
    public void testharTilrettelagtKommunikasjon() {
        assertFalse(emptyPersonfakta.isHarTilrettelagtKommunikasjon());

        emptyPersonfakta.getTilrettelagtKommunikasjon().add(new Kodeverdi());

        assertTrue(emptyPersonfakta.isHarTilrettelagtKommunikasjon());
    }

    @Test
    public void borMed() {
        borMed(getFamilierelasjon(Familierelasjonstype.EKTE), true);
        borMed(getFamilierelasjon(Familierelasjonstype.EKTE), false);
        borMed(getFamilierelasjon(Familierelasjonstype.SAMBOER), true);
        borMed(getFamilierelasjon(Familierelasjonstype.SAMBOER), false);
        borMed(getFamilierelasjon(Familierelasjonstype.GIFT), true);
        borMed(getFamilierelasjon(Familierelasjonstype.GIFT), false);
        borMed(getFamilierelasjon(Familierelasjonstype.BARN), false);
        borMedAndre(getFamilierelasjon(Familierelasjonstype.BARN), true);

    }

    @Test
    public void isDoedDoedsdato() {
        // Ingen dødsdato
        assertThat(emptyPersonfakta.isDoed(), equalTo(false));

        // Med dødsdato
        emptyPersonfakta.setDoedsdato(LocalDateTime.now());
        assertThat(emptyPersonfakta.isDoed(), equalTo(true));
    }

    @Test
    public void isDoedBostatus() {
        // Ingen dødsdato og bostatus ikke død
        emptyPersonfakta.setBostatus(new Kodeverdi("bostatus", "notDead"));
        assertThat(emptyPersonfakta.isDoed(), equalTo(false));

        // Ingen dødsdato og bostatus ikke død
        emptyPersonfakta.setBostatus(new Kodeverdi("DØD", "Død"));
        assertThat(emptyPersonfakta.isDoed(), equalTo(true));
    }

    @Test
    public void sjekkMobilTelefon() {
        String nummer = "99554433";
        Personfakta personfakta = lagPersonfakta(lagTelefon(nummer, "MOBI"));

        assertThat(personfakta.getMobil().get().getIdentifikator(), is("99554433"));
        assertTrue(personfakta.harTelefonnummer());
    }

    @Test
    public void sjekkTelefonArbeid() {
        String nummer = "99554433";
        Personfakta personfakta = lagPersonfakta(lagTelefon(nummer, "ARBT"));

        assertThat(personfakta.getJobbTlf().get().getIdentifikator(), is("99554433"));
        assertTrue(personfakta.harTelefonnummer());
    }

    @Test
    public void sjekkTelefonHjem() {
        String nummer = "99554433";
        Personfakta personfakta = lagPersonfakta(lagTelefon(nummer, "HJET"));

        assertThat(personfakta.getHjemTlf().get().getIdentifikator(), is("99554433"));
        assertTrue(personfakta.harTelefonnummer());
    }

    @Test
    public void personUtenTelefon() {
        assertFalse(emptyPersonfakta.harTelefonnummer());
    }

    @Test
    public void borMedPartner() {
        Familierelasjon familierelasjon = getFamilierelasjon(Familierelasjonstype.EKTE);
        familierelasjon.setHarSammeBosted(true);
        List<Familierelasjon> familierelasjonList = new ArrayList<>();
        familierelasjonList.add(familierelasjon);
        emptyPersonfakta.setHarFraRolleIList(familierelasjonList);
        String borMedPartner = emptyPersonfakta.getBorMedPartner();
        assertThat(borMedPartner, equalTo("Ja"));
    }

    private Personfakta lagPersonfakta(Telefon... telefoner ) {
        List<Telefon> kontaktinformasjon = Arrays.stream(telefoner).collect(Collectors.toList());
        Personfakta personfakta = new Personfakta();
        personfakta.setKontaktinformasjon(kontaktinformasjon);
        return personfakta;
    }

    private Telefon lagTelefon(String identifikator, String value) {
        return new Telefon()
                .withIdentifikator(identifikator)
                .withType(new Kodeverdi(value, null));
    }

    private void borMed(Familierelasjon familierelasjon, boolean borMed) {
        List<Familierelasjon> familierelasjonList = new ArrayList<>();
        familierelasjon.setHarSammeBosted(borMed);
        familierelasjonList.add(familierelasjon);
        emptyPersonfakta.setHarFraRolleIList(familierelasjonList);
        Familierelasjon borMedFamilierelasjon = emptyPersonfakta.getBorMed();
        if (borMed) {
            assertThat(borMedFamilierelasjon.getTilRolle(), equalTo(familierelasjon.getTilRolle()));
        } else {
            assertNull(borMedFamilierelasjon);
        }

    }

    private void borMedAndre(Familierelasjon familierelasjon, boolean borMed) {
        List<Familierelasjon> familierelasjonList = new ArrayList<>();
        familierelasjon.setHarSammeBosted(borMed);
        familierelasjonList.add(familierelasjon);
        emptyPersonfakta.setHarFraRolleIList(familierelasjonList);
        Familierelasjon borMedFamilierelasjon = emptyPersonfakta.getBorMed();
        assertNull(borMedFamilierelasjon);
    }

    private Familierelasjon getFamilierelasjon(Familierelasjonstype familierelasjonstype) {
        Familierelasjon ektefelle = new Familierelasjon();
        ektefelle.setTilRolle(familierelasjonstype.name());
        ektefelle.setTilPerson(new Person.With().fodselsnummer("99063375483").done());
        ektefelle.setHarSammeBosted(true);
        return ektefelle;
    }
}
