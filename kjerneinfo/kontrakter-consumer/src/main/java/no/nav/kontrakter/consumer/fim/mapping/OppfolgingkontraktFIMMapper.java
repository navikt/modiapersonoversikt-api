package no.nav.kontrakter.consumer.fim.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.metadata.Type;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktRequest;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktResponse;
import no.nav.kontrakter.domain.oppfolging.Bruker;
import no.nav.kontrakter.domain.oppfolging.SYFOPunkt;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.WSHentOppfoelgingskontraktListeRequest;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.WSHentOppfoelgingskontraktListeResponse;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

/**
 * Denne klassen beskriver custom-mapping av objekter fra FIM-typer som vi får inn fra oppfolginskontrakt-tjenesten til
 * domeneklassene vi har definert i kontrakter-domain. Mappingen utføres ved hjelp av rammeverket Orika.
 */
public final class OppfolgingkontraktFIMMapper {
    private static Logger logger = LoggerFactory.getLogger(OppfolgingkontraktFIMMapper.class);

    private OppfolgingkontraktFIMMapper() {
    }

    public static void configure(MapperFactory mapperFactory, ConverterFactory converterFactory) {
        configureRequestClassMaps(mapperFactory);
        configureResponseConverters(converterFactory);
        configureRequestConverters(converterFactory);
        configureResponseClassMaps(mapperFactory);
    }

    private static void configureResponseClassMaps(MapperFactory mapperFactory) {
        registerFimBruker(mapperFactory);
        registerFimBrukerKontrakt(mapperFactory);
        registerFimSYFOPunkt(mapperFactory);
        registerFimHentOppfolgningskontraktListeResponse(mapperFactory);
    }

    private static void registerFimHentOppfolgningskontraktListeResponse(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(
                mapperFactory.classMap(
                        WSHentOppfoelgingskontraktListeResponse.class,
                        OppfolgingskontraktResponse.class)
                        .customize(new CustomMapper<WSHentOppfoelgingskontraktListeResponse, OppfolgingskontraktResponse>() {
							@Override
							public void mapAtoB(WSHentOppfoelgingskontraktListeResponse from, OppfolgingskontraktResponse to, MappingContext context) {

								List<WSOppfoelgingskontrakt> oppfolgingskontraktListe = from.getOppfoelgingskontraktListe();
								if (!oppfolgingskontraktListe.isEmpty()) {
									mapperFacade.map(oppfolgingskontraktListe.get(0), to);
								}
								splitSYFOkontrakter(oppfolgingskontraktListe, to);
							}

							private void splitSYFOkontrakter(List<WSOppfoelgingskontrakt> oppfolgingskontraktListe, OppfolgingskontraktResponse to) {
								oppfolgingskontraktListe.stream()
                                        .filter((oppfoelgingskontrakt) -> oppfoelgingskontrakt instanceof WSSYFOkontrakt)
                                        .forEach((oppfoelgingskontrakt) ->
                                                customSyfoMapping((WSSYFOkontrakt) oppfoelgingskontrakt, to)
                                        );
							}

							private void customSyfoMapping(WSSYFOkontrakt syfoKontrakt, OppfolgingskontraktResponse to) {
								List<SYFOPunkt> syfoPunkter = new ArrayList<>();
								for (WSSYFOPunkt syfoPunkt : syfoKontrakt.getHarSYFOPunkt()) {
									syfoPunkter.add(mapperFacade.map(syfoPunkt, SYFOPunkt.class));
								}
								to.setSyfoPunkter(syfoPunkter);

								Bruker bruker = mapperFacade.map(syfoKontrakt.getGjelderBruker(), Bruker.class);
								bruker.setSykmeldtFrom(mapperFacade.map(syfoKontrakt.getSykmeldtFra(), LocalDate.class));
								to.setBruker(bruker);
							}
						})
                        .toClassMap());
    }

