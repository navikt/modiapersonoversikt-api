package no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.foreldrepenger.mapping;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Periode;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeRequest;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeResponse;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.*;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.foreldrepenger.Adopsjon;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.foreldrepenger.Foedsel;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.foreldrepenger.Foreldrepengeperiode;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.foreldrepenger.Foreldrepengerettighet;
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
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Mapperklasse
 */
public class ForeldrepengerMapper {
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

    public FimHentForeldrepengerettighetRequest map(ForeldrepengerListeRequest request) {
        if (request == null) {
            return null;
        }
        FimHentForeldrepengerettighetRequest wsRequest = new FimHentForeldrepengerettighetRequest();
        wsRequest.setIdent(request.getIdent());
//      Var ikke del av orika-mapping
//      wsRequest.setForeldrepengerettighet(map(request.getForeldrepengerettighetPeriode()));
        return wsRequest;
    }

    private FimPeriode map(Periode source) {
        if (source == null) {
            return null;
        }
        FimPeriode wsPeriode = new FimPeriode();
        wsPeriode.setFom(map(source.getFrom()));
        wsPeriode.setTom(map(source.getTo()));
        return wsPeriode;
    }

    public ForeldrepengerListeResponse map(FimHentForeldrepengerettighetResponse wsResponse) {
        if (wsResponse == null) {
            return null;
        }
        ForeldrepengerListeResponse response = new ForeldrepengerListeResponse();
        response.setForeldrepengerettighet(map(wsResponse.getForeldrepengerettighet()));
        return response;
    }

    private Foreldrepengerettighet map(FimForeldrepengerettighet source) {
        if (source == null) {
            return null;
        }
        if (source instanceof FimAdopsjon) {
            return map((FimAdopsjon) source);
        } else if (source instanceof FimFoedsel) {
            return map((FimFoedsel) source);
        }
        return null;
    }

    private Adopsjon map(FimAdopsjon source) {
        if (source == null) {
            return null;
        }
        Adopsjon rettighet = mapForeldrepengeRettighet(new Adopsjon(), source);
        rettighet.setOmsorgsovertakelse(map(source.getOmsorgsovertakelse()));
        rettighet.setRettighetFom(map(source.getOmsorgsovertakelse()));
        return rettighet;
    }

    private Foedsel map(FimFoedsel source) {
        if (source == null) {
            return null;
        }
        Foedsel rettighet = mapForeldrepengeRettighet(new Foedsel(), source);
        rettighet.setTermin(map(source.getTermin()));
        rettighet.setRettighetFom(map(source.getTermin()));
        return rettighet;
    }

    private <T extends Foreldrepengerettighet> T mapForeldrepengeRettighet(T to, FimForeldrepengerettighet source) {
        if (to == null || source == null) {
            return null;
        }
        if (source.getAndreForelder() != null) {
            to.setAndreForeldersFnr(source.getAndreForelder().getIdent());
        }
        if (source.getAntallBarn() != null) {
            to.setAntallBarn(source.getAntallBarn().intValue());
        }
        to.setBarnetsFoedselsdato(map(source.getBarnetFoedt()));
        to.setForelder(map(source.getForelder()));
        if (source.getDekningsgrad() != null) {
            to.setDekningsgrad(source.getDekningsgrad().doubleValue());
        }
        to.setFedrekvoteTom(map(source.getFedrekvoteTom()));
        to.setMoedrekvoteTom(map(source.getMoedrekvoteTom()));
        to.setForeldrepengetype(map(source.getForeldrepengetype()));
        if (source.getGraderingsdager() != null) {
            to.setGraderingsdager(source.getGraderingsdager().intValue());
        }
        if (source.getRestDager() != null) {
            to.setRestDager(source.getRestDager().intValue());
        }
        to.setEldsteIdDato(null);
        to.setPeriode(forEach(source.getForeldrepengeperiodeListe(), this::map));
        to.setForeldreAvSammeKjoenn(map(source.getForeldreAvSammeKjoenn()));

        to.setSlutt(map(source.getSlutt()));
        to.setArbeidsforholdListe(forEach(source.getArbeidsforholdListe(), this::map));
        to.setArbeidskategori(map(source.getArbeidskategori()));

        return to;
    }

    private Arbeidsforhold map(FimArbeidsforhold source) {
        if (source == null) {
            return null;
        }
        Arbeidsforhold arbeidsforhold = new Arbeidsforhold();
        arbeidsforhold.setArbeidsgiverNavn(source.getArbeidsgiverNavn());
        arbeidsforhold.setArbeidsgiverKontonr(source.getArbeidsgiverKontonr());
        arbeidsforhold.setInntektsperiode(map(source.getInntektsperiode()));
        if (source.getInntektForPerioden() != null) {
            arbeidsforhold.setInntektForPerioden(source.getInntektForPerioden().doubleValue());
        }
        arbeidsforhold.setSykepengerFom(null);
        arbeidsforhold.setRefusjonTom(map(source.getRefusjonTom()));
        arbeidsforhold.setRefusjonstype(map(source.getRefusjonstype()));
        return arbeidsforhold;
    }

    private Bruker map(FimPerson source) {
        if (source == null) {
            return null;
        }
        Bruker bruker = new Bruker();
        bruker.setIdent(source.getIdent());
        return bruker;
    }

