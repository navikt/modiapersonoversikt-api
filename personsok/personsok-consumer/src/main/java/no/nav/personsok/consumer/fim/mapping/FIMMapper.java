package no.nav.personsok.consumer.fim.mapping;

import no.nav.personsok.consumer.fim.kodeverk.KodeverkManager;
import no.nav.personsok.consumer.fim.personsok.to.FinnPersonRequest;
import no.nav.personsok.consumer.fim.personsok.to.FinnPersonResponse;
import no.nav.personsok.domain.Adresse;
import no.nav.personsok.domain.Kodeverkstype;
import no.nav.personsok.domain.Person;
import no.nav.personsok.domain.UtvidetPersonsok;
import no.nav.personsok.domain.enums.AdresseType;
import no.nav.personsok.domain.enums.Diskresjonskode;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.AdresseFilter;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.PersonFilter;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.Soekekriterie;
import org.joda.time.LocalDate;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Denne klassen beskriver custom-mapping av objekter fra FIM-typer som vi får inn fra tjenesten til domeneklassene vi har
 * definert i kjerneinfo-domain. Mappingen utføres ved hjelp av rammeverket Orika.
 */
public final class FIMMapper {

    private static final String SEPARATOR = " ";
    private KodeverkManager kodeverkManager;

    public FIMMapper(KodeverkManager kodeverkManager) {
        this.kodeverkManager = kodeverkManager;
    }

    public no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonRequest map(FinnPersonRequest request) {
        no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonRequest xmlRequest = new no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonRequest();
        xmlRequest.setAdresseFilter(toAdresseFilter(request.getUtvidetPersonsok()));
        xmlRequest.setPersonFilter(toPersonFilter(request.getUtvidetPersonsok()));
        xmlRequest.setSoekekriterie(toSokekriterie(request.getUtvidetPersonsok()));
        return xmlRequest;
    }

    private AdresseFilter toAdresseFilter(UtvidetPersonsok utvidetPersonsok) {
        AdresseFilter adresseFilter = new AdresseFilter();
        adresseFilter.setGatenummer(Integer.parseInt(utvidetPersonsok.getHusnummer()));
        adresseFilter.setHusbokstav(utvidetPersonsok.getHusbokstav());
        adresseFilter.setPostnummer(utvidetPersonsok.getPostnummer());
        return adresseFilter;
    }

    private PersonFilter toPersonFilter(UtvidetPersonsok utvidetPersonsok) {
        PersonFilter personFilter = new PersonFilter();
        personFilter.setAlderFra(utvidetPersonsok.getAlderFra());
        personFilter.setAlderTil(utvidetPersonsok.getAlderTil());
        personFilter.setEnhetId(utvidetPersonsok.getKommunenr());
        personFilter.setFoedselsdatoFra(map(utvidetPersonsok.getFodselsdatoFra()));
        personFilter.setFoedselsdatoTil(map(utvidetPersonsok.getFodselsdatoTil()));
        personFilter.setKjoenn(utvidetPersonsok.getKjonn().toString());
        return personFilter;
    }

    private Soekekriterie toSokekriterie(UtvidetPersonsok utvidetPersonsok) {
        Soekekriterie kriterier = new Soekekriterie();
        kriterier.setFornavn(utvidetPersonsok.getFornavn());
        kriterier.setEtternavn(utvidetPersonsok.getEtternavn());
        kriterier.setGatenavn(utvidetPersonsok.getGatenavn());
        kriterier.setBankkontoNorge(utvidetPersonsok.getKontonummer());
        return kriterier;
    }

    public FinnPersonResponse map(no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonResponse xmlResponse) {
        FinnPersonResponse response = new FinnPersonResponse();
        response.setPersonListe(forEach(xmlResponse.getPersonListe(), this::map));
        response.setTotaltAntallTreff(xmlResponse.getTotaltAntallTreff());
        return response;
    }

