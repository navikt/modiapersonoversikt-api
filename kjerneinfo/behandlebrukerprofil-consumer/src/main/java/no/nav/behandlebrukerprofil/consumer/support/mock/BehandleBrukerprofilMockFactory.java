package no.nav.behandlebrukerprofil.consumer.support.mock;

import no.nav.brukerprofil.domain.Bankkonto;
import no.nav.brukerprofil.domain.BankkontoUtland;
import no.nav.brukerprofil.domain.Bruker;
import no.nav.brukerprofil.domain.Telefon;
import no.nav.brukerprofil.domain.adresser.*;
import no.nav.kjerneinfo.common.domain.Kodeverdi;

import java.util.ArrayList;
import java.util.List;

public class BehandleBrukerprofilMockFactory {
    public static String TILRETTELAGT_KOMMUNIKASJON_KODE = "LESA";

    private static final String CO = "Lars Hansen";
    private static final String GATENAVN = "Gatenavn";
    private static final String GATENUMMER = "4";
    private static final String HUSBOKSTAV = "A";
    private static final String HUSNUMMER = "45";
    private static final String POSTBOKSNUMMER_MED_MELLOMROM = " 2154";
    private static final String POSTNUMMER = "0562";
    private static final String POSTSTED = "Oslo";
    private static final String EIENDOMSNAVN = "Eiendomsnavn";

    public static Bruker getBruker() {
        Bruker bruker = new Bruker();
        bruker.setBankkonto(getBankkonto());
        bruker.setIdent("11223344556");
        bruker.setMidlertidigadresseNorge(createGateadresse());
        setTelefonnumre(bruker, "12345678", "78451245", "77698458");
        bruker.setTilrettelagtKommunikasjon(createTilrettelagtKommunikasjon());
        return bruker;
    }

    private static List<Kodeverdi> createTilrettelagtKommunikasjon() {
        List<Kodeverdi> behov = new ArrayList<>();
        Kodeverdi tilrettelagtKommunikasjon = new Kodeverdi(
                TILRETTELAGT_KOMMUNIKASJON_KODE, "Ledsager");
        behov.add(tilrettelagtKommunikasjon);
        return behov;
    }

    private static Bankkonto getBankkonto() {
        Bankkonto bankkonto = new Bankkonto();
        bankkonto.setBanknavn("Banknavn");
        bankkonto.setKontonummer("1111222233334444");
        return bankkonto;
    }

    private static List<Telefon> setTelefonnumre(Bruker bruker, String mobilNr, String hjemNr, String jobbNr) {
        List<Telefon> telefoner = new ArrayList<>();
        Telefon mobil = new Telefon();
        mobil.setIdentifikator(mobilNr);
        mobil.setType(new Kodeverdi("MOBI", "mobil"));
        bruker.setMobil(mobil);
        Telefon hjem = new Telefon();
        hjem.setIdentifikator(hjemNr);
        hjem.setType(new Kodeverdi("HJET", "hjemnummer"));
        bruker.setHjemTlf(hjem);
        Telefon jobb = new Telefon();
        jobb.setIdentifikator(jobbNr);
        jobb.setType(new Kodeverdi("ARBT", "jobbnummer"));
        bruker.setJobbTlf(jobb);

        telefoner.add(mobil);
        telefoner.add(hjem);
        telefoner.add(jobb);
        return telefoner;
    }

    public static StrukturertAdresse createMatrikkeadresse() {
        Matrikkeladresse strukturertAdresse = new Matrikkeladresse();
        strukturertAdresse.setBolignummer(HUSBOKSTAV);
        strukturertAdresse.setEiendomsnavn(EIENDOMSNAVN);
        strukturertAdresse.setPoststed(POSTNUMMER);
        strukturertAdresse.setPoststedsnavn(POSTSTED);
        return strukturertAdresse;
    }

    public static StrukturertAdresse createPostboksadresse() {
        Postboksadresse strukturertAdresse = new Postboksadresse();
        strukturertAdresse.setPoststed(POSTNUMMER);
        strukturertAdresse.setPoststedsnavn(POSTSTED);
        strukturertAdresse.setPostboksnummer(POSTBOKSNUMMER_MED_MELLOMROM);
        strukturertAdresse.setPostboksanlegg("Anlegg");
        return strukturertAdresse;
    }


    private static StrukturertAdresse createGateadresse() {
        Gateadresse strukturertAdresse = new Gateadresse();
        strukturertAdresse.setTilleggsadresseType(CO);
        strukturertAdresse.setGatenavn(GATENAVN);
        strukturertAdresse.setHusnummer(HUSNUMMER);
        strukturertAdresse.setHusbokstav(HUSBOKSTAV);
        strukturertAdresse.setBolignummer(GATENUMMER);
        strukturertAdresse.setPoststed(POSTNUMMER);

        return strukturertAdresse;
    }

    public static BankkontoUtland getBankkontoUtland() {
        BankkontoUtland b = new BankkontoUtland();
        b.setBankadresse(getUstrukturertAdresse());
        b.setBankkode("AAAA");
        b.setBanknavn("Bank");
        b.setKontonummer("665544223311");
        b.setLandkode(new Kodeverdi("DE", "Deutschland"));
        b.setSwift("BA1111");
        b.setValuta(new Kodeverdi("EU", "EURO"));
        return b;
    }

    public static UstrukturertAdresse getUstrukturertAdresse() {
        UstrukturertAdresse a = new UstrukturertAdresse();
        a.setAdresselinje1("linje 1");
        a.setAdresselinje2("linje 2");
        a.setAdresselinje3("linje 3");
        a.setAdresselinje4("linje 4");
        a.setLandkode(new Kodeverdi("DE", "Deutschland"));
        return a;
    }
}
