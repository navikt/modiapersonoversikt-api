package no.nav.brukerprofil.domain.support;

import ma.glasnost.orika.*;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.metadata.Type;
import no.nav.brukerprofil.domain.*;
import no.nav.brukerprofil.domain.adresser.Gateadresse;
import no.nav.brukerprofil.domain.adresser.StrukturertAdresse;
import no.nav.brukerprofil.domain.adresser.UstrukturertAdresse;
import no.nav.kjerneinfo.common.domain.Kodeverdi;
import no.nav.kjerneinfo.domain.person.*;
import no.nav.modig.core.exception.ApplicationException;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import static java.util.Objects.nonNull;

public class BrukerprofilMapper extends ConfigurableMapper {

    private static Logger logger = LoggerFactory.getLogger(BrukerprofilMapper.class);
    private static BrukerprofilMapper instance = null;

    private BrukerprofilMapper() {}

    public static BrukerprofilMapper getInstance() {
        if (instance == null) {
            instance = new BrukerprofilMapper();
        }

        return instance;
    }

    @Override
    public void configure(MapperFactory mapperFactory) {
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        configureRequestConverters(converterFactory);
        configureResponseConverters(converterFactory);

        registerDatoConverters(mapperFactory);
        registerPerson(mapperFactory);
        registerUstrukturertAdresse(mapperFactory);
        registerGateadresse(mapperFactory);
        registerMatrikkeladresse(mapperFactory);
        registerPostboksadresse(mapperFactory);
    }