    private Person map(no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.Person xmlPerson) {
        Person person = new Person();
        Personnavn xmlPersonnavn = xmlPerson.getPersonnavn();
        if (xmlPersonnavn != null) {
            person.setFornavn(xmlPersonnavn.getFornavn());
            person.setMellomnavn(xmlPersonnavn.getMellomnavn());
            person.setEtternavn(xmlPersonnavn.getEtternavn());
            person.setSammensattNavn(xmlPersonnavn.getSammensattNavn());
        }

        Personstatus xmlPersonStatus = xmlPerson.getPersonstatus();
        if (xmlPersonStatus != null) {
            Kodeverkstype personstatus = new Kodeverkstype();
            personstatus.setKode(xmlPersonStatus.getPersonstatus().getValue());
            person.setPersonstatus(personstatus);
        }

        Diskresjonskoder xmlDiskresjonskode = xmlPerson.getDiskresjonskode();
        if (xmlDiskresjonskode != null) {
            person.setDiskresjonskodePerson(map(xmlDiskresjonskode.getValue()));
        }

        NorskIdent xmlIdent = xmlPerson.getIdent();
        if (xmlIdent != null) {
            person.setFodselsnummer(xmlIdent.getIdent());
        }

        person.setAdresser(new ArrayList<>());
        Bostedsadresse xmlBostedsadresse = xmlPerson.getBostedsadresse();
        if (xmlBostedsadresse != null) {
            person.getAdresser().add(map(xmlBostedsadresse));
        }
        Postadresse xmlPostadresse = xmlPerson.getPostadresse();
        if (xmlPostadresse != null) {
            person.getAdresser().add(map(xmlPostadresse));
        }

        if (xmlPerson instanceof Bruker) {
            Bruker xmlBruker = (Bruker) xmlPerson;
            AnsvarligEnhet xmlAnsvarligEnhet = xmlBruker.getHarAnsvarligEnhet();
            if (xmlAnsvarligEnhet != null) {
                person.setKommunenr(xmlAnsvarligEnhet.getEnhet().getOrganisasjonselementID());
            }

            Postadressetyper xmlPostadresseType = xmlBruker.getGjeldendePostadresseType();
            if (xmlBostedsadresse != null) {
                if (xmlPostadresseType != null && AdresseType.BOLIGADRESSE.name().equals(xmlPostadresseType.getValue())) {
                    person.getAdresser().add(0, map(xmlBostedsadresse));
                } else {
                    person.getAdresser().add(map(xmlBostedsadresse));
                }
            }

            if (xmlPostadresse != null) {
                if (xmlPostadresseType != null && AdresseType.POSTADRESSE.name().equals(xmlPostadresseType.getValue())) {
                    person.getAdresser().add(0, map(xmlPostadresse));
                } else {
                    person.getAdresser().add(map(xmlPostadresse));
                }
            }

            if (xmlBruker.getMidlertidigPostadresse() != null) {
                if (xmlPostadresseType != null && AdresseType.MIDLERTIDIG_POSTADRESSE_NORGE.name().equals(xmlPostadresseType.getValue())) {
                    person.getAdresser().add(0, map(xmlBruker.getMidlertidigPostadresse()));
                } else if (xmlPostadresseType != null && AdresseType.MIDLERTIDIG_POSTADRESSE_UTLAND.name().equals(xmlPostadresseType.getValue())) {
                    person.getAdresser().add(0, map(xmlBruker.getMidlertidigPostadresse()));
                } else {
                    person.getAdresser().add(map(xmlBruker.getMidlertidigPostadresse()));
                }
            }
        }

        return person;
    }

    private Diskresjonskode map(String from) {
        switch (from) {
            case "SPSF":
                return Diskresjonskode.withKode("6");
            case "SPFO":
                return Diskresjonskode.withKode("7");
            default:
                return Diskresjonskode.withKode(from);
        }
    }

    private Adresse map(Bostedsadresse from) {
        Adresse to = new Adresse();
        to.setAdresseType(AdresseType.BOLIGADRESSE);

        if (from.getStrukturertAdresse().getClass().equals(StedsadresseNorge.class)) {
            StedsadresseNorge strukturertAdresse = (StedsadresseNorge) from.getStrukturertAdresse();
            to.setAdresseString(addressBuilder(strukturertAdresse.getTilleggsadresse(),
                    strukturertAdresse.getBolignummer(), null,
                    strukturertAdresse.getPoststed()));
        } else if (from.getStrukturertAdresse().getClass().equals(Gateadresse.class)) {
            Gateadresse strukturertAdresse = (Gateadresse) from.getStrukturertAdresse();
            to.setAdresseString(addressBuilder(strukturertAdresse.getGatenavn(),
                    strukturertAdresse.getHusnummer(),
                    strukturertAdresse.getHusbokstav(), strukturertAdresse.getPoststed()));
        } else if (from.getStrukturertAdresse().getClass().equals(Matrikkeladresse.class)) {
            Matrikkeladresse strukturertAdresse = (Matrikkeladresse) from.getStrukturertAdresse();
            List<String> adresseElementer = new LinkedList<String>() {
                @Override
                public boolean add(String s) {
                    return s == null || super.add(s);
                }
            };
            adresseElementer.add(strukturertAdresse.getEiendomsnavn());
            if (strukturertAdresse.getMatrikkelnummer() != null) {
                adresseElementer.add(strukturertAdresse.getMatrikkelnummer().getBruksnummer());
                adresseElementer.add(strukturertAdresse.getMatrikkelnummer().getFestenummer());
                adresseElementer.add(strukturertAdresse.getMatrikkelnummer().getGaardsnummer());
                adresseElementer.add(strukturertAdresse.getMatrikkelnummer().getSeksjonsnummer());
                adresseElementer.add(strukturertAdresse.getMatrikkelnummer().getUndernummer());
            }
            adresseElementer.add(addressBuilder(null, "", null, strukturertAdresse.getPoststed()));

            to.setAdresseString(join(adresseElementer.toArray(), SEPARATOR).trim());
        } else if (from.getStrukturertAdresse().getClass().equals(PostboksadresseNorsk.class)) {
            PostboksadresseNorsk strukturertAdresse = (PostboksadresseNorsk) from.getStrukturertAdresse();
            to.setAdresseString(join(new String[]{addressBuilder(null, strukturertAdresse.getPostboksanlegg(), null, strukturertAdresse.getPoststed())}, SEPARATOR).trim());
        }
        return to;
    }

