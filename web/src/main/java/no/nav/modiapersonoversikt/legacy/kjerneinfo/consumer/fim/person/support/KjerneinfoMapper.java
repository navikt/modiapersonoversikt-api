package no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.support;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.metadata.Type;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Kodeverdi;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Periode;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.info.Bankkonto;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.info.BankkontoUtland;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.*;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.GeografiskTilknytning;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.Matrikkeladresse;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.Person;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.Postboksadresse;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.Stedsadresse;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.UstrukturertAdresse;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.fakta.Familierelasjon;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.fakta.Telefon;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.predicate.AdresseUtils;
import no.nav.modiapersonoversikt.consumer.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import no.nav.modiapersonoversikt.consumer.kodeverk.consumer.fim.kodeverk.to.feil.HentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class KjerneinfoMapper extends ConfigurableMapper {

    private KodeverkmanagerBi kodeverkmanagerBean;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public KjerneinfoMapper() {
    }

    public KjerneinfoMapper(KodeverkmanagerBi kodeverkmanagerBean) {
        this.kodeverkmanagerBean = kodeverkmanagerBean;
    }

    public void setKodeverkmanagerBean(KodeverkmanagerBi kodeverkmanagerBean) {
        this.kodeverkmanagerBean = kodeverkmanagerBean;
    }

    @Override
    public void configure(MapperFactory mapperFactory) {
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        configure(mapperFactory, converterFactory);
    }

    private void configure(MapperFactory mapperFactory, ConverterFactory converterFactory) {
        registerDateConverters(converterFactory);
        regiserBankkontoConverters(converterFactory);
        registerAdresseConverters(converterFactory);
        registerStrukturertadresseTyperConverter(converterFactory);
        registerMidlertidigAdresseConverters(converterFactory);
        registerKodeverdiConverter(converterFactory);

        mapResponse(mapperFactory);
        mapAktoer(mapperFactory);
        mapPerson(mapperFactory);
        mapBruker(mapperFactory);
        mapBankkonto(mapperFactory);
        mapBankkontoUtland(mapperFactory);
        mapFamilierelasjon(mapperFactory);
        mapTelefonummere(mapperFactory);
        mapGeografiskTilknytning(mapperFactory);
        mapTilrettelagtKommunikasjon(mapperFactory);
        mapUstrukturertAdresse(mapperFactory);
        mapMidlertidigPostadresse(mapperFactory);
    }

    private void registerDateConverters(ConverterFactory converterFactory) {
        converterFactory.registerConverter(new CustomConverter<LocalDateTime, XMLGregorianCalendar>() {
            @Override
            public XMLGregorianCalendar convert(LocalDateTime source, Type<? extends XMLGregorianCalendar> destinationType, MappingContext mappingContext) {
                try {
                    GregorianCalendar cal = new GregorianCalendar();
                    cal.setTime(source.toDate());
                    return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
                } catch (DatatypeConfigurationException ex) {
                    logger.warn("DatatypeConfigurationException", ex.getMessage());
                    return null;
                }
            }
        });

        converterFactory.registerConverter(new CustomConverter<XMLGregorianCalendar, LocalDateTime>() {
            @Override
            public LocalDateTime convert(XMLGregorianCalendar source, Type<? extends LocalDateTime> destinationType, MappingContext mappingContext) {
                return LocalDateTime.fromCalendarFields(source.toGregorianCalendar());
            }

            @Override
            public boolean canConvert(Type<?> sourceType, Type<?> destinationType) {
                return this.sourceType.isAssignableFrom(sourceType) && this.destinationType.equals(destinationType);
            }
        });

        converterFactory.registerConverter(new CustomConverter<LocalDate, XMLGregorianCalendar>() {
            @Override
            public XMLGregorianCalendar convert(LocalDate source, Type<? extends XMLGregorianCalendar> destinationType, MappingContext mappingContext) {
                try {
                    GregorianCalendar cal = new GregorianCalendar();
                    cal.setTime(source.toDate());
                    return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
                } catch (DatatypeConfigurationException ex) {
                    logger.warn("DatatypeConfigurationException", ex.getMessage());
                    return null;
                }
            }
        });

        converterFactory.registerConverter(new CustomConverter<XMLGregorianCalendar, LocalDate>() {
            @Override
            public LocalDate convert(XMLGregorianCalendar source, Type<? extends LocalDate> destinationType, MappingContext mappingContext) {
                return LocalDate.fromCalendarFields(source.toGregorianCalendar());
            }

            @Override
            public boolean canConvert(Type<?> sourceType, Type<?> destinationType) {
                return this.sourceType.isAssignableFrom(sourceType) && this.destinationType.equals(destinationType);
            }
        });

        converterFactory.registerConverter(new CustomConverter<Gyldighetsperiode, Periode>() {
            @Override
            public Periode convert(Gyldighetsperiode source, Type<? extends Periode> destinationType, MappingContext mappingContext) {
                Periode periode = new Periode();
                periode.setFrom(mapperFacade.map(source.getFom(), LocalDate.class));
                periode.setTo(mapperFacade.map(source.getTom(), LocalDate.class));
                return periode;
            }
        });

    }

    private void registerAdresseConverters(ConverterFactory converterFactory) {
        converterFactory.registerConverter(new CustomConverter<Adresse, GeografiskAdresse>() {
            @Override
            public GeografiskAdresse convert(Adresse adresse, Type<? extends GeografiskAdresse> type, MappingContext mappingContext) {
                return mapperFacade.map(adresse, Gateadresse.class);
            }
        });

        converterFactory.registerConverter(new CustomConverter<Bostedsadresse, Adresselinje>() {
            @Override
            public Adresselinje convert(Bostedsadresse source, Type<? extends Adresselinje> destinationType, MappingContext mappingContext) {
                Adresselinje adresselinje = mapperFacade.map(source.getStrukturertAdresse(), Adresselinje.class);
                LocalDateTime sistOppdatert = mapperFacade.map(source.getEndringstidspunkt(), LocalDateTime.class);
                Endringsinformasjon endringsinformasjon = new Endringsinformasjon();
                endringsinformasjon.setEndretAv(source.getEndretAv());
                endringsinformasjon.setSistOppdatert(sistOppdatert);
                adresselinje.setEndringsinformasjon(endringsinformasjon);

                return adresselinje;
            }
        });

        converterFactory.registerConverter(new CustomConverter<Adresse, Bostedsadresse>() {
            @Override
            public Bostedsadresse convert(Adresse source, Type<? extends Bostedsadresse> destinationType, MappingContext mappingContext) {
                Bostedsadresse bostedsadresse = new Bostedsadresse();
                bostedsadresse.setEndretAv(source.getEndringsinformasjon().getEndretAv());
                XMLGregorianCalendar xmlGregorianCalendar = mapperFacade.map(source.getEndringsinformasjon().getSistOppdatert(), XMLGregorianCalendar.class);
                bostedsadresse.setEndringstidspunkt(xmlGregorianCalendar);
                Gateadresse gateadresse = mapperFacade.map(source, Gateadresse.class);
                bostedsadresse.setStrukturertAdresse(gateadresse);
                return bostedsadresse;
            }
        });

        converterFactory.registerConverter(new CustomConverter<Postadresse, Adresselinje>() {
            @Override
            public Adresselinje convert(Postadresse source, Type<? extends Adresselinje> destinationType, MappingContext mappingContext) {
                Adresselinje adresselinje = mapperFacade.map(source.getUstrukturertAdresse(), Adresselinje.class);
                LocalDateTime sistOppdatert = mapperFacade.map(source.getEndringstidspunkt(), LocalDateTime.class);
                Endringsinformasjon endringsinformasjon = new Endringsinformasjon();
                endringsinformasjon.setEndretAv(source.getEndretAv());
                endringsinformasjon.setSistOppdatert(sistOppdatert);
                adresselinje.setEndringsinformasjon(endringsinformasjon);

                return adresselinje;
            }
        });

        converterFactory.registerConverter(new CustomConverter<StrukturertAdresse, Adresselinje>() {
            @Override
            public Adresselinje convert(StrukturertAdresse source, Type<? extends Adresselinje> destinationType, MappingContext mappingContext) {
                if (source instanceof Gateadresse) {
                    return mapperFacade.map(source, Adresse.class);
                } else if (source instanceof no.nav.tjeneste.virksomhet.person.v3.informasjon.Matrikkeladresse) {
                    return mapperFacade.map(source, Matrikkeladresse.class);
                } else if (source instanceof StedsadresseNorge) {
                    return mapperFacade.map(source, Stedsadresse.class);
                } else if (source instanceof PostboksadresseNorsk) {
                    return mapperFacade.map(source, Postboksadresse.class);
                } else {
                    return null;
                }
            }
        });

        converterFactory.registerConverter(new CustomConverter<UstrukturertAdresse, Adresselinje>() {
            @Override
            public Adresselinje convert(UstrukturertAdresse source, Type<? extends Adresselinje> destinationType, MappingContext mappingContext) {
                return mapperFacade.map(source, UstrukturertAdresse.class);
            }
        });
    }

    private void registerStrukturertadresseTyperConverter(ConverterFactory converterFactory) {

        converterFactory.registerConverter(new CustomConverter<Gateadresse, Adresselinje>() {
            @Override
            public Adresselinje convert(final Gateadresse source, Type<? extends Adresselinje> destinationType, MappingContext mappingContext) {
                Postnummer postnummer = source.getPoststed();
                Adresse adresse = new Adresse();
                String gatenummerString = source.getHusnummer() == null ? "" : String.valueOf(source.getHusnummer());
                adresse.setTilleggsadresse(source.getTilleggsadresse());
                adresse.setTilleggsadresseType(source.getTilleggsadresseType());
                adresse.setGatenavn(source.getGatenavn());
                adresse.setGatenummer(gatenummerString);
                adresse.setHusbokstav(source.getHusbokstav());
                adresse.setBolignummer(source.getBolignummer());
                adresse.setPostnummer(source.getPoststed().getValue());
                adresse.setPoststednavn(getPoststedFromPostnummer(postnummer));
                return adresse;
            }
        });
        converterFactory.registerConverter(new CustomConverter<no.nav.tjeneste.virksomhet.person.v3.informasjon.Matrikkeladresse, Adresselinje>() {
            @Override
            public Adresselinje convert(final no.nav.tjeneste.virksomhet.person.v3.informasjon.Matrikkeladresse source, Type<? extends Adresselinje> destinationType, MappingContext mappingContext) {
                Postnummer postnummer = source.getPoststed();
                String postnummerValue = postnummer == null ? "" : postnummer.getValue();

                return new Matrikkeladresse()
                        .withEiendomsnavn(source.getEiendomsnavn())
                        .withPostnummer(postnummerValue)
                        .withPoststed(getPoststedFromPostnummer(postnummer))
                        .withTilleggsadresse(source.getTilleggsadresse())
                        .withTilleggsadressetype(source.getTilleggsadresseType());
            }
        });

        converterFactory.registerConverter(new CustomConverter<StedsadresseNorge, Stedsadresse>() {
            @Override
            public Stedsadresse convert(final StedsadresseNorge source, Type<? extends Stedsadresse> destinationType, MappingContext mappingContext) {
                Postnummer postnummer = source.getPoststed();
                String postnummerValue = postnummer == null ? "" : postnummer.getValue();
                Stedsadresse stedsadresse = new Stedsadresse();
                stedsadresse.setAdressestring(AdresseUtils.spaceAppend(postnummerValue, getPoststedFromPostnummer(postnummer)));
                return stedsadresse;
            }
        });

        converterFactory.registerConverter(new CustomConverter<PostboksadresseNorsk, Postboksadresse>() {
            @Override
            public Postboksadresse convert(final PostboksadresseNorsk source, Type<? extends Postboksadresse> destinationType, MappingContext mappingContext) {
                Postnummer postnummer = source.getPoststed();
                String postnummerValue = postnummer == null ? "" : postnummer.getValue();
                Postboksadresse postboksadresse = new Postboksadresse();
                postboksadresse.setPostboksanlegg(source.getPostboksanlegg());
                postboksadresse.setPostboksnummer(source.getPostboksnummer());
                postboksadresse.setPoststed(postnummerValue);
                postboksadresse.setPoststednavn(getPoststedFromPostnummer(postnummer));
                postboksadresse.setTilleggsadresse(source.getTilleggsadresse());
                postboksadresse.setTilleggsadresseType(source.getTilleggsadresseType());
                return postboksadresse;
            }
        });
    }

    private void registerMidlertidigAdresseConverters(ConverterFactory converterFactory) {
        converterFactory.registerConverter(new CustomConverter<MidlertidigPostadresseNorge, Adresselinje>() {
            @Override
            public Adresselinje convert(MidlertidigPostadresseNorge source, Type<? extends Adresselinje> destinationType, MappingContext mappingContext) {
                return mapperFacade.map(source.getStrukturertAdresse(), Adresselinje.class);
            }
        });
    }

    private void regiserBankkontoConverters(ConverterFactory converterFactory) {
        converterFactory.registerConverter(new CustomConverter<BankkontoUtland, Bankkonto>() {
            @Override
            public Bankkonto convert(BankkontoUtland source, Type<? extends Bankkonto> destinationType, MappingContext mappingContext) {
                return mapperFacade.map(source, BankkontoUtland.class);
            }
        });

        converterFactory.registerConverter(new CustomConverter<no.nav.tjeneste.virksomhet.person.v3.informasjon.Bankkonto, Bankkonto>() {
            @Override
            public Bankkonto convert(no.nav.tjeneste.virksomhet.person.v3.informasjon.Bankkonto source, Type<? extends Bankkonto> destinationType, MappingContext mappingContext) {
                Endringsinformasjon endringsinformasjon = new Endringsinformasjon();
                endringsinformasjon.setEndretAv(source.getEndretAv());
                endringsinformasjon.setSistOppdatert(mapperFacade.map(source.getEndringstidspunkt(), LocalDateTime.class));

                if (source instanceof no.nav.tjeneste.virksomhet.person.v3.informasjon.BankkontoUtland) {
                    BankkontoUtland bankkontoUtland = mapperFacade.map(((no.nav.tjeneste.virksomhet.person.v3.informasjon.BankkontoUtland) source).getBankkontoUtland(), BankkontoUtland.class);
                    bankkontoUtland.setKontonummer(((no.nav.tjeneste.virksomhet.person.v3.informasjon.BankkontoUtland) source).getBankkontoUtland().getBankkontonummer());
                    bankkontoUtland.setEndringsinformasjon(endringsinformasjon);
                    return bankkontoUtland;
                } else {
                    Bankkonto bankkonto = mapperFacade.map(((BankkontoNorge) source).getBankkonto(), Bankkonto.class);
                    bankkonto.setKontonummer(((BankkontoNorge) source).getBankkonto().getBankkontonummer());
                    bankkonto.setEndringsinformasjon(endringsinformasjon);
                    return bankkonto;
                }
            }
        });
    }

    private String getPoststedFromPostnummer(Postnummer postnummer) {
        String kodeRef = postnummer == null ? "" : postnummer.getValue();
        String postnummerRef = postnummer == null ? "" : postnummer.getKodeverksRef().replace("http://nav.no/kodeverk/Kodeverk/", "");
        return poststedFromPostnummer(kodeRef, postnummerRef);
    }

    private String poststedFromPostnummer(String postnummerValue, String kodeverkref) {
        if (isBlank(postnummerValue)) {
            return "";
        } else {
            try {
                return kodeverkmanagerBean.getBeskrivelseForKode(postnummerValue, kodeverkref, "nb");
            } catch (HentKodeverkKodeverkIkkeFunnet hentKodeverkKodeverkIkkeFunnet) {
                logger.warn("HentKodeverkKodeverkIkkeFunnet ved kall på getBeskrivelseForKode", hentKodeverkKodeverkIkkeFunnet.getMessage());
                return "";
            }
        }
    }

    private void registerKodeverdiConverter(ConverterFactory converterFactory) {
        converterFactory.registerConverter(new CustomConverter<no.nav.tjeneste.virksomhet.person.v3.informasjon.Kodeverdi, Kodeverdi>() {
            @Override
            public Kodeverdi convert(no.nav.tjeneste.virksomhet.person.v3.informasjon.Kodeverdi source, Type<? extends Kodeverdi> destinationType, MappingContext mappingContext) {
                Kodeverdi result = new Kodeverdi();
                result.setKodeRef(source.getValue());
                String beskrivelse;
                try {
                    if (kodeverksrefIfHasKodeverksref(source) != null) {
                        beskrivelse = kodeverkmanagerBean.getBeskrivelseForKode(source.getValue(), kodeverksrefIfHasKodeverksref(source), "nb");
                    } else {
                        beskrivelse = source.getValue();
                    }
                } catch (HentKodeverkKodeverkIkkeFunnet hentKodeverkKodeverkIkkeFunnet) {
                    logger.warn("HentKodeverkKodeverkIkkeFunnet ved kall på getBeskrivelseForKode", hentKodeverkKodeverkIkkeFunnet.getMessage());
                    beskrivelse = source.getValue();
                }
                result.setBeskrivelse(beskrivelse);
                return result;
            }

            @Override
            public boolean canConvert(Type<?> sourceType, Type<?> destinationType) {
                return this.sourceType.isAssignableFrom(sourceType) && this.destinationType.equals(destinationType);
            }
        });
    }

    private static String kodeverksrefIfHasKodeverksref(no.nav.tjeneste.virksomhet.person.v3.informasjon.Kodeverdi kodeverdi) {
        if (kodeverdi instanceof Personstatuser) {
            return ((Personstatuser) kodeverdi).getKodeverksRef().replace("http://nav.no/kodeverk/Kodeverk/", "");
        } else if (kodeverdi instanceof Sivilstander) {
            return ((Sivilstander) kodeverdi).getKodeverksRef().replace("http://nav.no/kodeverk/Kodeverk/", "");
        } else if (kodeverdi instanceof Kjoennstyper) {
            return ((Kjoennstyper) kodeverdi).getKodeverksRef().replace("http://nav.no/kodeverk/Kodeverk/", "");
        } else if (kodeverdi instanceof Landkoder) {
            return ((Landkoder) kodeverdi).getKodeverksRef().replace("http://nav.no/kodeverk/Kodeverk/", "");
        } else if (kodeverdi instanceof no.nav.tjeneste.virksomhet.person.v3.informasjon.Diskresjonskoder) {
            return ((no.nav.tjeneste.virksomhet.person.v3.informasjon.Diskresjonskoder) kodeverdi).getKodeverksRef().replace("http://nav.no/kodeverk/Kodeverk/", "");
        } else {
            return null;
        }

    }

    private static void mapBankkontoUtland(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(no.nav.tjeneste.virksomhet.person.v3.informasjon.BankkontoUtland.class, BankkontoUtland.class)
                .field("bankkontoUtland.bankkontonummer", "kontonummer")
                .field("bankkontoUtland.banknavn", "banknavn")
                .field("bankkontoUtland.swift", "swift")
                .field("bankkontoUtland.landkode", "landkode")
                .field("bankkontoUtland.bankkode", "bankkode")
                .field("bankkontoUtland.bankadresse", "bankadresse")
                .field("bankkontoUtland.valuta", "valuta")
                .byDefault().toClassMap());
    }

    private static void mapBankkonto(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(BankkontoNorge.class, Bankkonto.class)
                .field("bankkonto.bankkontonummer", "kontonummer")
                .field("bankkonto.banknavn", "banknavn")
                .byDefault().toClassMap());
    }

    private static void mapResponse(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(HentPersonResponse.class, HentKjerneinformasjonResponse.class)
                .byDefault().toClassMap());
    }

    private static void mapFamilierelasjon(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(no.nav.tjeneste.virksomhet.person.v3.informasjon.Familierelasjon.class, Familierelasjon.class)
                .field("tilRolle.value", "tilRolle")
                .byDefault().toClassMap());
    }

    private static void mapBruker(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(Bruker.class, Person.class)
                .field("diskresjonskode", "personfakta.diskresjonskode")
                .field("harFraRolleI", "personfakta.harFraRolleIList")
                .field("foedested", "personfakta.fodested")
                .field("midlertidigPostadresse", "personfakta.alternativAdresse")
                .field("bankkonto", "personfakta.bankkonto")
                .field("sikkerhetstiltak.periode.fom", "personfakta.sikkerhetstiltak.periode.from")
                .field("sikkerhetstiltak.periode.tom", "personfakta.sikkerhetstiltak.periode.to")
                .field("sikkerhetstiltak.sikkerhetstiltaksbeskrivelse", "personfakta.sikkerhetstiltak.sikkerhetstiltaksbeskrivelse")
                .field("sikkerhetstiltak.sikkerhetstiltakskode", "personfakta.sikkerhetstiltak.sikkerhetstiltakskode")
                .field("geografiskTilknytning", "personfakta.geografiskTilknytning")
                .field("kontaktinformasjon", "personfakta.kontaktinformasjon")
                .field("tilrettelagtKommunikasjon", "personfakta.tilrettelagtKommunikasjon")
                .field("gjeldendePostadressetype", "personfakta.gjeldendePostadressetype")
                .byDefault().toClassMap());
    }

    private static void mapAktoer(MapperFactory mapperFactory) {
        mapperFactory.getConverterFactory().registerConverter(new CustomConverter<Aktoer, Fodselsnummer>() {
            @Override
            public Fodselsnummer convert(Aktoer aktoer, Type<? extends Fodselsnummer> type, MappingContext mappingContext) {
                return new Fodselsnummer(((PersonIdent) aktoer).getIdent().getIdent());
            }
        });

        mapperFactory.getConverterFactory().registerConverter(new CustomConverter<Fodselsnummer, Aktoer>() {
            @Override
            public Aktoer convert(Fodselsnummer fodselsnummer, Type<? extends Aktoer> type,MappingContext mappingContext) {
                return new PersonIdent().withIdent(new NorskIdent().withIdent(fodselsnummer.getNummer()));
            }
        });
    }

    private static void mapTelefonummere(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(Telefonnummer.class, Telefon.class)
                .byDefault().toClassMap());
    }

    private static void mapTilrettelagtKommunikasjon(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(TilrettelagtKommunikasjonbehov.class, Kodeverdi.class)
                .field("behov", "beskrivelse")
                .field("tilrettelagtKommunikasjon.value", "kodeRef"));
    }

    private static void mapPerson(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person.class, Person.class)
                .field("diskresjonskode", "personfakta.diskresjonskode")
                .field("aktoer", "fodselsnummer")
                .field("personnavn", "personfakta.personnavn")
                .field("bostedsadresse", "personfakta.bostedsadresse")
                .field("postadresse", "personfakta.postadresse")
                .field("kjoenn.kjoenn", "personfakta.kjonn")
                .field("sivilstand.sivilstand", "personfakta.sivilstand")
                .field("sivilstand.fomGyldighetsperiode", "personfakta.sivilstandFom")
                .field("personstatus.personstatus", "personfakta.bostatus")
                .field("doedsdato.doedsdato", "personfakta.doedsdato")
                .field("statsborgerskap.land", "personfakta.statsborgerskap")
                .byDefault().toClassMap());
    }

    private static void mapGeografiskTilknytning(MapperFactory mapperFactory) {
        mapperFactory.getConverterFactory().registerConverter(new CustomConverter<no.nav.tjeneste.virksomhet.person.v3.informasjon.GeografiskTilknytning, GeografiskTilknytning>() {
            @Override
            public GeografiskTilknytning convert(no.nav.tjeneste.virksomhet.person.v3.informasjon.GeografiskTilknytning source, Type<? extends GeografiskTilknytning> destinationType, MappingContext mappingContext) {
                GeografiskTilknytning geografiskTilknytning = new GeografiskTilknytning().withValue(source.getGeografiskTilknytning());
                if (source instanceof Bydel) {
                    geografiskTilknytning.withType(GeografiskTilknytningstyper.BYDEL);
                } else if (source instanceof Land) {
                    geografiskTilknytning.withType(GeografiskTilknytningstyper.LAND);
                } else if (source instanceof Kommune) {
                    geografiskTilknytning.withType(GeografiskTilknytningstyper.KOMMUNE);
                } else {
                    throw new IllegalArgumentException("Unrecognized type WSGeografiskTilknytning");
                }
                return geografiskTilknytning;
            }
        });
    }

    private static void mapUstrukturertAdresse(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(no.nav.tjeneste.virksomhet.person.v3.informasjon.UstrukturertAdresse.class, UstrukturertAdresse.class)
                .byDefault().toClassMap());
    }

    private static void mapMidlertidigPostadresse(MapperFactory mapperFactory) {
        mapperFactory.getConverterFactory().registerConverter(new CustomConverter<MidlertidigPostadresse, Adresselinje>() {
            @Override
            public Adresselinje convert(MidlertidigPostadresse source, Type<? extends Adresselinje> destinationType, MappingContext mappingContext) {
                if (source instanceof MidlertidigPostadresseNorge) {
                    if (((MidlertidigPostadresseNorge) source).getStrukturertAdresse() instanceof PostboksadresseNorsk) {
                        Postboksadresse postboksadresse = mapperFacade.map(((MidlertidigPostadresseNorge) source).getStrukturertAdresse(), Postboksadresse.class);
                        postboksadresse.setEndringsinformasjon(getEndringsinformasjon(mapperFacade, source));
                        postboksadresse.setPostleveringsPeriode(mapperFacade.map(source.getPostleveringsPeriode(), Periode.class));
                        return postboksadresse;
                    } else if (((MidlertidigPostadresseNorge) source).getStrukturertAdresse() instanceof no.nav.tjeneste.virksomhet.person.v3.informasjon.Matrikkeladresse) {
                        Adresselinje matrikkeladresse = mapperFacade.map(((MidlertidigPostadresseNorge) source).getStrukturertAdresse(), Adresselinje.class);
                        matrikkeladresse.setEndringsinformasjon(getEndringsinformasjon(mapperFacade, source));
                        ((Matrikkeladresse) matrikkeladresse).setPostleveringsPeriode(mapperFacade.map(source.getPostleveringsPeriode(), Periode.class));
                        return matrikkeladresse;
                    }
                    Adresselinje adresse = mapperFacade.map((MidlertidigPostadresseNorge) source, Adresselinje.class);
                    ((Adresse) adresse).setPostleveringsPeriode(mapperFacade.map(source.getPostleveringsPeriode(), Periode.class));
                    adresse.setEndringsinformasjon(getEndringsinformasjon(mapperFacade, source));
                    return adresse;
                } else {
                    AlternativAdresseUtland alternativAdresseUtland = mapperFacade.map(((MidlertidigPostadresseUtland) source).getUstrukturertAdresse(), AlternativAdresseUtland.class);
                    alternativAdresseUtland.setPostleveringsPeriode(mapperFacade.map(source.getPostleveringsPeriode(), Periode.class));
                    alternativAdresseUtland.setEndringsinformasjon(getEndringsinformasjon(mapperFacade, source));
                    no.nav.tjeneste.virksomhet.person.v3.informasjon.Kodeverdi kodeverdi = ((MidlertidigPostadresseUtland) source).getUstrukturertAdresse().getLandkode();
                    alternativAdresseUtland.setLandkode(mapperFacade.map(kodeverdi, Kodeverdi.class));
                    return alternativAdresseUtland;
                }
            }
        });
    }

    private static Endringsinformasjon getEndringsinformasjon(MapperFacade mapperFacade, MidlertidigPostadresse source) {
        Endringsinformasjon endringsinformasjon = new Endringsinformasjon();
        endringsinformasjon.setEndretAv(source.getEndretAv());
        endringsinformasjon.setSistOppdatert(mapperFacade.map(source.getEndringstidspunkt(), LocalDateTime.class));
        return endringsinformasjon;
    }
}
