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

    public static FimPerson map(PersonMock from) {

        FimPerson to = createFimPerson(from);

        mapNorskIdent(from, to);
        mapPersonNavn(from, to);
        mapAdresse(from, to);
        mapKjonn(from, to);
        mapPersonstatus(from, to);

        if (to instanceof FimBruker) {
            if (from.getMidlertidigadresse() != null) {
                mapAdresserUtland(from, (FimBruker) to);
            }
            FimDiskresjonskoder fimDiskresjonskoder = new FimDiskresjonskoder();
            fimDiskresjonskoder.setValue(from.getDiskresjonskode());
            to.setDiskresjonskode(fimDiskresjonskoder);

            mapAnsvarligEnhet(from, (FimBruker) to);
        }

        return to;
    }

    private static void mapPersonstatus(PersonMock from, FimPerson to) {
        FimPersonstatus toStatus = new FimPersonstatus();
        FimPersonstatuser fimPersonstatuser = new FimPersonstatuser();
        fimPersonstatuser.setValue(from.getPersonstatus());
        toStatus.setPersonstatus(fimPersonstatuser);
        to.setPersonstatus(toStatus);
    }

    private static FimPerson createFimPerson(PersonMock from) {
        FimPerson to;
        if (from.getMidlertidigadresse() != null || from.getDiskresjonskode() != null || isNotBlank(from.getEnhet())) {
            to = new FimBruker();
        } else {
            to = new FimPerson();
        }
        return to;
    }

    private static void mapAnsvarligEnhet(PersonMock from, FimBruker to) {
        FimAnsvarligEnhet fimAnsvarligEnhet = new FimAnsvarligEnhet();
        FimOrganisasjonsenhet fimOrganisasjonsenhet = new FimOrganisasjonsenhet();
        fimOrganisasjonsenhet.setOrganisasjonselementID(from.getEnhet());
        fimAnsvarligEnhet.setEnhet(fimOrganisasjonsenhet);

        to.setHarAnsvarligEnhet(fimAnsvarligEnhet);
    }

    private static void mapAdresserUtland(PersonMock from, FimBruker to) {
        FimMidlertidigPostadresseUtland postadresseUtland = new FimMidlertidigPostadresseUtland();
        FimUstrukturertAdresse generellAdresseUtland = new FimUstrukturertAdresse();
        generellAdresseUtland.setAdresselinje1(from.getMidlertidigadresse().getAdresseLinje1());
        generellAdresseUtland.setAdresselinje2(from.getMidlertidigadresse().getAdresseLinje2());
        generellAdresseUtland.setAdresselinje3(from.getMidlertidigadresse().getAdresseLinje3());
        postadresseUtland.setUstrukturertAdresse(generellAdresseUtland);
        to.setMidlertidigPostadresse(postadresseUtland);
    }

    private static void mapNorskIdent(PersonMock from, FimPerson to) {
        FimPersonidenter fimPersonidenter = new FimPersonidenter();
        fimPersonidenter.setValue(from.getIdenttype());
        FimNorskIdent norskIdent = new FimNorskIdent();
        norskIdent.setIdent(from.getIdnummer());
        norskIdent.setType(fimPersonidenter);
        to.setIdent(norskIdent);
    }

    private static void mapKjonn(PersonMock from, FimPerson to) {
        FimKjoennstyper fimKjoennstyper = new FimKjoennstyper();
        fimKjoennstyper.setValue(from.getKjonn());
        FimKjoenn kjonn = new FimKjoenn();
        kjonn.setKjoenn(fimKjoennstyper);
        to.setKjoenn(kjonn);
    }

    private static void mapAdresse(PersonMock from, FimPerson to) {
        if (from.getBostedsadresse() != null) {
            FimBostedsadresse fimBostedsadresse = new FimBostedsadresse();
            FimGateadresse gateadresse = new FimGateadresse();
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
            FimPostadresse postadresse = new FimPostadresse();
            FimUstrukturertAdresse fimUstrukturertAdresse = new FimUstrukturertAdresse();
            fimUstrukturertAdresse.setLandkode(from.getPostadresse().getLandkode());
            fimUstrukturertAdresse.setAdresselinje1(from.getPostadresse().getAdresseLinje1());
            fimUstrukturertAdresse.setAdresselinje2(from.getPostadresse().getAdresseLinje2());
            fimUstrukturertAdresse.setAdresselinje3(from.getPostadresse().getAdresseLinje3());
            postadresse.setUstrukturertAdresse(fimUstrukturertAdresse);
            to.setPostadresse(postadresse);
        }

    }

    private static void mapPersonNavn(PersonMock from, FimPerson to) {
        to.setPersonnavn(new FimPersonnavn());
        to.getPersonnavn().setSammensattNavn(from.getSammensattNavn());
        to.getPersonnavn().setFornavn(from.getFornavn());
        to.getPersonnavn().setMellomnavn(from.getMellomnavn());
        to.getPersonnavn().setEtternavn(from.getEtternavn());
    }
}
