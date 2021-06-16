package no.nav.modiapersonoversikt.legacy.kjerneinfo.domene.person;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Kodeverdi;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.info.Bankkonto;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.info.BankkontoUtland;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.*;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.fakta.Familierelasjon;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domene.factory.PersonDoFactory;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PersonTest {

    private String FNR = "12345678910";
    private int PID = 1234;
    private String BOLIGNUMMER = "bnr1";
    private String GATENAVN = "gnavn1";
    private String GATENUMMER = "gnr1";
    private int ID = 3456;
    private String POSTNUMMER = "prn1";
    private String POSTSTED = "psted1";
    private String POSTADRESSE = "padr1";
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
    private Kodeverdi SIVILSTAND_SAMBOER = new Kodeverdi.With().kodeRef("SAMBOER").done();
    private String TIL_ROLLE_SAMBOER = "SAMBOER";
	private String SIKKERHETSTILTAK_BESKRIVELSE = "Farlig person";
	private String SIKKERHETSTILTAK_KODE = "FPE";

    @Test
    public void testGetSet() {

        Person person = PersonDoFactory.createPerson(FNR, PID, BOLIGNUMMER, GATENAVN, GATENUMMER, ID, POSTNUMMER, POSTSTED, POSTADRESSE,
                FOM, TOM, ADRLINJE1, ADRLINJE2, ADRLINJE3, BANKNAVN, KONTONUMMER, BOSTATUS, DISKRESJONSKODE, GJELDMEDLSTATUS, ENHET,
                FODESTED, KJONN, PERSONFAKTAID, FORNAVN, MELLOMNAVN, ETTERNAVN, PERSONNAVNID, ENDRET,
                BANKKODE, LANDKODE, SWIFT, VALUTA, SIVILSTAND_SAMBOER, TIL_ROLLE_SAMBOER, SIKKERHETSTILTAK_BESKRIVELSE, SIKKERHETSTILTAK_KODE);

        checkPerson(person);
    }

    private void checkPerson(Person person) {
        checkFodselsnummer(person.getFodselsnummer());
        checkPersonfakta(person.getPersonfakta());
        assertEquals(PID, person.getPersonId());
        assertEquals("Person ["
                + "personId=" + person.getPersonId() + ", "
                + "fodselsnummer=" + person.getFodselsnummer() + ", "
                + "personfakta=" + person.getPersonfakta()
                + "]", person.toString());
    }

    private void checkPersonfakta(Personfakta personfakta) {
        checkBostedsadresse(personfakta.getAdresse());
        checkBostedsadresse(personfakta.getAlternativAdresse());
        checkNullBostedsadresse();
        checkBankkonto(personfakta.getBankkonto());
        assertEquals(BOSTATUS, personfakta.getBostatus());
        checkBostedsadresse(personfakta.getBostedsadresse());
        assertEquals(DISKRESJONSKODE, personfakta.getDiskresjonskode().getKodeRef());
        assertEquals(FODESTED, personfakta.getFodested());
        assertEquals(GJELDMEDLSTATUS, personfakta.getGjeldendeMedlemskapstatus());
        checkHarFraRolleIList(personfakta.getHarFraRolleIList());
        assertEquals(ENHET, personfakta.getGeografiskTilknytning().getValue());
        assertEquals(KJONN, personfakta.getKjonn().toString());
        assertEquals(PERSONFAKTAID, personfakta.getPersonfaktaId());
        checkPersonnavn(personfakta.getPersonnavn());
        checkBostedsadresse(personfakta.getPostadresse());
        assertEquals(SIVILSTAND_SAMBOER.getKodeRef(), personfakta.getSivilstand().getKodeRef());
        assertEquals("Personfakta [personfaktaId=" + personfakta.getPersonfaktaId() + ", personnavn="
                + personfakta.getPersonnavn() + ", sivilstand=" + personfakta.getSivilstand().getKodeRef() + ", adresse="
                + personfakta.getBostedsadresse() + "]", personfakta.toString());
    }

    private void checkPersonnavn(Personnavn personnavn) {
        assertEquals(ETTERNAVN, personnavn.getEtternavn());
        assertEquals(FORNAVN, personnavn.getFornavn());
        assertEquals(MELLOMNAVN, personnavn.getMellomnavn());
        checkEndringsinformasjon(personnavn.getSistEndret());

    }

    private void checkEndringsinformasjon(Endringsinformasjon sistEndret) {
        assertEquals(FORNAVN, sistEndret.getEndretAv());
        assertEquals(ENDRET.getYear(), sistEndret.getSistOppdatert().getYear());
    }

    private void checkHarFraRolleIList(List<Familierelasjon> harFraRolleIList) {
        if (!harFraRolleIList.isEmpty()) {
            checkFamilierelasjon(harFraRolleIList.get(0));
        }
    }

    private void checkFamilierelasjon(Familierelasjon familierelasjon) {
        assertNull(familierelasjon.getTilPerson());
        assertEquals(TIL_ROLLE_SAMBOER, familierelasjon.getTilRolle());
    }

    private void checkBankkontoUtland(BankkontoUtland bankkontoUtland) {
        assertEquals(KONTONUMMER, bankkontoUtland.getKontonummer());
        assertEquals(BANKNAVN, bankkontoUtland.getBanknavn());
        assertEquals(BANKKODE, bankkontoUtland.getBankkode());
        assertEquals(LANDKODE, bankkontoUtland.getLandkode().getKodeRef());
        assertEquals(VALUTA, bankkontoUtland.getValuta().getKodeRef());
        assertEquals(SWIFT, bankkontoUtland.getSwift());
    }

    private void checkBankkonto(Bankkonto bankkonto) {
        assertEquals(BANKNAVN, bankkonto.getBanknavn());
        assertEquals(KONTONUMMER, bankkonto.getKontonummer());
        if (bankkonto instanceof BankkontoUtland) {
            checkBankkontoUtland((BankkontoUtland) bankkonto);
        }
    }

    private void checkNullBostedsadresse() {
        Adresse adresse = new Adresse();
        assertEquals("", adresse.getGateadresseLinje());
        assertEquals("", adresse.getPostadresseLinje());
    }

    private void checkBostedsadresse(Adresselinje adresselinje) {
        assert (adresselinje instanceof Adresse);
        Adresse adresse = (Adresse) adresselinje;
        assertEquals(BOLIGNUMMER, adresse.getBolignummer());
        assertEquals(GATENAVN + " " + GATENUMMER + " " + BOLIGNUMMER, adresse.getGateadresseLinje());
        assertEquals(GATENAVN, adresse.getGatenavn());
        assertEquals(GATENUMMER, adresse.getGatenummer());
        assertEquals(POSTNUMMER + " " + POSTSTED, adresse.getPostadresseLinje());
        assertEquals(POSTNUMMER, adresse.getPostnummer());
        assertEquals(POSTSTED, adresse.getPoststednavn());
        assertEquals(POSTADRESSE, adresse.getTilleggsadresseMedType());
    }

    private void checkFodselsnummer(Fodselsnummer fodselsnummer) {
        assertEquals(FNR, fodselsnummer.getNummer());
    }
}