    private Adresse map(Postadresse from) {
        Adresse to = new Adresse();
        to.setAdresseType(AdresseType.POSTADRESSE);
        UstrukturertAdresse ustrukturertAdresse = from.getUstrukturertAdresse();
        to.setAdresseString(joinAdresseLinjer(ustrukturertAdresse));
        return to;
    }

    private Adresse map(MidlertidigPostadresse from) {
        Adresse to = new Adresse();

        if (from instanceof no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.MidlertidigPostadresseUtland && ((no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.MidlertidigPostadresseUtland) from).getUstrukturertAdresse() != null) {
            to.setAdresseType(AdresseType.MIDLERTIDIG_POSTADRESSE_UTLAND);
            no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.UstrukturertAdresse ustrukturertAdresse = ((no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.MidlertidigPostadresseUtland) from).getUstrukturertAdresse();
            to.setAdresseString(joinAdresseLinjer(ustrukturertAdresse));
        } else if (from instanceof no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.MidlertidigPostadresseNorge) {
            no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.UstrukturertAdresse ustrukturertAdresse = ((no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.MidlertidigPostadresseNorge) from).getUstrukturertAdresse();
            if (ustrukturertAdresse != null) {
                to.setAdresseType(AdresseType.MIDLERTIDIG_POSTADRESSE_NORGE);
                to.setAdresseString(joinAdresseLinjer(ustrukturertAdresse));
            }
        }

        return to;
    }

    private <S, T> List<T> forEach(List<S> list, Function<S, T> fn) {
        return list.stream().map(fn).collect(Collectors.toList());
    }

    private String joinAdresseLinjer(no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.UstrukturertAdresse adresse) {
        Landkoder landkode = adresse.getLandkode();
        return join(new String[]{adresse.getAdresselinje1(), adresse.getAdresselinje2(), adresse.getAdresselinje3(), adresse.getAdresselinje4(), ",", getNavnPaLand(landkode)}, SEPARATOR).trim();
    }

    private String addressBuilder(String gatenavn, BigInteger gatenummer, String husbokstav, Postnummer postnummer) {
        String gatenummerString = gatenummer == null ? "" : String.valueOf(gatenummer);
        return addressBuilder(gatenavn, gatenummerString, husbokstav, postnummer);
    }

    private String addressBuilder(String gatenavn, String gatenummer, String husbokstav, Postnummer postnummer) {
        String blank = (isBlank(gatenummer) && isBlank(husbokstav)) ? "" : SEPARATOR;
        if ((isBlank(gatenavn) && isBlank(gatenummer) && isBlank(husbokstav))
                || isBlank(getPoststed(postnummer))) {
            return join(gatenavn, blank, gatenummer, husbokstav, getPoststed(postnummer));
        }
        return join(gatenavn, blank, gatenummer, husbokstav, ", ", getPoststed(postnummer));
    }

    private String getPoststed(Postnummer postnummer) {
        if (postnummer != null) {
            String poststed = kodeverkManager.getBeskrivelseForKode(postnummer.getValue(), "Postnummer", "nb");

            if (postnummer.getValue().equals(poststed)) {
                return postnummer.getValue();
            } else {
                return join(new String[]{postnummer.getValue(), poststed}, SEPARATOR).trim();
            }
        }
        return EMPTY;
    }

    private String getNavnPaLand(Landkoder landkode) {
        if (landkode != null) {
            return kodeverkManager.getBeskrivelseForKode(landkode.getValue(), "Landkoder", "nb");
        } else {
            return EMPTY;
        }
    }

    private static XMLGregorianCalendar map(LocalDate source) {
        if (source == null) {
            return null;
        }
        try {
            return DatatypeFactory
                    .newInstance()
                    .newXMLGregorianCalendarDate(source.getYear(), source.getMonthOfYear(), source.getDayOfMonth(), 0);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Could not map to XMLGregorianCalendar", e);
        }
    }
}