    private static void registerPerson(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(Person.class, Bruker.class)
                .field("fodselsnummer.nummer", "ident")
                .field("personfakta.tilrettelagtKommunikasjon", "tilrettelagtKommunikasjon")
                .field("personfakta.gjeldendePostadressetype", "gjeldendePostadresseType")
                .field("personfakta.bankkonto", "bankkonto")
                .customize(
                        new CustomMapper<Person, Bruker>() {
                            @Override
                            public void mapAtoB(Person from, Bruker to, MappingContext context) {
                                Personfakta personfakta = from.getPersonfakta();
                                mapAdresse(mapperFacade, to, personfakta);
                                mapTelefon(mapperFacade, to, personfakta);
                                mapNavn(mapperFacade, to, personfakta);
                                mapBankkonto(mapperFacade, to, personfakta.getBankkonto());
                            }

                        }).byDefault().toClassMap());
    }

    private static void mapNavn(MapperFacade mapperFacade, Bruker to, Personfakta personfakta) {
        if (nonNull(personfakta.getPersonnavn())) {
            if (nonNull(personfakta.getPersonnavn().getFornavn())) {
                to.setFornavn(mapperFacade.map(personfakta.getPersonnavn().getFornavn(), Navn.class));
            } else {
                to.setFornavn(new Navn(""));
            }
            if (nonNull(personfakta.getPersonnavn().getMellomnavn())) {
                to.setMellomnavn(mapperFacade.map(personfakta.getPersonnavn().getMellomnavn(), Navn.class));
            } else {
                to.setMellomnavn(new Navn(""));
            }
            if (nonNull(personfakta.getPersonnavn().getEtternavn())) {
                to.setEtternavn(mapperFacade.map(personfakta.getPersonnavn().getEtternavn(), Navn.class));
            } else {
                to.setEtternavn(new Navn(""));
            }
        }
    }

    private static void mapTelefon(MapperFacade mapperFacade, Bruker to, Personfakta personfakta) {
        if (personfakta.getMobil().isPresent()) {
            to.setMobil(mapperFacade.map(personfakta.getMobil().get(), Telefon.class));
        }
        if (personfakta.getJobbTlf().isPresent()) {
            to.setJobbTlf(mapperFacade.map(personfakta.getJobbTlf().get(), Telefon.class));
        }
        if (personfakta.getHjemTlf().isPresent()) {
            to.setHjemTlf(mapperFacade.map(personfakta.getHjemTlf().get(), Telefon.class));
        }
    }

    private static void mapBankkonto(MapperFacade mapperFacade, Bruker to, no.nav.kjerneinfo.domain.info.Bankkonto source) {

        if (source instanceof no.nav.kjerneinfo.domain.info.BankkontoUtland) {
            BankkontoUtland bankkontoUtland = mapperFacade.map(source, BankkontoUtland.class);
            bankkontoUtland.setBankadresse(mapperFacade.map(((no.nav.kjerneinfo.domain.info.BankkontoUtland) source).getBankadresse(), UstrukturertAdresse.class));
            bankkontoUtland.setKontonummer(source.getKontonummer());
            bankkontoUtland.setValuta(mapperFacade.map(((no.nav.kjerneinfo.domain.info.BankkontoUtland) source).getValuta(), Kodeverdi.class));
            if (source.getEndringsinformasjon() != null) {
                bankkontoUtland.setEndretAv(source.getEndringsinformasjon().getEndretAv());
                bankkontoUtland.setEndringstidspunkt(source.getEndringsinformasjon().getSistOppdatert());
            }
            to.setBankkonto(bankkontoUtland);
        }
        else if (source != null) {
            Bankkonto bankkontoNorge = mapperFacade.map(source, Bankkonto.class);
            if (source.getEndringsinformasjon() != null) {
                bankkontoNorge.setEndretAv(source.getEndringsinformasjon().getEndretAv());
                bankkontoNorge.setEndringstidspunkt(source.getEndringsinformasjon().getSistOppdatert());
            }
            to.setBankkonto(bankkontoNorge);
        }
    }

    private static void mapAdresse(MapperFacade mapperFacade, Bruker to, Personfakta personfakta) {
        if (personfakta.getBostedsadresse() != null) {
            if (personfakta.getBostedsadresse() instanceof Adresse) {
                to.setBostedsadresse(mapperFacade.map(personfakta.getBostedsadresse(), Gateadresse.class));
            } else if (personfakta.getBostedsadresse() instanceof Matrikkeladresse) {
                to.setBostedsadresse(
                        mapperFacade.map(
                                (Matrikkeladresse) personfakta.getBostedsadresse(),
                                no.nav.brukerprofil.domain.adresser.Matrikkeladresse.class
                        )
                );
            }
        }
        if (personfakta.getPostadresse() != null) {
            to.setPostadresse(mapperFacade.map(personfakta.getPostadresse(), UstrukturertAdresse.class));
        }

        if (personfakta.getAlternativAdresse() instanceof Adresse) {
            to.setMidlertidigadresseNorge(mapperFacade.map(personfakta.getAlternativAdresse(), StrukturertAdresse.class));
        } else if (personfakta.getAlternativAdresse() instanceof AlternativAdresseUtland) {
            to.setMidlertidigadresseUtland(mapperFacade.map(personfakta.getAlternativAdresse(), UstrukturertAdresse.class));
            to.getMidlertidigadresseUtland().setLandkode(((AlternativAdresseUtland) personfakta.getAlternativAdresse()).getLandkode());
            to.getMidlertidigadresseUtland().setPostleveringsPeriode(((AlternativAdresseUtland) personfakta.getAlternativAdresse()).getPostleveringsPeriode());
        } else if (personfakta.getAlternativAdresse() instanceof Postboksadresse) {
            to.setMidlertidigadresseNorge(mapperFacade.map(personfakta.getAlternativAdresse(), no.nav.brukerprofil.domain.adresser.Postboksadresse.class));
        } else if (personfakta.getAlternativAdresse() instanceof Matrikkeladresse) {
            to.setMidlertidigadresseNorge(mapperFacade.map(personfakta.getAlternativAdresse(), no.nav.brukerprofil.domain.adresser.Matrikkeladresse.class));
        }
    }

    private static void registerMatrikkeladresse(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(Matrikkeladresse.class, no.nav.brukerprofil.domain.adresser.Matrikkeladresse.class)
                .customize(
                        new CustomMapper<Matrikkeladresse, no.nav.brukerprofil.domain.adresser.Matrikkeladresse>() {
                            @Override
                            public void mapAtoB(Matrikkeladresse from, no.nav.brukerprofil.domain.adresser.Matrikkeladresse to, MappingContext context) {
                                to.setTilleggsadresseType(from.getTilleggsadresseType());
                                to.setTilleggsadresse(from.getTilleggsadresse());
                                to.setPostleveringsPeriode(from.getPostleveringsPeriode());
                                if (from.getEndringsinformasjon() != null) {
                                    to.setEndretAv(from.getEndringsinformasjon().getEndretAv());
                                    to.setEndringstidspunkt(from.getEndringsinformasjon().getSistOppdatert());
                                }
                                to.setPoststedsnavn(from.getPoststed());
                                to.setPoststed(from.getPostnummer());

                                to.setEiendomsnavn(from.getEiendomsnavn());
                                to.setGaardsnummer(from.getGaardsnummer());
                                to.setBruksnummer(from.getBruksnummer());
                                to.setFestenummer(from.getFestenummer());
                                to.setSeksjonsnummer(from.getSeksjonsnummer());
                                to.setUndernummer(from.getUndernummer());
                            }
                        }
                )
                .byDefault().toClassMap());
    }

    private static void registerPostboksadresse(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(Postboksadresse.class, no.nav.brukerprofil.domain.adresser.Postboksadresse.class)
                .customize(
                        new CustomMapper<Postboksadresse, no.nav.brukerprofil.domain.adresser.Postboksadresse>() {
                            @Override
                            public void mapAtoB(Postboksadresse from, no.nav.brukerprofil.domain.adresser.Postboksadresse to, MappingContext context) {
                                to.setTilleggsadresseType(from.getTilleggsadresseType());
                                to.setTilleggsadresse(from.getTilleggsadresse());
                                to.setPostleveringsPeriode(from.getPostleveringsPeriode());
                                if (from.getEndringsinformasjon() != null) {
                                    to.setEndretAv(from.getEndringsinformasjon().getEndretAv());
                                    to.setEndringstidspunkt(from.getEndringsinformasjon().getSistOppdatert());
                                }
                                to.setPoststedsnavn(from.getPoststednavn());
                                to.setPoststed(from.getPoststed());

                                to.setPostboksnummer(from.getPostboksnummer());
                                to.setPostboksanlegg(from.getPostboksanlegg());
                            }
                        }
                )
                .byDefault().toClassMap());
    }

    private static void registerGateadresse(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(Adresse.class, Gateadresse.class)
                .customize(
                        new CustomMapper<Adresse, Gateadresse>() {
                            @Override
                            public void mapAtoB(Adresse from, Gateadresse to, MappingContext context) {
                                to.setTilleggsadresseType(from.getTilleggsadresseType());
                                to.setTilleggsadresse(from.getTilleggsadresseMedType());
                                to.setPostleveringsPeriode(from.getPostleveringsPeriode());
                                if (from.getEndringsinformasjon() != null) {
                                    to.setEndretAv(from.getEndringsinformasjon().getEndretAv());
                                    to.setEndringstidspunkt(from.getEndringsinformasjon().getSistOppdatert());
                                }
                                to.setPoststedsnavn(from.getPoststednavn());
                                to.setPoststed(from.getPostnummer());

                                to.setGatenavn(from.getGatenavn());
                                to.setHusnummer(from.getGatenummer());
                                to.setHusbokstav(from.getHusbokstav());
                                to.setBolignummer(from.getBolignummer());
                            }
                        })
                .byDefault().toClassMap());
    }

    private static void registerUstrukturertAdresse(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(no.nav.kjerneinfo.domain.person.UstrukturertAdresse.class, UstrukturertAdresse.class)
                .customize(
                        new CustomMapper<no.nav.kjerneinfo.domain.person.UstrukturertAdresse, UstrukturertAdresse>() {
                            @Override
                            public void mapAtoB(no.nav.kjerneinfo.domain.person.UstrukturertAdresse from, UstrukturertAdresse to, MappingContext context) {
                                if (from.getEndringsinformasjon() != null) {
                                    to.setEndretAv(from.getEndringsinformasjon().getEndretAv());
                                    to.setEndringstidspunkt(from.getEndringsinformasjon().getSistOppdatert());
                                }

                                to.setAdresselinje1(from.getAdresselinje1());
                                to.setAdresselinje2(from.getAdresselinje2());
                                to.setAdresselinje3(from.getAdresselinje3());
                                to.setAdresselinje4(from.getAdresselinje4());
                            }
                        }
                )
                .byDefault().toClassMap());
    }

    private static void registerDatoConverters(MapperFactory mapperFactory) {

        mapperFactory.getConverterFactory().registerConverter(new CustomConverter<LocalDateTime, LocalDateTime>() {
            @Override
            public LocalDateTime convert(LocalDateTime source, Type<? extends LocalDateTime> destinationType, MappingContext mappingContext) {
                return source;
            }
        });

        mapperFactory.getConverterFactory().registerConverter(new CustomConverter<LocalDate, LocalDate>() {
            @Override
            public LocalDate convert(LocalDate source, Type<? extends LocalDate> destinationType, MappingContext mappingContext) {
                return source;
            }
        });
    }

    private static void configureRequestConverters(ConverterFactory converterFactory) {
        converterFactory.registerConverter(new CustomConverter<LocalDate, XMLGregorianCalendar>() {
            @Override
            public XMLGregorianCalendar convert(LocalDate source, Type<? extends XMLGregorianCalendar> destinationType, MappingContext mappingContext) {
                try {
                    return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(source.getYear(), source.getMonthOfYear(), source.getDayOfMonth(), 0);
                } catch (DatatypeConfigurationException e) {
                    logger.warn("DatatypeConfigurationException", e.getMessage());
                    throw new ApplicationException("DatatypeConfigurationException", e, "Klarer ikke å lage dato");
                }
            }
        });
    }

    private static void configureResponseConverters(ConverterFactory converterFactory) {

        converterFactory.registerConverter(new CustomConverter<XMLGregorianCalendar, LocalDate>() {
            @Override
            public LocalDate convert(XMLGregorianCalendar source, Type<? extends LocalDate> destinationType, MappingContext mappingContext) {
                return new LocalDate(source.getYear(), source.getMonth(), source.getDay());
            }
        });
    }
}
