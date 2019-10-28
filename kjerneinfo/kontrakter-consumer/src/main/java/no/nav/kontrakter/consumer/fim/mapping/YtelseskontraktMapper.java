package no.nav.kontrakter.consumer.fim.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.metadata.Type;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktRequest;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktResponse;
import no.nav.kontrakter.domain.ytelse.Dagpengeytelse;
import no.nav.kontrakter.domain.ytelse.Vedtak;
import no.nav.kontrakter.domain.ytelse.Ytelse;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.FimDagpengekontrakt;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.FimVedtak;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.FimYtelseskontrakt;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.meldinger.FimHentYtelseskontraktListeRequest;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.meldinger.FimHentYtelseskontraktListeResponse;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;

public class YtelseskontraktMapper extends ConfigurableMapper {
    private static Logger logger = LoggerFactory.getLogger(YtelseskontraktMapper.class);
    private static YtelseskontraktMapper instance = null;

    private YtelseskontraktMapper() {
    }

    public static YtelseskontraktMapper getInstance() {
        if (instance == null) {
            instance = new YtelseskontraktMapper();
        }

        return instance;
    }

    @Override
    public void configure(MapperFactory mapperFactory) {
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        configure(mapperFactory, converterFactory);
    }

    public static void configure(MapperFactory mapperFactory, ConverterFactory converterFactory) {
        configureResponseConverters(converterFactory);
        configureRequestConverters(converterFactory);
        configureRequestClassMaps(mapperFactory);
        configureResponseClassMaps(mapperFactory);

    }

    private static void configureResponseClassMaps(final MapperFactory mapperFactory) {
        registerFimHentYtelseskontraktListeResponse(mapperFactory);
        registerFimVedtak(mapperFactory);
    }

    private static void registerFimHentYtelseskontraktListeResponse(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(FimHentYtelseskontraktListeResponse.class, YtelseskontraktResponse.class)
                .field("bruker.rettighetsgruppe.rettighetsGruppe", "rettighetsgruppe")
                .field("ytelseskontraktListe", "ytelser")
                .toClassMap());
    }

    private static void registerFimVedtak(MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory.classMap(FimVedtak.class, Vedtak.class)
                .field("vedtaksperiode.fom", "activeFrom")
                .field("vedtaksperiode.tom", "activeTo")
                .field("status", "vedtakstatus")
                .field("beslutningsdato", "vedtaksdato")
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

        converterFactory.registerConverter(new CustomConverter<FimYtelseskontrakt, Ytelse>() {

            @Override
            public Ytelse convert(FimYtelseskontrakt source, Type<? extends Ytelse> destinationType, MappingContext mappingContext) {
                Ytelse ytelse;
                if (source instanceof FimDagpengekontrakt) {
                    ytelse = mapperFacade.map(source, Dagpengeytelse.class);
                } else {
                    ytelse = new Ytelse();
                }
                ytelse.setType(source.getYtelsestype());
                ytelse.setStatus(source.getStatus());
                ytelse.setDatoKravMottat(mapperFacade.map(source.getDatoKravMottatt(), LocalDate.class));
                ytelse.setDagerIgjenMedBortfall(source.getBortfallsprosentDagerIgjen());
                ytelse.setUkerIgjenMedBortfall(source.getBortfallsprosentUkerIgjen());

                ytelse.setFom(mapperFacade.map(source.getFomGyldighetsperiode(), LocalDate.class));
                ytelse.setTom(mapperFacade.map(source.getTomGyldighetsperiode(), LocalDate.class));
                ArrayList<Vedtak> vedtak = new ArrayList<>();
                for (FimVedtak fimVedtak : source.getIhtVedtak()) {
                    vedtak.add(mapperFacade.map(fimVedtak, Vedtak.class));
                }
                ytelse.setVedtak(vedtak);

                return ytelse;
            }
        });

        converterFactory.registerConverter(new CustomConverter<FimDagpengekontrakt, Dagpengeytelse>() {
            @Override
            public Dagpengeytelse convert(FimDagpengekontrakt source, Type<? extends Dagpengeytelse> destinationType, MappingContext mappingContext) {
                Dagpengeytelse dagpengeytelse = new Dagpengeytelse();
                dagpengeytelse.setAntallDagerIgjen(source.getAntallDagerIgjen());
                dagpengeytelse.setAntallUkerIgjen(source.getAntallUkerIgjen());
                dagpengeytelse.setAntallDagerIgjenPermittering(source.getAntallDagerIgjenUnderPermittering());
                dagpengeytelse.setAntallUkerIgjenPermittering(source.getAntallUkerIgjenUnderPermittering());

                return dagpengeytelse;
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
        mapperFactory.registerClassMap(mapperFactory.classMap(YtelseskontraktRequest.class, FimHentYtelseskontraktListeRequest.class)
                .field("fodselsnummer", "personidentifikator")
                .field("from", "periode.fom")
                .field("to", "periode.tom")
                .toClassMap());
    }
}
