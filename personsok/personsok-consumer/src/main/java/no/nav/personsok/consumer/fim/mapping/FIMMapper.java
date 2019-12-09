package no.nav.personsok.consumer.fim.mapping;

import ma.glasnost.orika.*;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.metadata.Type;
import no.nav.personsok.consumer.fim.kodeverk.KodeverkManager;
import no.nav.personsok.consumer.fim.personsok.to.FinnPersonRequest;
import no.nav.personsok.consumer.fim.personsok.to.FinnPersonResponse;
import no.nav.personsok.domain.Adresse;
import no.nav.personsok.domain.Kjonn;
import no.nav.personsok.domain.Person;
import no.nav.personsok.domain.enums.AdresseType;
import no.nav.personsok.domain.enums.Diskresjonskode;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.*;
import org.joda.time.LocalDate;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Denne klassen beskriver custom-mapping av objekter fra FIM-typer som vi får inn fra tjenesten til domeneklassene vi har
 * definert i kjerneinfo-domain. Mappingen utføres ved hjelp av rammeverket Orika.
 */
public final class FIMMapper extends ConfigurableMapper {

    private static final String SEPARATOR = " ";
    private KodeverkManager kodeverkManager;

    public FIMMapper(KodeverkManager kodeverkManager) {
        this.kodeverkManager = kodeverkManager;
    }

