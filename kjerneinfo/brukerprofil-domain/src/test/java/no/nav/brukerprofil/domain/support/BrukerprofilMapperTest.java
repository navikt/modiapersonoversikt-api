package no.nav.brukerprofil.domain.support;

import no.nav.brukerprofil.domain.BankkontoUtland;
import no.nav.brukerprofil.domain.Bruker;
import no.nav.brukerprofil.domain.adresser.Gateadresse;
import no.nav.brukerprofil.domain.adresser.Matrikkeladresse;
import no.nav.brukerprofil.domain.adresser.UstrukturertAdresse;
import no.nav.kjerneinfo.common.domain.Kodeverdi;
import no.nav.kjerneinfo.common.domain.Periode;
import no.nav.kjerneinfo.domain.info.Bankkonto;
import no.nav.kjerneinfo.domain.person.*;
import no.nav.kjerneinfo.domain.person.fakta.Telefon;
import org.joda.time.LocalDateTime;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BrukerprofilMapperTest {

    private static final String GJELDENDE_TYPE = "M";
    private static final String MOBILNUMMER = "55441122";
    private static final String HJEMNUMMER = "55662233";
    private static final String POSTBOKSNUMMER = "13337";
    private static final LocalDateTime ENDRETTIDSPUNKT = new LocalDateTime().withYear(2012).withMonthOfYear(12).withDayOfMonth(6);
    private static final LocalDateTime GYLDIG_TIL = new LocalDateTime().withYear(2022).withMonthOfYear(12).withDayOfMonth(6);

    private final String ADRESSELINJE1 = "Dovregubbens Hall";
    private final String ADRESSELINJE2 = "Dovre";
    private final String ADRESSELINJE3 = "Norge";
    private final String ADRESSELINJE4 = "Verden";
    private final String SIST_OPPDATERT = "2015-01-01";
    private final String TILRETTELAGT_KOMMUNIKASJON_BESKRIVELSE = "Tolkehjelp";
    private final String TILRETTELAGT_KOMMUNIKASJON_KODE = "TOHJ";
    private final String FODSELSNUMMER = "10108000398";
    private final String JOBBNUMMER = "88774455";
    private final String KONTONUMMER = "1644 1241 14134";
    private final String BANKNAVN = "Den Norske Bank";
    private final String UTENLANDSK_BANKADRESSE = "New York Stock Exchange";
    private final String UTENLANDSK_BANKNAVN = "Chase";
    private final String BANKKODE = "KODE";
    private final String SWIFT_KODE = "SWIFT";
    private final String VALUTA = "USD";
    private final String POSTSTEDSNAVN = "Oslo";
    private final String POSTNUMMER = "0477";
    private final String BOLIGNUMMER = "70";
    private final String HUSBOKSTAV = "A";
    private final String GATENUMMER = "66";
    private final String GATENAVN = "Sandakerveien";
    private final String TILLEGGSADRESSE = "Mitt tillegg";
    private final String TILLEGGSADRESSE_TYPE = "c/o";
    private final String GAARDSNUMMER = "00055";
    private final String BRUKSNUMMER = "1155";
    private final String FESTENUMMER = "2255";
    private final String SEKSJONSNUMMER = "3355";
    private final String UNDERNUMMER = "4455";
    private final String EIENDOMSNAVN = "Åslia";
    private final String LANDKODE = "NOR";
    private final String ENDRET_AV = "TOM JONES";
    private final String POSTBOKSANLEGG = "Bergen postkontor";

    private BrukerprofilMapper mapper = BrukerprofilMapper.getInstance();

    @Ignore
    @Test
    // Used to provoke an OOM caused by Orikas MapperFactory.
    // Must be ran with these Metaspace settings:
    // -XX:MetaspaceSize=80m
    // -XX:MaxMetaspaceSize=100m
    // -XX:MaxMetaspaceFreeRatio=80
    // -XX:MinMetaspaceFreeRatio=40
    public void testOOMCausedByOrikasMapper() {
        BrukerprofilMapper mapper = null;

        for (int i = 0; i <= 10000; i++) {
            // Disabled now that BrukerprofilMapper is a Singleton
            //mapper = new BrukerprofilMapper();
        }
    }

    @Test
    public void testNullAdresse() {
        Person person = lagMockPerson();

        Bruker bruker = mapper.map(person, Bruker.class);

        assertNull(bruker.getMidlertidigadresseNorge());
        assertNull(bruker.getBostedsadresse());
        assertNull(bruker.getMidlertidigadresseUtland());
        assertNull(bruker.getPostadresse());
    }

    @Test
    public void testNullBankkonto() {
        Person person = lagMockPerson();

        Bruker bruker = mapper.map(person, Bruker.class);

        assertNull(bruker.getBankkonto());
    }

    private Person lagMockPerson() {
        Person person = new Person();
        person.setFodselsnummer(new Fodselsnummer(FODSELSNUMMER));
        Personfakta personfakta = new Personfakta();
        Personnavn personnavn = new Personnavn();
        personnavn.setFornavn("Test");
        personnavn.setMellomnavn("");
        personnavn.setEtternavn("Testersen");
        personfakta.setPersonnavn(personnavn);
        person.setPersonfakta(personfakta);
        return person;
    }

    @Test
    public void mapPerson() {
        Person person = lagMockPerson();

        Bruker to = mapper.map(person, Bruker.class);

        assertThat(to.getIdent(), is(FODSELSNUMMER));
    }

    @Test
    public void mapGateAdresse() {
        Person person = lagMockPerson();
        person.getPersonfakta().setBostedsadresse(lagAdresse());

        Bruker to = mapper.map(person, Bruker.class);
        Gateadresse gateadresse = (Gateadresse) to.getBostedsadresse();

        assertThat(gateadresse.getTilleggsadresseType(), is(TILLEGGSADRESSE_TYPE));
        assertThat(gateadresse.getTilleggsadresse(), is(TILLEGGSADRESSE));
        assertThat(gateadresse.getPostleveringsPeriode().getTo(), is(GYLDIG_TIL.toLocalDate()));
        assertThat(gateadresse.getEndretAv(), is(ENDRET_AV));
        assertThat(gateadresse.getEndringstidspunkt(), is(ENDRETTIDSPUNKT));
        assertThat(gateadresse.getPoststedsnavn(), is(POSTSTEDSNAVN));
        assertThat(gateadresse.getPoststed(), is(POSTNUMMER));

        assertThat(gateadresse.getGatenavn(), is(GATENAVN));
        assertThat(gateadresse.getHusnummer(), is(GATENUMMER));
        assertThat(gateadresse.getHusbokstav(), is(HUSBOKSTAV));
        assertThat(gateadresse.getBolignummer(), is(BOLIGNUMMER));
    }

    @Test
    public void mapMatrikkeladresse() {
        Person person = lagMockPerson();
        person.getPersonfakta().setBostedsadresse(lagMatrikkelAdresse());

        Bruker to = mapper.map(person, Bruker.class);
        Matrikkeladresse matrikkeladresse = (Matrikkeladresse) to.getBostedsadresse();

        assertThat(matrikkeladresse.getTilleggsadresseType(), is(TILLEGGSADRESSE_TYPE));
        assertThat(matrikkeladresse.getTilleggsadresse(), is(TILLEGGSADRESSE));
        assertThat(matrikkeladresse.getPostleveringsPeriode().getTo(), is(GYLDIG_TIL.toLocalDate()));
        assertThat(matrikkeladresse.getEndretAv(), is(ENDRET_AV));
        assertThat(matrikkeladresse.getEndringstidspunkt(), is(ENDRETTIDSPUNKT));
        assertThat(matrikkeladresse.getPoststedsnavn(), is(POSTSTEDSNAVN));
        assertThat(matrikkeladresse.getPoststed(), is(POSTNUMMER));

        assertThat(matrikkeladresse.getEiendomsnavn(), is(EIENDOMSNAVN));
        assertThat("Gårdsnummer er ikke likt", matrikkeladresse.getGaardsnummer(), is(GAARDSNUMMER));
        assertThat("Bruksnummer er ikke likt", matrikkeladresse.getBruksnummer(), is(BRUKSNUMMER));
        assertThat("Festenummer er ikke likt", matrikkeladresse.getFestenummer(), is(FESTENUMMER));
        assertThat("Seksjonsnummer er ikke likt", matrikkeladresse.getSeksjonsnummer(), is(SEKSJONSNUMMER));
        assertThat("Undernummer er ikke likt", matrikkeladresse.getUndernummer(), is(UNDERNUMMER));
    }

    @Test
    public void mapPostadresse() {
        Person person = lagMockPerson();
        person.getPersonfakta().setPostadresse(lagPostadresse());
        person.getPersonfakta().setGjeldendePostadressetype(new Kodeverdi(GJELDENDE_TYPE, GJELDENDE_TYPE));

        Bruker to = mapper.map(person, Bruker.class);

        assertThat(to.getPostadresse().getAdresselinje1(), is(ADRESSELINJE1));
        assertThat(to.getPostadresse().getAdresselinje2(), is(ADRESSELINJE2));
        assertThat(to.getPostadresse().getAdresselinje3(), is(ADRESSELINJE3));
        assertThat(to.getPostadresse().getAdresselinje4(), is(ADRESSELINJE4));
        assertThat(to.getPostadresse().getEndretAv(), is(ENDRET_AV));
        assertThat(to.getPostadresse().getEndringstidspunkt(), is(LocalDateTime.parse(SIST_OPPDATERT)));
        assertThat(to.getGjeldendePostadresseType().getKodeRef(), is(GJELDENDE_TYPE));
    }

    @Test
    public void mapMidlertidigAdresseNorge() {
        Person person = lagMockPerson();
        person.getPersonfakta().setAlternativAdresse(lagAdresse());

        Bruker to = mapper.map(person, Bruker.class);
        Gateadresse midlertidigAdresse = (Gateadresse) to.getMidlertidigadresseNorge();

        assertThat(midlertidigAdresse.getEndretAv(), is(ENDRET_AV));
        assertThat(midlertidigAdresse.getEndringstidspunkt(), is(ENDRETTIDSPUNKT));
        assertThat(midlertidigAdresse.getPoststedsnavn(), is(POSTSTEDSNAVN));
        assertThat(midlertidigAdresse.getPoststed(), is(POSTNUMMER));
        assertThat(midlertidigAdresse.getBolignummer(), is(BOLIGNUMMER));
        assertThat(midlertidigAdresse.getHusbokstav(), is(HUSBOKSTAV));
        assertThat(midlertidigAdresse.getHusnummer(), is(GATENUMMER));
        assertThat(midlertidigAdresse.getGatenavn(), is(GATENAVN));
        assertThat(midlertidigAdresse.getPostleveringsPeriode().getTo(), is(GYLDIG_TIL.toLocalDate()));
    }

    @Test
    public void mapMidlertidigAdresseUtland() {
        Person person = lagMockPerson();
        person.getPersonfakta().setAlternativAdresse(lagAlternativAdresseUtland());

        Bruker to = mapper.map(person, Bruker.class);
        UstrukturertAdresse midlertidigAdresse = to.getMidlertidigadresseUtland();

        assertThat(midlertidigAdresse.getEndretAv(), is(ENDRET_AV));
        assertThat(midlertidigAdresse.getEndringstidspunkt().toLocalDate().toString(), is(SIST_OPPDATERT));
        assertThat(midlertidigAdresse.getPostleveringsPeriode().getTo(), is(GYLDIG_TIL.toLocalDate()));
        assertThat(midlertidigAdresse.getAdresselinje1(), is(ADRESSELINJE1));
        assertThat(midlertidigAdresse.getAdresselinje2(), is(ADRESSELINJE2));
        assertThat(midlertidigAdresse.getAdresselinje3(), is(ADRESSELINJE3));
        assertThat(midlertidigAdresse.getAdresselinje4(), is(ADRESSELINJE4));
        assertThat(midlertidigAdresse.getLandkode().getKodeRef(), is(LANDKODE));
    }

    @Test
    public void mapMidlertidigMatrikkeladresse() {
        Person person = lagMockPerson();
        person.getPersonfakta().setAlternativAdresse(lagMatrikkelAdresse());

        Bruker to = mapper.map(person, Bruker.class);
        Matrikkeladresse midlertidigAdresse = (Matrikkeladresse) to.getMidlertidigadresseNorge();

        assertThat(midlertidigAdresse.getTilleggsadresse(), is(TILLEGGSADRESSE));
        assertThat(midlertidigAdresse.getEiendomsnavn(), is(EIENDOMSNAVN));
        assertThat(midlertidigAdresse.getPoststed(), is(POSTNUMMER));
        assertThat(midlertidigAdresse.getEndretAv(), is(ENDRET_AV));
        assertThat(midlertidigAdresse.getEndringstidspunkt().toLocalDate(), is(ENDRETTIDSPUNKT.toLocalDate()));
        assertThat(midlertidigAdresse.getPostleveringsPeriode().getTo(), is(GYLDIG_TIL.toLocalDate()));
    }

    @Test
    public void mapPostboksAdresse() {
        Person person = lagMockPerson();
        person.getPersonfakta().setAlternativAdresse(lagPostboksAdresse());

        Bruker to = mapper.map(person, Bruker.class);
        no.nav.brukerprofil.domain.adresser.Postboksadresse postboksadresse = (no.nav.brukerprofil.domain.adresser.Postboksadresse) to.getMidlertidigadresseNorge();

        assertThat(postboksadresse.getTilleggsadresseType(), is(TILLEGGSADRESSE_TYPE));
        assertThat(postboksadresse.getTilleggsadresse(), is(TILLEGGSADRESSE));
        assertThat(postboksadresse.getPostleveringsPeriode().getTo(), is(GYLDIG_TIL.toLocalDate()));
        assertThat(postboksadresse.getEndringstidspunkt().toLocalDate().toString(), is(SIST_OPPDATERT));
        assertThat(postboksadresse.getEndretAv(), is(ENDRET_AV));
        assertThat(postboksadresse.getPoststedsnavn(), is(POSTSTEDSNAVN));
        assertThat(postboksadresse.getPoststed(), is(POSTNUMMER));

        assertThat(postboksadresse.getPostboksnummer(), is(POSTBOKSNUMMER));
        assertThat(postboksadresse.getPostboksanlegg(), is(POSTBOKSANLEGG));
    }

    @Test
    public void mapTilrettelagtKommunikasjon() {
        Person person = lagMockPerson();
        List<Kodeverdi> tilrettelagtKommunikasjon = new ArrayList<>();
        tilrettelagtKommunikasjon.add(new Kodeverdi(TILRETTELAGT_KOMMUNIKASJON_KODE, TILRETTELAGT_KOMMUNIKASJON_BESKRIVELSE));
        person.getPersonfakta().setTilrettelagtKommunikasjon(tilrettelagtKommunikasjon);

        Bruker to = mapper.map(person, Bruker.class);

        assertThat(to.getTilrettelagtKommunikasjon().size(), is(1));
        assertThat(to.getTilrettelagtKommunikasjon().get(0).getBeskrivelse(), is(TILRETTELAGT_KOMMUNIKASJON_BESKRIVELSE));
        assertThat(to.getTilrettelagtKommunikasjon().get(0).getKodeRef(), is(TILRETTELAGT_KOMMUNIKASJON_KODE));
    }

    @Test
    public void mapNavn() {
        Person person = lagMockPerson();
        person.getPersonfakta().getPersonnavn().setFornavn("LOREM");
        person.getPersonfakta().getPersonnavn().setMellomnavn("IPSUM");
        person.getPersonfakta().getPersonnavn().setEtternavn("BAR");
        Bruker to = mapper.map(person, Bruker.class);

        assertThat(to.getFornavn().getNavn(), is("LOREM"));
        assertThat(to.getMellomnavn().getNavn(), is("IPSUM"));
        assertThat(to.getEtternavn().getNavn(), is("BAR"));
    }

    @Test
    public void mapTelefon() {
        Person person = lagMockPerson();
        List<Telefon> telefon = new ArrayList<>();
        telefon.add(new Telefon().withIdentifikator(MOBILNUMMER).withType(new Kodeverdi("MOBI", "MOBI")));
        telefon.add(new Telefon().withIdentifikator(JOBBNUMMER).withType(new Kodeverdi("ARBT", "ARBT")));
        telefon.add(new Telefon().withIdentifikator(HJEMNUMMER).withType(new Kodeverdi("HJET", "HJET")));
        person.getPersonfakta().setKontaktinformasjon(telefon);

        Bruker to = mapper.map(person, Bruker.class);

        assertThat(to.getMobil().getIdentifikator(), is(MOBILNUMMER));
        assertThat(to.getJobbTlf().getIdentifikator(), is(JOBBNUMMER));
        assertThat(to.getHjemTlf().getIdentifikator(), is(HJEMNUMMER));
    }

    @Test
    public void mapNorskBankkonto() {
        Person person = lagMockPerson();
        Bankkonto bankkonto = new Bankkonto();
        bankkonto.setKontonummer(KONTONUMMER);
        bankkonto.setBanknavn(BANKNAVN);
        Endringsinformasjon endringsinformasjon = new Endringsinformasjon();
        endringsinformasjon.setSistOppdatert(LocalDateTime.parse(SIST_OPPDATERT));
        endringsinformasjon.setEndretAv(ENDRET_AV);
        bankkonto.setEndringsinformasjon(endringsinformasjon);
        person.getPersonfakta().setBankkonto(bankkonto);

        Bruker to = mapper.map(person, Bruker.class);

        assertThat(to.getBankkonto().getBanknavn(), is(BANKNAVN));
        assertThat(to.getBankkonto().getKontonummer(), is(KONTONUMMER));
        assertThat(to.getBankkonto().getEndretAv(), is(ENDRET_AV));
        assertThat(to.getBankkonto().getEndringstidspunkt().toLocalDate().toString(), is(SIST_OPPDATERT));
    }

    @Test
    public void mapUtenlandskBankkonto() {
        no.nav.kjerneinfo.domain.info.BankkontoUtland bankkontoUtland = new no.nav.kjerneinfo.domain.info.BankkontoUtland();
        no.nav.kjerneinfo.domain.person.UstrukturertAdresse bankadresse = new no.nav.kjerneinfo.domain.person.UstrukturertAdresse();
        bankadresse.setAdresselinje1(UTENLANDSK_BANKADRESSE);
        bankkontoUtland.setBankadresse(bankadresse);
        bankkontoUtland.setLandkode(new Kodeverdi(LANDKODE, LANDKODE));
        bankkontoUtland.setBanknavn(UTENLANDSK_BANKNAVN);
        bankkontoUtland.setKontonummer(KONTONUMMER);
        bankkontoUtland.setBankkode(BANKKODE);
        bankkontoUtland.setSwift(SWIFT_KODE);
        bankkontoUtland.setValuta(new Kodeverdi(VALUTA, VALUTA));
        Endringsinformasjon endringsinformasjon = new Endringsinformasjon();
        endringsinformasjon.setEndretAv(ENDRET_AV);
        endringsinformasjon.setSistOppdatert(LocalDateTime.parse(SIST_OPPDATERT));
        bankkontoUtland.setEndringsinformasjon(endringsinformasjon);
        Person person = lagMockPerson();
        person.getPersonfakta().setBankkonto(bankkontoUtland);

        Bruker bruker = mapper.map(person, Bruker.class);
        BankkontoUtland to = (BankkontoUtland) bruker.getBankkonto();

        assertThat(to.getBankadresse().getAdresselinje1(), is(UTENLANDSK_BANKADRESSE));
        assertThat(to.getLandkode().getKodeRef(), is(LANDKODE));
        assertThat(to.getBanknavn(), is(UTENLANDSK_BANKNAVN));
        assertThat(to.getBankkode(), is(BANKKODE));
        assertThat(to.getSwift(), is(SWIFT_KODE));
        assertThat(to.getValuta().getKodeRef(), is(VALUTA));
        assertThat(to.getKontonummer(), is(KONTONUMMER));
        assertThat(to.getEndretAv(), is(ENDRET_AV));
        assertThat(to.getEndringstidspunkt().toLocalDate().toString(), is(SIST_OPPDATERT));
    }

    @Test
    public void mapGjeldendePostadressetype() {
        Person from = lagMockPerson();
        String koderef = "koderef";
        String beskrivelse = "beskrivelse";
        from.getPersonfakta().setGjeldendePostadressetype(new Kodeverdi(koderef, beskrivelse));

        Bruker to = mapper.map(from, Bruker.class);

        assertThat(to.getGjeldendePostadresseType().getBeskrivelse(), is(beskrivelse));
        assertThat(to.getGjeldendePostadresseType().getKodeRef(), is(koderef));
    }

    private AlternativAdresseUtland lagAlternativAdresseUtland() {
        AlternativAdresseUtland adresseUtland = new AlternativAdresseUtland();
        adresseUtland.setAdresselinje1(ADRESSELINJE1);
        adresseUtland.setAdresselinje2(ADRESSELINJE2);
        adresseUtland.setAdresselinje3(ADRESSELINJE3);
        adresseUtland.setAdresselinje4(ADRESSELINJE4);
        adresseUtland.setLandkode(new Kodeverdi(LANDKODE, "NOR"));
        adresseUtland.setPostleveringsPeriode(new Periode(null, GYLDIG_TIL));

        Endringsinformasjon endringsinformasjon = new Endringsinformasjon();
        endringsinformasjon.setEndretAv(ENDRET_AV);
        endringsinformasjon.setSistOppdatert(LocalDateTime.parse(SIST_OPPDATERT));
        adresseUtland.setEndringsinformasjon(endringsinformasjon);
        return adresseUtland;
    }

    private Adresselinje lagPostboksAdresse() {
        Postboksadresse postboksadresse = new Postboksadresse();
        postboksadresse.setPoststed(POSTNUMMER);
        postboksadresse.setPostboksanlegg(POSTBOKSANLEGG);
        postboksadresse.setPostboksnummer(POSTBOKSNUMMER);
        postboksadresse.setPoststednavn(POSTSTEDSNAVN);
        postboksadresse.setTilleggsadresse(TILLEGGSADRESSE);
        postboksadresse.setTilleggsadresseType(TILLEGGSADRESSE_TYPE);
        postboksadresse.setPostleveringsPeriode(new Periode(null, GYLDIG_TIL));
        Endringsinformasjon endringsinformasjon = new Endringsinformasjon();
        endringsinformasjon.setEndretAv(ENDRET_AV);
        endringsinformasjon.setSistOppdatert(LocalDateTime.parse(SIST_OPPDATERT));
        postboksadresse.setEndringsinformasjon(endringsinformasjon);
        return postboksadresse;
    }

    private no.nav.kjerneinfo.domain.person.UstrukturertAdresse lagPostadresse() {
        no.nav.kjerneinfo.domain.person.UstrukturertAdresse ustrukturertAdresse = new no.nav.kjerneinfo.domain.person.UstrukturertAdresse();
        ustrukturertAdresse.setAdresselinje1(ADRESSELINJE1);
        ustrukturertAdresse.setAdresselinje2(ADRESSELINJE2);
        ustrukturertAdresse.setAdresselinje3(ADRESSELINJE3);
        ustrukturertAdresse.setAdresselinje4(ADRESSELINJE4);
        Endringsinformasjon endringsinformasjon = new Endringsinformasjon();
        endringsinformasjon.setEndretAv(ENDRET_AV);
        endringsinformasjon.setSistOppdatert(LocalDateTime.parse(SIST_OPPDATERT));
        ustrukturertAdresse.setEndringsinformasjon(endringsinformasjon);
        return ustrukturertAdresse;
    }

    private Adresselinje lagMatrikkelAdresse() {
        no.nav.kjerneinfo.domain.person.Matrikkeladresse matrikkeladresse = new no.nav.kjerneinfo.domain.person.Matrikkeladresse();
        matrikkeladresse.setPostnummer(POSTNUMMER);
        matrikkeladresse.setEiendomsnavn(EIENDOMSNAVN);
        matrikkeladresse.setPoststed(POSTSTEDSNAVN);
        matrikkeladresse.setTilleggsadresse(TILLEGGSADRESSE);
        matrikkeladresse.setTilleggsadressetype(TILLEGGSADRESSE_TYPE);
        matrikkeladresse.setGaardsnummer(GAARDSNUMMER);
        matrikkeladresse.setBruksnummer(BRUKSNUMMER);
        matrikkeladresse.setFestenummer(FESTENUMMER);
        matrikkeladresse.setSeksjonsnummer(SEKSJONSNUMMER);
        matrikkeladresse.setUndernummer(UNDERNUMMER);
        matrikkeladresse.setPostleveringsPeriode(new Periode(null, GYLDIG_TIL));

        Endringsinformasjon endringsinformasjon = new Endringsinformasjon();
        endringsinformasjon.setSistOppdatert(ENDRETTIDSPUNKT);
        endringsinformasjon.setEndretAv(ENDRET_AV);
        matrikkeladresse.setEndringsinformasjon(endringsinformasjon);


        return matrikkeladresse;
    }

    private Adresse lagAdresse() {
        Adresse adresse = new Adresse();

        Endringsinformasjon endringsinformasjon = new Endringsinformasjon();
        endringsinformasjon.setSistOppdatert(ENDRETTIDSPUNKT);
        endringsinformasjon.setEndretAv(ENDRET_AV);
        adresse.setEndringsinformasjon(endringsinformasjon);

        adresse.setPoststednavn(POSTSTEDSNAVN);
        adresse.setPostnummer(POSTNUMMER);
        adresse.setBolignummer(BOLIGNUMMER);
        adresse.setHusbokstav(HUSBOKSTAV);
        adresse.setGatenummer(GATENUMMER);
        adresse.setGatenavn(GATENAVN);
        adresse.setTilleggsadresse(TILLEGGSADRESSE);
        adresse.setTilleggsadresseType(TILLEGGSADRESSE_TYPE);
        adresse.setPostleveringsPeriode(new Periode(null, GYLDIG_TIL));
        return adresse;
    }

}
