package no.nav.behandlebrukerprofil.consumer.support.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.metadata.Type;
import no.nav.behandlebrukerprofil.consumer.messages.BehandleBrukerprofilRequest;
import no.nav.brukerprofil.domain.Bankkonto;
import no.nav.brukerprofil.domain.BankkontoUtland;
import no.nav.brukerprofil.domain.Bruker;
import no.nav.brukerprofil.domain.Telefon;
import no.nav.brukerprofil.domain.adresser.*;
import no.nav.kjerneinfo.common.domain.Kodeverdi;
import no.nav.kjerneinfo.common.domain.Periode;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.informasjon.*;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.meldinger.FimOppdaterKontaktinformasjonOgPreferanserRequest;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

public class BehandleBrukerprofilMapper extends ConfigurableMapper {
    private static Logger logger = LoggerFactory.getLogger(BehandleBrukerprofilMapper.class);
    private static BehandleBrukerprofilMapper instance = null;

    private BehandleBrukerprofilMapper() {}

    public static BehandleBrukerprofilMapper getInstance() {
        if (instance == null) {
            instance = new BehandleBrukerprofilMapper();
        }

        return instance;
    }

    @Override
    public void configure(MapperFactory mapperFactory) {

        configureConverters(mapperFactory.getConverterFactory());

        mapperFactory.classMap(BehandleBrukerprofilRequest.class, FimOppdaterKontaktinformasjonOgPreferanserRequest.class)
                .field("bruker", "bruker")
                .customize(
                        new CustomMapper<BehandleBrukerprofilRequest, FimOppdaterKontaktinformasjonOgPreferanserRequest>() {
                            @Override
                            public void mapAtoB(BehandleBrukerprofilRequest from, FimOppdaterKontaktinformasjonOgPreferanserRequest to, MappingContext context) {
                                FimBruker destinationObject = new FimBruker();
                                mapperFacade.map(from.getBruker(), destinationObject);
                                to.setBruker(destinationObject);
                            }
                        })
                .byDefault().register();

        mapBrukerToFimBruker(mapperFactory);
        mapUstrukturertAdresseToFimMidlertidigPostadresseUtland(mapperFactory);
        mapStrukturertAdresseToFimMidlertidigPostAdresseNorge(mapperFactory);
        mapGataadresseToFimGateadresse(mapperFactory);
        mapMatrikkeladresseToFimMatrikkeladresse(mapperFactory);
        mapPostboksadresseToFimPostboksadresse(mapperFactory);

        mapperFactory.classMap(Kodeverdi.class, FimKodeverdi.class)
                .field("kodeRef", "value")
                .byDefault().register();

        mapperFactory.classMap(Bankkonto.class, FimBankkontoNorge.class)
                .field("kontonummer", "bankkonto.bankkontonummer")
                .field("banknavn", "bankkonto.banknavn")
                .byDefault().register();

        mapperFactory.classMap(BankkontoUtland.class, FimBankkontoUtland.class)
                .field("kontonummer", "bankkontoUtland.bankkontonummer")
                .field("banknavn", "bankkontoUtland.banknavn")
                .field("swift", "bankkontoUtland.swift")
                .field("bankkode", "bankkontoUtland.bankkode")
                .field("bankadresse", "bankkontoUtland.bankadresse")
                .customize(
                        new CustomMapper<BankkontoUtland, FimBankkontoUtland>() {
                            @Override
                            public void mapAtoB(BankkontoUtland from, FimBankkontoUtland to, MappingContext context) {
                                FimBankkontonummerUtland bankkontoUtland = to.getBankkontoUtland();
                                if (bankkontoUtland == null) {
                                    bankkontoUtland = new FimBankkontonummerUtland();
                                    to.setBankkontoUtland(bankkontoUtland);
                                }
                                if (from.getLandkode() != null && bankkontoUtland.getBankadresse() == null) {
                                    bankkontoUtland.setBankadresse(new FimUstrukturertAdresse());
                                    bankkontoUtland.getBankadresse().setLandkode(mapperFacade.map(from.getLandkode(), FimLandkoder.class));
                                }
                                bankkontoUtland.setLandkode(mapperFacade.map(from.getLandkode(), FimLandkoder.class));
                                bankkontoUtland.setValuta(mapperFacade.map(from.getValuta(), FimValutaer.class));
                            }
                        })
                .byDefault().register();

        mapperFactory.classMap(UstrukturertAdresse.class, FimUstrukturertAdresse.class)
                .field("adresselinje1", "adresselinje1")
                .field("adresselinje2", "adresselinje2")
                .field("adresselinje3", "adresselinje3")
                .byDefault().register();

        mapperFactory.classMap(Telefon.class, FimTelefonnummer.class)
                .field("retningsnummer", "retningsnummer")
                .field("type", "type")
                .field("identifikator", "identifikator")
                .byDefault().register();

        mapperFactory.classMap(Periode.class, FimGyldighetsperiode.class)
                .customize(
                        new CustomMapper<Periode, FimGyldighetsperiode>() {
                            @Override
                            public void mapAtoB(Periode from, FimGyldighetsperiode to, MappingContext context) {
                                to.setFom(convertLocalDateToXML(from.getFrom()));
                                to.setTom(convertLocalDateToXML(from.getTo()));
                            }
                        })
                .byDefault().register();
    }

    private XMLGregorianCalendar convertLocalDateToXML(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(localDate.toDate());
        XMLGregorianCalendar xmlCalTo;
        try {
            xmlCalTo = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException e) {
            logger.warn("DatatypeConfigurationException", e.getMessage());
            throw new ApplicationException("DatatypeConfigurationException", e, "Mottok dato på feil format");
        }
        return xmlCalTo;
    }