    @Override
    public void configure(MapperFactory mapperFactory) {
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        configureRequestConverters(converterFactory);
        configureRequestClassMaps(mapperFactory);

        configureResponseConverters(converterFactory);
        configureResponseClassMaps(mapperFactory);
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
                return join(new String[] { postnummer.getValue(), poststed }, SEPARATOR).trim();
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

    private void configureResponseClassMaps(MapperFactory mapperFactory) {
        registerFimFinnPersonResponse(mapperFactory);
        registerFimPerson(mapperFactory);
        registerFimBruker(mapperFactory);
        registerFimBostedsadresse(mapperFactory);
        registerFimPostadresse(mapperFactory);
        registrerMidlertidigAdresse(mapperFactory);
    }

    private void registerFimFinnPersonResponse(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonResponse.class, FinnPersonResponse.class)
                .byDefault().toClassMap());
    }

    private void registrerMidlertidigAdresse(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.MidlertidigPostadresse.class, Adresse.class).customize(new CustomMapper<no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.MidlertidigPostadresse, Adresse>() {
            @Override
            public void mapAtoB(no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.MidlertidigPostadresse from, Adresse to, MappingContext context) {
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
            }
        }).byDefault().toClassMap());
    }

    private String joinAdresseLinjer(no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.UstrukturertAdresse adresse) {
        Landkoder landkode = adresse.getLandkode();
        return join(new String[] { adresse.getAdresselinje1(), adresse.getAdresselinje2(), adresse.getAdresselinje3(), adresse.getAdresselinje4(), "," , getNavnPaLand(landkode) }, SEPARATOR).trim();
    }

    private void registerFimPostadresse(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(Postadresse.class, Adresse.class).customize(new CustomMapper<Postadresse, Adresse>() {
            @Override
            public void mapAtoB(Postadresse from, Adresse to, MappingContext context) {
                to.setAdresseType(AdresseType.POSTADRESSE);
                UstrukturertAdresse ustrukturertAdresse = from.getUstrukturertAdresse();
                to.setAdresseString(joinAdresseLinjer(ustrukturertAdresse));
            }
        }).byDefault().toClassMap());
    }

    private void registerFimBostedsadresse(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(Bostedsadresse.class, Adresse.class).customize(new CustomMapper<Bostedsadresse, Adresse>() {
            @Override
            public void mapAtoB(Bostedsadresse from, Adresse to, MappingContext context) {
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
                    to.setAdresseString(join(new String[] { addressBuilder(null, strukturertAdresse.getPostboksanlegg(), null, strukturertAdresse.getPoststed()) }, SEPARATOR).trim());
                }
            }
        }).byDefault().toClassMap());
    }

    private void registerFimBruker(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(Bruker.class, Person.class)
                .field("personnavn.fornavn", "fornavn")
                .field("personnavn.mellomnavn", "mellomnavn")
                .field("personnavn.etternavn", "etternavn")
                .field("personnavn.sammensattNavn", "sammensattNavn")
                .field("personstatus.personstatus.value", "personstatus.kode")
                .fieldAToB("ident.ident", "fodselsnummer")
                .field("harAnsvarligEnhet.enhet.organisasjonselementID", "kommunenr")
                .customize(
                        new CustomMapper<Bruker, Person>() {
                            /*
                             * Gjeldende adressetype skal være første element i listen
                             */
                            @Override
                            public void mapAtoB(Bruker from, Person to, MappingContext context) {
                                to.setAdresser(new ArrayList<Adresse>());
                                if (from.getBostedsadresse() != null) {
                                    if (from.getGjeldendePostadresseType() != null && AdresseType.BOLIGADRESSE.name().equals(from.getGjeldendePostadresseType().getValue())) {
                                        to.getAdresser().add(0, mapperFacade.map(from.getBostedsadresse(), Adresse.class));
                                    } else {
                                        to.getAdresser().add(mapperFacade.map(from.getBostedsadresse(), Adresse.class));
                                    }
                                }
                                if (from.getPostadresse() != null) {
                                    if (from.getGjeldendePostadresseType() != null && AdresseType.POSTADRESSE.name().equals(from.getGjeldendePostadresseType().getValue())) {
                                        to.getAdresser().add(0, mapperFacade.map(from.getPostadresse(), Adresse.class));
                                    } else {
                                        to.getAdresser().add(mapperFacade.map(from.getPostadresse(), Adresse.class));
                                    }
                                }
                                if (from.getMidlertidigPostadresse() != null) {
                                    mapMidlertidigPostadresse(from, to, mapperFacade);
                                }
                            }
                        }).byDefault().toClassMap());
    }

    private static void mapMidlertidigPostadresse(Bruker from, Person to, MapperFacade mapperFacade) {
        if (from.getGjeldendePostadresseType() != null && AdresseType.MIDLERTIDIG_POSTADRESSE_NORGE.name().equals(from.getGjeldendePostadresseType().getValue())) {
            to.getAdresser().add(0, mapperFacade.map(from.getMidlertidigPostadresse(), Adresse.class));
        } else if (from.getGjeldendePostadresseType() != null && AdresseType.MIDLERTIDIG_POSTADRESSE_UTLAND.name().equals(from.getGjeldendePostadresseType().getValue())) {
            to.getAdresser().add(0, mapperFacade.map(from.getMidlertidigPostadresse(), Adresse.class));
        } else {
            to.getAdresser().add(mapperFacade.map((from.getMidlertidigPostadresse()), Adresse.class));
        }
    }

    private static void registerFimPerson(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.Person.class, Person.class)
                .field("personnavn.fornavn", "fornavn")
                .field("personnavn.mellomnavn", "mellomnavn")
                .field("personnavn.etternavn", "etternavn")
                .field("personstatus.personstatus.value", "personstatus.kode")
                .field("diskresjonskode.value", "diskresjonskodePerson")
                .fieldAToB("ident.ident", "fodselsnummer")
                .customize(new CustomMapper<no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.Person, Person>() {
                    @Override
                    public void mapAtoB(no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.Person from, Person to, MappingContext context) {
                        to.setAdresser(new ArrayList<Adresse>());
                        if (from.getBostedsadresse() != null) {
                            to.getAdresser().add(mapperFacade.map(from.getBostedsadresse(), Adresse.class));
                        }
                        if (from.getPostadresse() != null) {
                            to.getAdresser().add(mapperFacade.map(from.getPostadresse(), Adresse.class));
                        }
                    }
                }).byDefault().toClassMap());
    }

    private void configureResponseConverters(ConverterFactory converterFactory) {
        converterFactory.registerConverter(lagDatoConverter());
        converterFactory.registerConverter(lagIdentitetConverter());
        converterFactory.registerConverter(lagDiskresjonskodeConverter());
        converterFactory.registerConverter(lagGeografiskAdresseConverter());
        converterFactory.registerConverter(lagGateadresseConverter());
        converterFactory.registerConverter(lagUstrukturertAdresseConverter());
    }

    private CustomConverter<UstrukturertAdresse, String> lagUstrukturertAdresseConverter() {
        return new CustomConverter<UstrukturertAdresse, String>() {
            @Override
            public String convert(UstrukturertAdresse source, Type<? extends String> destinationType, MappingContext mappingContext) {
                return source.getAdresselinje1();
            }
        };
    }

    private CustomConverter<Gateadresse, String> lagGateadresseConverter() {
        return new CustomConverter<Gateadresse, String>() {
            @Override
            public String convert(Gateadresse source, Type<? extends String> destinationType, MappingContext mappingContext) {
                return addressBuilder(source.getGatenavn(), source.getHusnummer(), source.getHusbokstav(), source.getPoststed());
            }
        };
    }

    private CustomConverter<GeografiskAdresse, String> lagGeografiskAdresseConverter() {
        return new CustomConverter<GeografiskAdresse, String>() {
            @Override
            public String convert(GeografiskAdresse source, Type<? extends String> destinationType, MappingContext mappingContext) {
                if (source instanceof Gateadresse) {
                    return mapperFacade.map(source, String.class);
                }
                return null;
            }
        };
    }

    private CustomConverter<String, Diskresjonskode> lagDiskresjonskodeConverter() {
        return new CustomConverter<String, Diskresjonskode>() {
            @Override
            public Diskresjonskode convert(String string, Type<? extends Diskresjonskode> type, MappingContext mappingContext) {
                switch (string) {
                case "SPSF":
                    return Diskresjonskode.withKode("6");
                case "SPFO":
                    return Diskresjonskode.withKode("7");
                default:
                    return Diskresjonskode.withKode(string);
                }
            }
        };
    }

    private CustomConverter<List<NorskIdent>, String> lagIdentitetConverter() {
        return new CustomConverter<List<NorskIdent>, String>() {
            @Override
            public String convert(List<NorskIdent> source, Type<? extends String> destinationType, MappingContext mappingContext) {
                for (NorskIdent norskIdent : source) {
                    if ("F".equals(norskIdent.getType().getValue())) {
                        return norskIdent.getIdent();
                    }
                }
                return "";
            }
        };
    }

    private CustomConverter<XMLGregorianCalendar, LocalDate> lagDatoConverter() {
        return new CustomConverter<XMLGregorianCalendar, LocalDate>() {
            @Override
            public LocalDate convert(XMLGregorianCalendar source, Type<? extends LocalDate> destinationType, MappingContext mappingContext) {
                return new LocalDate(source.getYear(), source.getMonth(), source.getDay());
            }

            @Override
            public boolean canConvert(Type<?> sourceType, Type<?> destinationType) {
                return this.sourceType.isAssignableFrom(sourceType) && this.destinationType.equals(destinationType);
            }

        };
    }

    private void configureRequestClassMaps(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(FinnPersonRequest.class, no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonRequest.class)
                .field("utvidetPersonsok.fornavn", "soekekriterie.fornavn")
                .field("utvidetPersonsok.etternavn", "soekekriterie.etternavn")
                .field("utvidetPersonsok.gatenavn", "soekekriterie.gatenavn")
                .field("utvidetPersonsok.kontonummer", "soekekriterie.bankkontoNorge")
                .field("utvidetPersonsok.alderFra", "personFilter.alderFra")
                .field("utvidetPersonsok.alderTil", "personFilter.alderTil")
                .field("utvidetPersonsok.kommunenr", "personFilter.enhetId")
                .field("utvidetPersonsok.fodselsdatoFra", "personFilter.foedselsdatoFra")
                .field("utvidetPersonsok.fodselsdatoTil", "personFilter.foedselsdatoTil")
                .field("utvidetPersonsok.kjonn", "personFilter.kjoenn")
                .field("utvidetPersonsok.husnummer", "adresseFilter.gatenummer")
                .field("utvidetPersonsok.husbokstav", "adresseFilter.husbokstav")
                .field("utvidetPersonsok.postnummer", "adresseFilter.postnummer")
                .byDefault().toClassMap());
    }

    private void configureRequestConverters(ConverterFactory converterFactory) {
        converterFactory.registerConverter(new CustomConverter<LocalDate, XMLGregorianCalendar>() {
            @Override
            public XMLGregorianCalendar convert(LocalDate source, Type<? extends XMLGregorianCalendar> destinationType, MappingContext mappingContext) {
                try {
                    return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(source.getYear(), source.getMonthOfYear(), source.getDayOfMonth(), 0);
                } catch (DatatypeConfigurationException e) {
                    throw new IllegalArgumentException("Cannot create date", e);
                }
            }
        });

        converterFactory.registerConverter(new CustomConverter<Enum<Kjonn>, String>() {
            @Override
            public String convert(Enum<Kjonn> source, Type<? extends String> destinationType, MappingContext mappingContext) {
                return source.toString();
            }
        });
    }
}
