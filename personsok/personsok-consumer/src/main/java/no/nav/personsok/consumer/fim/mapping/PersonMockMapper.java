package no.nav.personsok.consumer.fim.mapping;

import no.nav.personsok.consumer.fim.personsok.mock.GateadresseMock;
import no.nav.personsok.consumer.fim.personsok.mock.PersonMock;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.*;

import java.math.BigInteger;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Klasse for å fylle ut et FIM-object med datene som er relevante for oss. På denne måten kan vi enklere teste
 * mapperen.
 */
public final class PersonMockMapper {

    private PersonMockMapper() {
    }

    public static Person map(PersonMock from) {

        Person to = createFimPerson(from);

        mapNorskIdent(from, to);
        mapPersonNavn(from, to);
        mapAdresse(from, to);
        mapKjonn(from, to);
        mapPersonstatus(from, to);

        if (to instanceof Bruker) {
            if (from.getMidlertidigadresse() != null) {
                mapAdresserUtland(from, (Bruker) to);
            }
            Diskresjonskoder fimDiskresjonskoder = new Diskresjonskoder();
            fimDiskresjonskoder.setValue(from.getDiskresjonskode());
            to.setDiskresjonskode(fimDiskresjonskoder);

            mapAnsvarligEnhet(from, (Bruker) to);
        }

        return to;
    }

    private static void mapPersonstatus(PersonMock from, Person to) {
        Personstatus toStatus = new Personstatus();
        Personstatuser fimPersonstatuser = new Personstatuser();
        fimPersonstatuser.setValue(from.getPersonstatus());
        toStatus.setPersonstatus(fimPersonstatuser);
        to.setPersonstatus(toStatus);
    }

    private static Person createFimPerson(PersonMock from) {
        Person to;
        if (from.getMidlertidigadresse() != null || from.getDiskresjonskode() != null || isNotBlank(from.getEnhet())) {
            to = new Bruker();
        } else {
            to = new Person();
        }
        return to;
    }

    private static void mapAnsvarligEnhet(PersonMock from, Bruker to) {
        AnsvarligEnhet fimAnsvarligEnhet = new AnsvarligEnhet();
        Organisasjonsenhet fimOrganisasjonsenhet = new Organisasjonsenhet();
        fimOrganisasjonsenhet.setOrganisasjonselementID(from.getEnhet());
        fimAnsvarligEnhet.setEnhet(fimOrganisasjonsenhet);

        to.setHarAnsvarligEnhet(fimAnsvarligEnhet);
    }

    private static void mapAdresserUtland(PersonMock from, Bruker to) {
        MidlertidigPostadresseUtland postadresseUtland = new MidlertidigPostadresseUtland();
        UstrukturertAdresse generellAdresseUtland = new UstrukturertAdresse();
        generellAdresseUtland.setAdresselinje1(from.getMidlertidigadresse().getAdresseLinje1());
        generellAdresseUtland.setAdresselinje2(from.getMidlertidigadresse().getAdresseLinje2());
        generellAdresseUtland.setAdresselinje3(from.getMidlertidigadresse().getAdresseLinje3());
        postadresseUtland.setUstrukturertAdresse(generellAdresseUtland);
        to.setMidlertidigPostadresse(postadresseUtland);
    }

    private static void mapNorskIdent(PersonMock from, Person to) {
        Personidenter fimPersonidenter = new Personidenter();
        fimPersonidenter.setValue(from.getIdenttype());
        NorskIdent norskIdent = new NorskIdent();
        norskIdent.setIdent(from.getIdnummer());
        norskIdent.setType(fimPersonidenter);
        to.setIdent(norskIdent);
    }

    private static void mapKjonn(PersonMock from, Person to) {
        Kjoennstyper fimKjoennstyper = new Kjoennstyper();
        fimKjoennstyper.setValue(from.getKjonn());
        Kjoenn kjonn = new Kjoenn();
        kjonn.setKjoenn(fimKjoennstyper);
        to.setKjoenn(kjonn);
    }

    private static void mapAdresse(PersonMock from, Person to) {
        if (from.getBostedsadresse() != null) {
            Bostedsadresse fimBostedsadresse = new Bostedsadresse();
            Gateadresse gateadresse = new Gateadresse();
            gateadresse.setGatenavn(((GateadresseMock) from.getBostedsadresse()).getGatenavn());
            if (from.getBostedsadresse().getGatenummer() != null) {
                gateadresse.setGatenummer(BigInteger.valueOf(Long.valueOf(((GateadresseMock) from.getBostedsadresse()).getGatenummer())));
            }
            gateadresse.setHusbokstav(((GateadresseMock) from.getBostedsadresse()).getHusbokstav());
            if (from.getBostedsadresse().getHusnummer() != null) {
                gateadresse.setHusnummer(BigInteger.valueOf(Long.valueOf(((GateadresseMock) from.getBostedsadresse()).getHusnummer())));
            }
            fimBostedsadresse.setStrukturertAdresse(gateadresse);
            to.setBostedsadresse(fimBostedsadresse);
        }

        if (from.getPostadresse() != null) {
            Postadresse postadresse = new Postadresse();
            UstrukturertAdresse fimUstrukturertAdresse = new UstrukturertAdresse();
            fimUstrukturertAdresse.setLandkode(from.getPostadresse().getLandkode());
            fimUstrukturertAdresse.setAdresselinje1(from.getPostadresse().getAdresseLinje1());
            fimUstrukturertAdresse.setAdresselinje2(from.getPostadresse().getAdresseLinje2());
            fimUstrukturertAdresse.setAdresselinje3(from.getPostadresse().getAdresseLinje3());
            postadresse.setUstrukturertAdresse(fimUstrukturertAdresse);
            to.setPostadresse(postadresse);
        }

    }

    private static void mapPersonNavn(PersonMock from, Person to) {
        to.setPersonnavn(new Personnavn());
        to.getPersonnavn().setSammensattNavn(from.getSammensattNavn());
        to.getPersonnavn().setFornavn(from.getFornavn());
        to.getPersonnavn().setMellomnavn(from.getMellomnavn());
        to.getPersonnavn().setEtternavn(from.getEtternavn());
    }
}