    private Foreldrepengeperiode map(FimForeldrepengeperiode source) {
        if (source == null) {
            return null;
        }
        Foreldrepengeperiode periode = new Foreldrepengeperiode();

        periode.setFodselsnummer(null);
        if (source.getHarAleneomsorgFar() != null) {
            periode.setHarAleneomsorgFar(source.getHarAleneomsorgFar());
        }
        if (source.getHarAleneomsorgMor() != null) {
            periode.setHarAleneomsorgMor(source.getHarAleneomsorgMor());
        }
        if (source.getArbeidsprosentMor() != null) {
            periode.setArbeidsprosentMor(source.getArbeidsprosentMor().doubleValue());
        }
        periode.setAvslagsaarsak(map(source.getAvslagsaarsak()));
        periode.setAvslaatt(map(source.getAvslaatt()));
        if (source.getDisponibelGradering() != null) {
            periode.setDisponibelGradering(source.getDisponibelGradering().doubleValue());
        }
        if (source.getErFedrekvote() != null) {
            periode.setErFedrekvote(source.getErFedrekvote());
        }
        periode.setForskyvelsesaarsak1(map(source.getForskyvelsesaarsak1()));
        periode.setForskyvelsesaarsak2(map(source.getForskyvelsesaarsak2()));
        periode.setForskyvelsesperiode(map(source.getForskyvet1()));
        periode.setForskyvelsesperiode2(map(source.getForskyvet2()));
        periode.setForeldrepengerFom(map(source.getForeldrepengerFom()));
//      Var ikke del av orika-mapping
//      periode.setMidlertidigStansDato(map(source.getMidlertidigStanset()));
        if (source.getErMoedrekvote() != null) {
            periode.setErModrekvote(source.getErMoedrekvote());
        }
        periode.setMorSituasjon(map(source.getMorSituasjon()));
        periode.setRettTilFedrekvote(map(source.getRettTilFedrekvote()));
        periode.setRettTilModrekvote(map(source.getRettTilMoedrekvote()));
        periode.setStansaarsak(map(source.getStansaarsak()));

        List<HistoriskUtbetaling> historiskeUtbetalinger = new ArrayList<>();
        List<KommendeUtbetaling> kommendeUtbetalinger = new ArrayList<>();
        for (FimVedtak utbetaling : source.getVedtakListe()) {
            // Her er det noe rart som vi "kopierer" fra orika-mapperen.
            // Hvorfor historisk blir dyttet inn i kommende er ukjent
            if (utbetaling instanceof  FimHistoriskVedtak) {
                kommendeUtbetalinger.add(map((FimHistoriskVedtak)utbetaling));
            }
        }

        periode.setHistoriskeUtbetalinger(historiskeUtbetalinger);
        periode.setKommendeUtbetalinger(kommendeUtbetalinger);

        return periode;
    }

    private Periode map(FimPeriode source) {
        if (source == null) {
            return null;
        }
        Periode periode = new Periode();
        periode.setFrom(map(source.getFom()));
        periode.setTo(map(source.getTom()));
        return periode;
    }

    private Kodeverkstype map(FimKodeverdi source) {
        if (source == null) {
            return null;
        }

        Kodeverkstype to = new Kodeverkstype();
        to.setKode(source.getKode());
        to.setTermnavn(source.getTermnavn());
        return to;
    }

    private KommendeUtbetaling map(FimHistoriskVedtak source) {
        if (source == null) {
            return null;
        }
        KommendeUtbetaling utbetaling = new KommendeUtbetaling();
        utbetaling.setUtbetalingsdato(map(source.getUtbetalt()));
        if (source.getBruttobeloep() != null) {
            utbetaling.setBruttobeloep(source.getBruttobeloep().doubleValue());
        }
        utbetaling.setArbeidsgiverNavn(source.getArbeidsgiverNavn());
        utbetaling.setArbeidsgiverKontonr(source.getArbeidsgiverKontonr());
        utbetaling.setArbeidsgiverOrgnr(source.getArbeidsgiverOrgnr());
        if (source.getDagsats() != null) {
            utbetaling.setDagsats(source.getDagsats().doubleValue());
        }
        utbetaling.setSaksbehandler(source.getSaksbehandler());
        if (source.getPeriodetype() != null) {
            Kodeverkstype type = new Kodeverkstype(source.getPeriodetype().getKode(), source.getPeriodetype().getTermnavn());
            utbetaling.setType(type);
        }

        utbetaling.setVedtak(map(source.getVedtak()));
        if (source.getUtbetalingsgrad() != null) {
            utbetaling.setUtbetalingsgrad(source.getUtbetalingsgrad().doubleValue());
        }

        return utbetaling;
    }

    private XMLGregorianCalendar map(LocalDate source) {
        if (source == null) {
            return null;
        }
        try {
            return DatatypeFactory
                    .newInstance()
                    .newXMLGregorianCalendarDate(source.getYear(), source.getMonthOfYear(), source.getDayOfMonth(), 0);
        } catch (DatatypeConfigurationException e) {
            logger.warn("DatatypeConfigurationException", e.getMessage());
            throw new RuntimeException("Could not map to XMLGregorianCalendar", e);
        }
    }

    public LocalDate map(XMLGregorianCalendar source) {
        if (source == null) {
            return null;
        }
        return new LocalDate(source.getYear(), source.getMonth(), source.getDay());
    }


    private <S, T> List<T> forEach(List<S> list, Function<S, T> fn) {
        if (list == null) {
            return null;
        }
        return list.stream().map(fn).collect(Collectors.toList());
    }
}