    private void mapPostboksadresseToFimPostboksadresse(MapperFactory mapperFactory) {
        mapperFactory.classMap(Postboksadresse.class, FimPostboksadresseNorsk.class)
                .field("tilleggsadresseType", "tilleggsadresseType")
                .field("tilleggsadresse", "tilleggsadresse")
                .field("postboksanlegg", "postboksanlegg")
                .field("poststed", "poststed.value")
                .field("landkode", "landkode.value")
                .customize(new CustomMapper<Postboksadresse, FimPostboksadresseNorsk>() {
                    @Override
                    public void mapAtoB(Postboksadresse from, FimPostboksadresseNorsk to, MappingContext context) {
                        to.setPostboksnummer(from.getPostboksnummer().trim());
                    }
                })
                .byDefault().register();
    }

    private void mapMatrikkeladresseToFimMatrikkeladresse(MapperFactory mapperFactory) {
        mapperFactory.classMap(Matrikkeladresse.class, FimMatrikkeladresse.class)
                .field("tilleggsadresseType", "tilleggsadresseType")
                .field("tilleggsadresse", "tilleggsadresse")
                .field("eiendomsnavn", "eiendomsnavn")
                .field("bolignummer", "bolignummer")
                .field("poststed", "poststed.value")
                .field("landkode", "landkode.value")
                .byDefault().register();
    }

    private void mapGataadresseToFimGateadresse(MapperFactory mapperFactory) {
        mapperFactory.classMap(Gateadresse.class, FimGateadresse.class)
                .field("tilleggsadresseType", "tilleggsadresseType")
                .field("tilleggsadresse", "tilleggsadresse")
                .field("gatenavn", "gatenavn")
                .field("husbokstav", "husbokstav")
                .field("bolignummer", "bolignummer")
                .field("poststed", "poststed.value")
                .field("landkode", "landkode.value")
                .byDefault().register();
    }

    private void mapBrukerToFimBruker(MapperFactory mapperFactory) {
        mapperFactory.classMap(Bruker.class, FimBruker.class)
                .field("tilrettelagtKommunikasjon{kodeRef}", "tilrettelagtKommunikasjon{behov}")
                .customize(
                        new CustomMapper<Bruker, FimBruker>() {
                            @Override
                            public void mapAtoB(Bruker from, FimBruker to, MappingContext context) {
                                FimPreferanser fimPreferanser = new FimPreferanser();
                                if (from.getMidlertidigadresseNorge() != null) {
                                    to.setMidlertidigPostadresse(mapperFacade.map(from.getMidlertidigadresseNorge(), FimMidlertidigPostadresseNorge.class));
                                } else if (from.getMidlertidigadresseUtland() != null) {
                                    to.setMidlertidigPostadresse(mapperFacade.map(from.getMidlertidigadresseUtland(), FimMidlertidigPostadresseUtland.class));
                                }
                                to.setPreferanser(fimPreferanser);
                                to.withKontaktinformasjon(konverterTelefonnumre(from.getMobil(), from.getHjemTlf(), from.getJobbTlf()));
                                to.setIdent(new FimNorskIdent().withIdent(from.getIdent()));
                            }

                            private List<FimTelefonnummer> konverterTelefonnumre(final Telefon... telefonnumre) {
                                if (telefonnumre == null || telefonnumre.length == 0) {
                                    return Collections.emptyList();
                                }

                                final List<FimTelefonnummer> konverterteTelefonnumre = new ArrayList<>();
                                for (Telefon telefonnummer : telefonnumre) {
                                    if (telefonnummer != null) {
                                        konverterteTelefonnumre.add(mapperFacade.map(telefonnummer, FimTelefonnummer.class));
                                    }
                                }
                                return konverterteTelefonnumre;
                            }

                        })
                .byDefault().register();
    }

    private void mapStrukturertAdresseToFimMidlertidigPostAdresseNorge(MapperFactory mapperFactory) {
        mapperFactory.classMap(StrukturertAdresse.class, FimMidlertidigPostadresseNorge.class)
                .customize(
                        new CustomMapper<StrukturertAdresse, FimMidlertidigPostadresseNorge>() {
                            @Override
                            public void mapAtoB(StrukturertAdresse from, FimMidlertidigPostadresseNorge to, MappingContext context) {
                                to.setPostleveringsPeriode(mapperFacade.map(from.getPostleveringsPeriode(), FimGyldighetsperiode.class));
                                if (from instanceof Gateadresse) {
                                    to.setStrukturertAdresse(mapperFacade.map(from, FimGateadresse.class));
                                } else if (from instanceof Matrikkeladresse) {
                                    to.setStrukturertAdresse(mapperFacade.map(from, FimMatrikkeladresse.class));
                                } else if (from instanceof Postboksadresse) {
                                    to.setStrukturertAdresse(mapperFacade.map(from, FimPostboksadresseNorsk.class));
                                }

                            }
                        })
                .byDefault().register();
    }

    private void mapUstrukturertAdresseToFimMidlertidigPostadresseUtland(MapperFactory mapperFactory) {
        mapperFactory.classMap(UstrukturertAdresse.class, FimMidlertidigPostadresseUtland.class)
                .field("adresselinje1", "ustrukturertAdresse.adresselinje1")
                .field("adresselinje2", "ustrukturertAdresse.adresselinje2")
                .field("adresselinje3", "ustrukturertAdresse.adresselinje3")
                .field("landkode", "ustrukturertAdresse.landkode")
                .field("postleveringsPeriode.from", "postleveringsPeriode.fom")
                .field("postleveringsPeriode.to", "postleveringsPeriode.tom")
                .byDefault().register();
    }

    private static void configureConverters(ConverterFactory converterFactory) {
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
}