    private static void registerFimSYFOPunkt(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(WSSYFOPunkt.class, SYFOPunkt.class)
                .field("fastOppfoelgingspunkt", "fastOppfolgingspunkt")
                .field("SYFOHendelse", "syfoHendelse")
                .byDefault().toClassMap()
                );
    }

    private static void registerFimBruker(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(WSBruker.class, Bruker.class)
                .fieldAToB("meldeplikt", "meldeplikt")
                .fieldAToB("servicegruppe", "innsatsgruppe")
                .byDefault().toClassMap()
                );
    }

    /**
     * FIM returnerer samme bruker for hvert element i listen. Vi mapper første bruker til vårt domeneobjekt.
     */
    private static void registerFimBrukerKontrakt(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(WSOppfoelgingskontrakt.class, OppfolgingskontraktResponse.class)
                .field("gjelderBruker", "bruker")
                .field("ihtGjeldendeVedtak.vedtaksperiode.fom", "vedtaksdato")
                .byDefault().toClassMap()
                );
    }

    private static void configureResponseConverters(ConverterFactory converterFactory) {

        converterFactory.registerConverter(new CustomConverter<XMLGregorianCalendar, LocalDate>() {
            @Override
            public LocalDate convert(XMLGregorianCalendar source, Type<? extends LocalDate> destinationType, MappingContext mappingContext) {
                return new LocalDate(source.getYear(), source.getMonth(), source.getDay());
            }
        });

        converterFactory.registerConverter(new CustomConverter<XMLGregorianCalendar, LocalDate>() {
            @Override
            public LocalDate convert(XMLGregorianCalendar source, Type<? extends LocalDate> destinationType, MappingContext mappingContext) {
                return new LocalDate(source.getYear(), source.getMonth(), source.getDay());
            }

            @Override
            public boolean canConvert(Type<?> sourceType, Type<?> destinationType) {
                return this.sourceType.isAssignableFrom(sourceType) && this.destinationType.equals(destinationType);
            }
        });

        converterFactory.registerConverter(new CustomConverter<XMLGregorianCalendar, LocalDateTime>() {
            @Override
            public LocalDateTime convert(XMLGregorianCalendar source, Type<? extends LocalDateTime> destinationType, MappingContext mappingContext) {
                return LocalDateTime.fromCalendarFields(source.toGregorianCalendar());
            }
        });

        converterFactory.registerConverter(new CustomConverter<List<WSMeldeplikt>, Boolean>() {
            @Override
            public Boolean convert(List<WSMeldeplikt> source, Type<? extends Boolean> destinationType, MappingContext mappingContext) {
                Boolean meldeplikt = null;
                if (source != null && !source.isEmpty()) {
                    meldeplikt = source.get(0).isMeldeplikt();
                }
                return meldeplikt;
            }
        });

        converterFactory.registerConverter(new CustomConverter<List<WSServiceGruppe>, String>() {
            @Override
            public String convert(List<WSServiceGruppe> source, Type<? extends String> destinationType, MappingContext mappingContext) {
                if (source == null || source.isEmpty()) {
                    return null;
                }
                return source.get(0).getServiceGruppe();
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
        converterFactory.registerConverter(new CustomConverter<LocalDateTime, XMLGregorianCalendar>() {
            @Override
            public XMLGregorianCalendar convert(LocalDateTime source, Type<? extends XMLGregorianCalendar> destinationType, MappingContext mappingContext) {
                try {
                    return DatatypeFactory.newInstance().newXMLGregorianCalendar(source.getYear(), source.getMonthOfYear(), source.getDayOfMonth(), source.getHourOfDay(), source.getMinuteOfHour(),
                            source.getSecondOfMinute(), source.getMillisOfSecond(), 0);
                } catch (DatatypeConfigurationException e) {
                    logger.warn("DatatypeConfigurationException", e.getMessage());
                    throw new ApplicationException("DatatypeConfigurationException", e, "Klarer ikke å lage dato");
                }
            }
        });
    }

    private static void configureRequestClassMaps(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(OppfolgingskontraktRequest.class, WSHentOppfoelgingskontraktListeRequest.class)
                .field("fodselsnummer", "personidentifikator")
                .field("from", "periode.fom")
                .field("to", "periode.tom")
                .toClassMap());
    }
}
