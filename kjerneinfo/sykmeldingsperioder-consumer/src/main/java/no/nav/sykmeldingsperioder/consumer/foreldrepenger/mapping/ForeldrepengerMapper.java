package no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.metadata.Type;
import no.nav.kjerneinfo.common.domain.Periode;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeRequest;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeResponse;
import no.nav.sykmeldingsperioder.domain.*;
import no.nav.sykmeldingsperioder.domain.foreldrepenger.Adopsjon;
import no.nav.sykmeldingsperioder.domain.foreldrepenger.Foedsel;
import no.nav.sykmeldingsperioder.domain.foreldrepenger.Foreldrepengeperiode;
import no.nav.sykmeldingsperioder.domain.foreldrepenger.Foreldrepengerettighet;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.informasjon.*;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.meldinger.FimHentForeldrepengerettighetRequest;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.meldinger.FimHentForeldrepengerettighetResponse;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapperklasse
 */
public class ForeldrepengerMapper extends ConfigurableMapper {
    private static Logger logger = LoggerFactory.getLogger(ForeldrepengerMapper.class);
    private static ForeldrepengerMapper instance = null;

    private ForeldrepengerMapper() {
    }

    public static ForeldrepengerMapper getInstance() {
        if (instance == null) {
            instance = new ForeldrepengerMapper();
        }

        return instance;
    }

    @Override
    public void configure(MapperFactory mapperFactory) {
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        configure(mapperFactory, converterFactory);
    }

    private static void configure(MapperFactory mapperFactory, ConverterFactory converterFactory) {
        configureRequestClassMaps(mapperFactory);
        configureResponseConverters(converterFactory);
        configureRequestConverters(converterFactory);
        configureResponseClassMaps(mapperFactory);
    }

    private static void configureResponseClassMaps(MapperFactory mapperFactory) {
        registerFimHentForeldrepengerListeResponse(mapperFactory);

        registerFimAdopsjon(mapperFactory);
        registerFimArbeidsforhold(mapperFactory);
        registerFimBruker(mapperFactory);
        registerFimFoedsel(mapperFactory);
        registerFimForeldrepengeperiode(mapperFactory);
        registrerFimForeldrepengerettighet(mapperFactory);
        registerFimHistoriskUtbetaling(mapperFactory);
        registerFimKodeliste(mapperFactory);
        registerFimKommendeUtbetaling(mapperFactory);
        registerFimPeriode(mapperFactory);
    }

