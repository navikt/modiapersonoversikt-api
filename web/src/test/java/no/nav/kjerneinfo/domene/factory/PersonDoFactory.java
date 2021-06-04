
package no.nav.kjerneinfo.domene.factory;

import no.nav.kjerneinfo.common.domain.Kodeverdi;
import no.nav.kjerneinfo.domain.info.BankkontoUtland;
import no.nav.kjerneinfo.domain.person.*;
import no.nav.kjerneinfo.domain.person.fakta.Familierelasjon;
import no.nav.kjerneinfo.domain.person.fakta.Sikkerhetstiltak;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public class PersonDoFactory {

    public static Person createPerson(String fnr, int pid, String bolignummer, String gatenavn, String gatenummer, int id, String postnummer,
            String poststed, String postadresse, LocalDate fom, LocalDate tom, String adrlinje1, String adrlinje2, String adrlinje3,
            String banknavn, String kontonummer, Kodeverdi bostatus, String diskresjonskode,
            String gjeldendeMedlemskapstatus, String enhet, String fodested, String kjonn,
            int personfaktaid, String fornavn, String mellomnavn, String etternavn, int personnavnid,
            LocalDateTime endret, String bankkode, String landkode, String swift, String valuta, Kodeverdi sivilstand, String tilRolle,
			String sikkerhetstiltakBeskrivelse, String sikkerhetstiltakKode) {

        Person person = new Person();

        person.setFodselsnummer(createFodselsnummer(fnr));
        person.setPersonId(pid);
        person.setPersonfakta(createPersonfakta(bolignummer, gatenavn, gatenummer, id, postnummer, poststed, postadresse, fom, tom, adrlinje1, adrlinje2, adrlinje3,
                banknavn, kontonummer, bostatus, diskresjonskode, gjeldendeMedlemskapstatus, enhet, fodested, kjonn, personfaktaid, fornavn, mellomnavn, etternavn,
                personnavnid, endret, bankkode, landkode, swift, valuta, sivilstand, tilRolle, sikkerhetstiltakBeskrivelse, sikkerhetstiltakKode));

        return person;
    }

    public static Personfakta createPersonfakta(String bolignummer, String gatenavn, String gatenummer, int id, String postnummer,
            String poststed, String postadresse, LocalDate fom, LocalDate tom, String adrlinje1, String adrlinje2, String adrlinje3,
            String banknavn, String kontonummer, Kodeverdi bostatus, String diskresjonskode,
            String gjeldendeMedlemskapstatus, String enhet, String fodested, String kjonn,
            int personfaktaid, String fornavn, String mellomnavn, String etternavn, int personnavnid,
            LocalDateTime endret,
            String bankkode, String landkode, String swift,
            String valuta, Kodeverdi sivilstand, String tilRolle,
			String sikkerhetstiltakBeskrivelse, String sikkerhetstiltakKode) {
        Personfakta personfakta = new Personfakta();

        personfakta.setAdresse(createAdresse(bolignummer, gatenavn, gatenummer, id, postnummer, poststed, postadresse, fom, tom));
        personfakta.setAlternativAdresse(createAdresse(bolignummer, gatenavn, gatenummer, id, postnummer, poststed, postadresse, fom, tom));
        personfakta.setBankkonto(createBankkontoUtland(kontonummer, banknavn, bankkode, landkode, swift, valuta));
        personfakta.setBostatus(bostatus);
        personfakta.setBostedsadresse(createAdresse(bolignummer, gatenavn, gatenummer, id, postnummer, poststed, postadresse, fom, tom));
        personfakta.setDiskresjonskode(new Kodeverdi(diskresjonskode, diskresjonskode));
        personfakta.setGjeldendeMedlemskapstatus(gjeldendeMedlemskapstatus);
        personfakta.setHarFraRolleIList(createFamilierelasjoner(tilRolle));
        personfakta.setGeografiskTilknytning(new GeografiskTilknytning().withValue(enhet).withType(GeografiskTilknytningstyper.BYDEL));
        personfakta.setFodested(fodested);
        Kodeverdi kjonnkodeverdi = new Kodeverdi();
        kjonnkodeverdi.setKodeRef(kjonn);
        personfakta.setKjonn(kjonnkodeverdi);
        personfakta.setPersonfaktaId(personfaktaid);
        personfakta.setPersonnavn(createPersonnavn(fornavn, mellomnavn, etternavn, personnavnid, endret));
        personfakta.setPostadresse(createAdresse(bolignummer, gatenavn, gatenummer, id, postnummer, poststed, postadresse, fom, tom));
        personfakta.setSivilstand(sivilstand);
		personfakta.setSikkerhetstiltak(createSikkerhetstiltak(sikkerhetstiltakBeskrivelse, sikkerhetstiltakKode));
        return personfakta;
    }



    private static Personnavn createPersonnavn(String fornavn, String mellomnavn, String etternavn, int personnavnid, LocalDateTime endret) {
        Personnavn personnavn = new Personnavn();

        personnavn.setEtternavn(etternavn);
        personnavn.setFornavn(fornavn);
        personnavn.setMellomnavn(mellomnavn);
        personnavn.setSistEndret(createEndringsinformasjon(fornavn, endret));

        return personnavn;
    }

    private static Endringsinformasjon createEndringsinformasjon(String endretAv, LocalDateTime sistOppdatert) {
        Endringsinformasjon endringsinformasjon = new Endringsinformasjon();

        endringsinformasjon.setEndretAv(endretAv);
        endringsinformasjon.setSistOppdatert(sistOppdatert);

        return endringsinformasjon;
    }

    private static List<Familierelasjon> createFamilierelasjoner(String tilRolle) {
        List<Familierelasjon> familierelasjonList = new ArrayList<>();

        familierelasjonList.add(createFamilierelasjon(tilRolle));

        return familierelasjonList;
    }

    private static Familierelasjon createFamilierelasjon(String tilRolle) {
        Familierelasjon familierelasjon = new Familierelasjon();

        familierelasjon.setTilPerson(null);
        familierelasjon.setTilRolle(tilRolle);

        return familierelasjon;
    }

    private static BankkontoUtland createBankkontoUtland(String kontonummer, String banknavn,
            String bankkode, String landkode, String swift, String valuta) {

        BankkontoUtland bankkonto = new BankkontoUtland();

        bankkonto.setKontonummer(kontonummer);
        bankkonto.setBanknavn(banknavn);
        bankkonto.setBankadresse(createAlternativAdresseUtland("", "", "", "", ""));
        bankkonto.setBankkode(bankkode);
        bankkonto.setLandkode(new Kodeverdi(landkode, landkode));
        bankkonto.setSwift(swift);
        bankkonto.setValuta(new Kodeverdi(valuta, valuta));

        return bankkonto;
    }

    private static AlternativAdresseUtland createAlternativAdresseUtland(String adrlinje1, String adrlinje2, String adrlinje3, String adrlinje4, String landkode) {
        AlternativAdresseUtland adresse = new AlternativAdresseUtland();
        adresse.setAdresselinje1(adrlinje1);
        adresse.setAdresselinje2(adrlinje2);
        adresse.setAdresselinje3(adrlinje3);
        adresse.setAdresselinje4(adrlinje4);
        adresse.setLandkode(new Kodeverdi("Landkoder", landkode));

        return adresse;
    }

    public static Adresse createAdresse(String bolignummer, String gatenavn, String gatenummer, int id, String postnummer,
            String poststed, String postadresse, LocalDate fom, LocalDate tom) {
        Adresse adresse = new Adresse();
        adresse.setBolignummer(bolignummer);
        adresse.setGatenavn(gatenavn);
        adresse.setGatenummer(gatenummer);
        adresse.setPostnummer(postnummer);
        adresse.setPoststednavn(poststed);
        adresse.setTilleggsadresse(postadresse);

        return adresse;
    }

    public static Fodselsnummer createFodselsnummer(String fnr) {
        Fodselsnummer fodselsnummer = new Fodselsnummer(fnr);
        fodselsnummer.setNummer(fodselsnummer.getNummer());

        return fodselsnummer;
    }

	public static Sikkerhetstiltak createSikkerhetstiltak(String sikkerhetstiltakBeskrivelse, String sikkerhetstiltakKode) {
		Sikkerhetstiltak sikkerhetstiltak = new Sikkerhetstiltak();
		sikkerhetstiltak.setSikkerhetstiltakskode(sikkerhetstiltakKode);
		sikkerhetstiltak.setSikkerhetstiltaksbeskrivelse(sikkerhetstiltakBeskrivelse);
        return sikkerhetstiltak;
    }
}
