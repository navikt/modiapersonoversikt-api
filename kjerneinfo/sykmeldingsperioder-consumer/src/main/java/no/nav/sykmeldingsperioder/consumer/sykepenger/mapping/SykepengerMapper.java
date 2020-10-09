package no.nav.sykmeldingsperioder.consumer.sykepenger.mapping;

import no.nav.kjerneinfo.common.domain.Periode;
import no.nav.kjerneinfo.common.utils.DateUtils;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerRequest;
import no.nav.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerResponse;
import no.nav.sykmeldingsperioder.domain.*;
import no.nav.sykmeldingsperioder.domain.sykepenger.*;
import no.nav.tjeneste.virksomhet.sykepenger.v2.informasjon.*;
import no.nav.tjeneste.virksomhet.sykepenger.v2.meldinger.FimHentSykepengerListeRequest;
import no.nav.tjeneste.virksomhet.sykepenger.v2.meldinger.FimHentSykepengerListeResponse;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

/**
 * Mapperklasse som benytter SykepengerFIMMapper for custom mapping.
 */
public class SykepengerMapper {
    private static Logger logger = LoggerFactory.getLogger(SykepengerMapper.class);
    private static SykepengerMapper instance = null;

    private SykepengerMapper() {
    }

    public static SykepengerMapper getInstance() {
        if (instance == null) {
            instance = new SykepengerMapper();
        }

        return instance;
    }

    public FimHentSykepengerListeRequest map(SykepengerRequest request) {
        return null;
    }

    public SykepengerResponse map(FimHentSykepengerListeResponse rawResponse) {
        return null;
    }