    private static void registerFimHentForeldrepengerListeResponse(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(FimHentForeldrepengerettighetResponse.class,
                ForeldrepengerListeResponse.class)
                .customize(new CustomMapper<FimHentForeldrepengerettighetResponse, ForeldrepengerListeResponse>() {
                    @Override
                    public void mapAtoB(FimHentForeldrepengerettighetResponse from, ForeldrepengerListeResponse to, MappingContext context) {
                        FimForeldrepengerettighet foreldrepengerettighet = from.getForeldrepengerettighet();
                        if (foreldrepengerettighet instanceof FimAdopsjon) {
                            to.setForeldrepengerettighet(mapperFacade.map(foreldrepengerettighet, Adopsjon.class));
                        } else if (foreldrepengerettighet instanceof FimFoedsel) {
                            to.setForeldrepengerettighet(mapperFacade.map(foreldrepengerettighet, Foedsel.class));
                        }
                    }
                })
                .byDefault()
                .toClassMap());
    }

    private static void registerFimAdopsjon(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(FimAdopsjon.class, Adopsjon.class)
                .customize(new CustomMapper<FimAdopsjon, Adopsjon>() {
                    @Override
                    public void mapAtoB(FimAdopsjon from, Adopsjon to, MappingContext context) {
                        to.setAndreForeldersFnr(from.getAndreForelder().getIdent());
                    }
                })
                .byDefault()
                .field("omsorgsovertakelse", "rettighetFom")
                .toClassMap()
        );
    }

    private static void registerFimFoedsel(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(FimFoedsel.class, Foedsel.class)
                .customize(new CustomMapper<FimFoedsel, Foedsel>() {
                    @Override
                    public void mapAtoB(FimFoedsel from, Foedsel to, MappingContext context) {
                        if (from.getAndreForelder() != null) {
                            to.setAndreForeldersFnr(from.getAndreForelder().getIdent());
                        }
                    }
                })
                .byDefault()
                .field("termin", "rettighetFom")
                .toClassMap()
        );
    }

    private static void registerFimForeldrepengeperiode(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(FimForeldrepengeperiode.class, Foreldrepengeperiode.class)
                .customize(new CustomMapper<FimForeldrepengeperiode, Foreldrepengeperiode>() {
                    @Override
                    public void mapAtoB(FimForeldrepengeperiode from, Foreldrepengeperiode to, MappingContext context) {
                        List<HistoriskUtbetaling> historiskUtbetalinger = new ArrayList<>();
                        List<KommendeUtbetaling> kommendeUtbetalinger = new ArrayList<>();
                        for (FimVedtak utbetaling : from.getVedtakListe()) {
                            if (utbetaling instanceof FimHistoriskVedtak) {
                                kommendeUtbetalinger.add(mapperFacade.map(utbetaling, KommendeUtbetaling.class));
                            }
                        }
                        to.setKommendeUtbetalinger(kommendeUtbetalinger);
                    }
                })
                .field("rettTilMoedrekvote", "rettTilModrekvote")
                .field("forskyvet1", "forskyvelsesperiode")
                .field("forskyvet2", "forskyvelsesperiode2")
                .byDefault()
                .toClassMap()
        );
    }

    private static void registrerFimForeldrepengerettighet(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(FimForeldrepengerettighet.class, Foreldrepengerettighet.class)
                .field("foreldrepengeperiodeListe", "periode")
                .field("barnetFoedt", "barnetsFoedselsdato")
                .byDefault()
                .toClassMap()
        );
    }

    private static void registerFimHistoriskUtbetaling(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(FimHistoriskVedtak.class, KommendeUtbetaling.class)
                .field("utbetalt", "utbetalingsdato")
                .byDefault()
                .toClassMap()
        );
    }

    private static void registerFimKommendeUtbetaling(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(FimHistoriskVedtak.class, KommendeUtbetaling.class)
                .field("utbetalt", "utbetalingsdato")
                .customize(new CustomMapper<FimHistoriskVedtak, KommendeUtbetaling>() {
                    @Override
                    public void mapAtoB(FimHistoriskVedtak from, KommendeUtbetaling to, MappingContext context) {
                        if (from.getPeriodetype() != null) {
                            to.setType(new Kodeverkstype(from.getPeriodetype().getKode(), from.getPeriodetype().getTermnavn()));
                        }
                    }
                })
                .byDefault()
                .toClassMap()
        );
    }

    private static void registerFimPeriode(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(FimPeriode.class, Periode.class)
                .field("fom", "from")
                .field("tom", "to")
                .toClassMap()
        );
    }

    private static void registerFimArbeidsforhold(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(FimArbeidsforhold.class, Arbeidsforhold.class)
                .byDefault()
                .toClassMap()
        );
    }

    private static void registerFimBruker(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(FimPerson.class, Bruker.class)
                .byDefault()
                .toClassMap()
        );
    }


    private static void registerFimKodeliste(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(FimKodeverdi.class, Kodeverkstype.class)
                .byDefault()
                .toClassMap()
        );
    }

    private static void configureResponseConverters(ConverterFactory converterFactory) {

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

    private static void configureRequestClassMaps(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(ForeldrepengerListeRequest.class, FimHentForeldrepengerettighetRequest.class)
                .byDefault()
                .toClassMap());
    }
}