    public LocalDate map(XMLGregorianCalendar xmlDate) {
        return null;
    }

//    private static void registerFimHentSykepengerResponse(MapperFactory mapperFactory) {
//        mapperFactory.registerClassMap(mapperFactory.classMap(FimHentSykepengerListeResponse.class,
//                SykepengerResponse.class)
//                .field("sykmeldingsperiodeListe", "sykmeldingsperioder")
//                .byDefault()
//                .toClassMap());
//    }
//
//    private static void registerFimGradering(MapperFactory mapperFactory) {
//        mapperFactory.registerClassMap(mapperFactory.classMap(FimsykGradering.class, Gradering.class)
//                .byDefault()
//                .toClassMap());
//    }
//
//    private static void registerFimKommendeUtbetaling(MapperFactory mapperFactory) {
//        mapperFactory.registerClassMap(mapperFactory.classMap(FimsykHistoriskVedtak.class, KommendeUtbetaling.class)
//                .field("utbetalt", "utbetalingsdato")
//                .customize(new CustomMapper<FimsykHistoriskVedtak, KommendeUtbetaling>() {
//                    @Override
//                    public void mapAtoB(FimsykHistoriskVedtak from, KommendeUtbetaling to, MappingContext context) {
//                        if (from.getPeriodetype() != null) {
//                            to.setType(new Kodeverkstype(from.getPeriodetype().getKode(), from.getPeriodetype().getTermnavn()));
//                        }
//                    }
//                })
//                .byDefault()
//                .toClassMap());
//    }
//
//    private static void registerFimKommendeVedtak(MapperFactory mapperFactory) {
//        mapperFactory.registerClassMap(mapperFactory.classMap(FimsykKommendeVedtak.class, UtbetalingPaVent.class)
//                .byDefault()
//                .toClassMap());
//    }
//
//    private static void registerFimPeriode(MapperFactory mapperFactory) {
//        mapperFactory.registerClassMap(mapperFactory.classMap(FimsykPeriode.class, Periode.class)
//                .field("fom", "from")
//                .field("tom", "to")
//                .toClassMap());
//    }
//
//    private static void registerFimSykmelding(MapperFactory mapperFactory) {
//        mapperFactory.registerClassMap(mapperFactory.classMap(FimsykSykmelding.class, Sykmelding.class)
//                .byDefault()
//                .customize(new CustomMapper<FimsykSykmelding, Sykmelding>() {
//                    @Override
//                    public void mapAtoB(FimsykSykmelding from, Sykmelding to, MappingContext context) {
//
//                        List<FimsykGradering> graderinger = from.getGradAvSykmeldingListe();
//                        XMLGregorianCalendar lastTom = null;
//                        FimsykGradering lastGradering = null;
//                        for (FimsykGradering gradering : graderinger) {
//                            if (lastTom == null || lastTom.compare(gradering.getGradert().getTom()) < 0) {
//                                lastTom = gradering.getGradert().getTom();
//                                lastGradering = gradering;
//                            }
//                        }
//                        if (lastGradering != null) {
//                            to.setSykmeldingsgrad(mapperFacade.map(lastGradering.getSykmeldingsgrad(), Double.class));
//                        }
//                    }
//                })
//                .toClassMap());
//    }
//
//    private static void registerFimArbeidsforhold(MapperFactory mapperFactory) {
//        mapperFactory.registerClassMap(mapperFactory.classMap(FimsykArbeidsforhold.class, Arbeidsforhold.class)
//                .byDefault()
//                .toClassMap());
//    }
//
//    private static void registerFimBruker(MapperFactory mapperFactory) {
//        mapperFactory.registerClassMap(mapperFactory.classMap(FimsykBruker.class, Bruker.class)
//                .byDefault()
//                .toClassMap());
//    }
//
//    private static void registerFimYrkesskade(MapperFactory mapperFactory) {
//        mapperFactory.registerClassMap(mapperFactory.classMap(FimsykYrkesskade.class, Yrkesskade.class)
//                .byDefault()
//                .toClassMap());
//    }
//
//    private static void registerFimForsikring(MapperFactory mapperFactory) {
//        mapperFactory.registerClassMap(mapperFactory.classMap(FimsykForsikring.class, Forsikring.class)
//                .byDefault()
//                .toClassMap());
//    }
//
//    private static void registerFimKodeliste(MapperFactory mapperFactory) {
//        mapperFactory.registerClassMap(mapperFactory.classMap(FimsykKodeverdi.class, Kodeverkstype.class)
//                .byDefault()
//                .toClassMap());
//    }
//
//    private static void registerFimsykPeriodetype(MapperFactory mapperFactory) {
//        mapperFactory.registerClassMap(mapperFactory.classMap(FimsykPeriodetype.class, Kodeverkstype.class)
//                .byDefault()
//                .toClassMap());
//    }
//
//    private static void registerFimSykmeldingsperiode(MapperFactory mapperFactory) {
//        mapperFactory.registerClassMap(mapperFactory.classMap(FimsykSykmeldingsperiode.class, Sykmeldingsperiode.class)
//                .field("stansaarsak", "stansarsak")
//                .field("sykmeldt", "bruker")
//                .field("sykmeldingListe", "sykmeldinger")
//                .customize(new CustomMapper<FimsykSykmeldingsperiode, Sykmeldingsperiode>() {
//                    @Override
//                    public void mapAtoB(FimsykSykmeldingsperiode from, Sykmeldingsperiode to, MappingContext context) {
//                        List<HistoriskUtbetaling> historiskeUtbetalinger = new ArrayList<>();
//                        List<UtbetalingPaVent> utbetalingerPaaVent = new ArrayList<>();
//                        List<KommendeUtbetaling> kommendeUtbetalinger = new ArrayList<>();
//                        for (FimsykVedtak utbetaling : from.getVedtakListe()) {
//                            if (utbetaling instanceof FimsykHistoriskVedtak) {
//                                kommendeUtbetalinger.add(mapperFacade.map(utbetaling, KommendeUtbetaling.class));
//                            } else if (utbetaling instanceof FimsykKommendeVedtak) {
//                                UtbetalingPaVent utbetalingPaaVent = createUtbetalingPaVentObjekt(mapperFacade, from, utbetaling);
//                                utbetalingerPaaVent.add(utbetalingPaaVent);
//                            }
//                        }
//                        to.setUtbetalingerPaVent(utbetalingerPaaVent);
//                        to.setKommendeUtbetalinger(kommendeUtbetalinger);
//                        to.setHistoriskeUtbetalinger(historiskeUtbetalinger);
//                    }
//                })
//                .byDefault()
//                .toClassMap());
//    }
//
//    private static UtbetalingPaVent createUtbetalingPaVentObjekt(MapperFacade mapperFacade, FimsykSykmeldingsperiode from, FimsykVedtak utbetaling) {
//        UtbetalingPaVent utbetalingPaaVent = mapperFacade.map(utbetaling, UtbetalingPaVent.class);
//        FimsykArbeidskategori arbeidskategori = from.getArbeidskategori();
//        FimsykStansaarsak stansaarsak = from.getStansaarsak();
//        FimsykPeriode ferie1 = from.getFerie1();
//        FimsykPeriode ferie2 = from.getFerie2();
//        FimsykPeriode sanksjon = from.getSanksjon();
//        Optional<FimsykPeriode> sykmeldt = getSykmeldt(from);
//        if (arbeidskategori != null) {
//            utbetalingPaaVent.setArbeidskategori(new Kodeverkstype(arbeidskategori.getKode(), arbeidskategori.getTermnavn()));
//        }
//        if (stansaarsak != null) {
//            utbetalingPaaVent.setStansaarsak(new Kodeverkstype(stansaarsak.getKode(), stansaarsak.getTermnavn()));
//        }
//        if (ferie1 != null) {
//            LocalDate fra = getFraDato(ferie1);
//            LocalDate til = getTilDato(ferie1);
//            utbetalingPaaVent.setFerie1(new Periode(fra, til));
//        }
//        if (ferie2 != null) {
//            LocalDate fra = getFraDato(ferie2);
//            LocalDate til = getTilDato(ferie2);
//            utbetalingPaaVent.setFerie2(new Periode(fra, til));
//        }
//        if (sanksjon != null) {
//            LocalDate fra = getFraDato(sanksjon);
//            LocalDate til = getTilDato(sanksjon);
//            utbetalingPaaVent.setSanksjon(new Periode(fra, til));
//        }
//        if (sykmeldt.isPresent()) {
//            LocalDate fra = getFraDato(sykmeldt.get());
//            LocalDate til = getTilDato(sykmeldt.get());
//            utbetalingPaaVent.setSykmeldt(new Periode(fra, til));
//        }
//
//        return utbetalingPaaVent;
//    }
//
//    private static Optional<FimsykPeriode> getSykmeldt(FimsykSykmeldingsperiode from) {
//        if (from.getSykmeldingListe().isEmpty()) {
//            return empty();
//        }
//        return ofNullable(from.getSykmeldingListe().get(0).getSykmeldt());
//    }
//
//    private static LocalDate getFraDato(FimsykPeriode periode) {
//        LocalDate dato = null;
//        XMLGregorianCalendar fom = periode.getFom();
//        if (fom != null) {
//            dato = new LocalDate(fom.getYear(), fom.getMonth(), fom.getDay());
//        }
//        return dato;
//    }
//
//    private static LocalDate getTilDato(FimsykPeriode periode) {
//        LocalDate dato = null;
//        XMLGregorianCalendar fom = periode.getTom();
//        if (fom != null) {
//            dato = new LocalDate(fom.getYear(), fom.getMonth(), fom.getDay());
//        }
//        return dato;
//    }
//
//    private static void configureResponseConverters(ConverterFactory converterFactory) {
//
//        converterFactory.registerConverter(new CustomConverter<XMLGregorianCalendar, LocalDate>() {
//            @Override
//            public LocalDate convert(XMLGregorianCalendar source, Type<? extends LocalDate> destinationType, MappingContext mappingContext) {
//                return new LocalDate(source.getYear(), source.getMonth(), source.getDay());
//            }
//
//            @Override
//            public boolean canConvert(Type<?> sourceType, Type<?> destinationType) {
//                return this.sourceType.isAssignableFrom(sourceType) && this.destinationType.equals(destinationType);
//            }
//
//        });
//    }
//
//    private static void configureRequestConverters(ConverterFactory converterFactory) {
//        converterFactory.registerConverter(new CustomConverter<LocalDate, XMLGregorianCalendar>() {
//            @Override
//            public XMLGregorianCalendar convert(LocalDate source, Type<? extends XMLGregorianCalendar> destinationType, MappingContext mappingContext) {
//                try {
//                    return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(source.getYear(), source.getMonthOfYear(), source.getDayOfMonth(), 0);
//                } catch (DatatypeConfigurationException e) {
//                    logger.warn("DatatypeConfigurationException", e.getMessage());
//                    throw new ApplicationException("DatatypeConfigurationException", e, "Klarer ikke å lage dato");
//                }
//            }
//        });
//    }
//
//    private static void configureRequestClassMaps(MapperFactory mapperFactory) {
//        mapperFactory.registerClassMap(mapperFactory.classMap(SykepengerRequest.class, FimHentSykepengerListeRequest.class)
//                .field("ident", "ident")
//                .customize(new CustomMapper<SykepengerRequest, FimHentSykepengerListeRequest>() {
//                    @Override
//                    public void mapAtoB(SykepengerRequest from, FimHentSykepengerListeRequest to, MappingContext context) {
//                        FimsykPeriode fimPeriode = new FimsykPeriode();
//                        fimPeriode.setFom(DateUtils.convertDateToXmlGregorianCalendar(from.getFrom().toDate()));
//                        fimPeriode.setTom(DateUtils.convertDateToXmlGregorianCalendar(from.getTo().toDate()));
//                        to.setSykmelding(fimPeriode);
//                    }
//                })
//                .toClassMap());
//    }
}
