package no.nav.modiapersonoversikt.consumer.arena.ytelseskontrakt;

import no.nav.modiapersonoversikt.arena.ytelseskontrakt.Dagpengeytelse;
import no.nav.modiapersonoversikt.arena.ytelseskontrakt.Vedtak;
import no.nav.modiapersonoversikt.arena.ytelseskontrakt.Ytelse;
import no.nav.modiapersonoversikt.arena.ytelseskontrakt.YtelseskontraktResponse;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.*;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.meldinger.FimHentYtelseskontraktListeRequest;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.meldinger.FimHentYtelseskontraktListeResponse;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class YtelseskontraktMapper {
    private static final Logger logger = LoggerFactory.getLogger(YtelseskontraktMapper.class);
    private static YtelseskontraktMapper instance = null;

    private YtelseskontraktMapper() {
    }

    public static YtelseskontraktMapper getInstance() {
        if (instance == null) {
            instance = new YtelseskontraktMapper();
        }

        return instance;
    }

    public FimHentYtelseskontraktListeRequest map(YtelseskontraktRequest request) {
        FimHentYtelseskontraktListeRequest wsRequest = new FimHentYtelseskontraktListeRequest();
        wsRequest.setPersonidentifikator(request.getFodselsnummer());
        FimPeriode wsPeriode = new FimPeriode();
        wsPeriode.setFom(map(request.getFrom()));
        wsPeriode.setTom(map(request.getTo()));
        wsRequest.setPeriode(wsPeriode);
        return wsRequest;
    }

    public YtelseskontraktResponse map(FimHentYtelseskontraktListeResponse wsResponse) {
        YtelseskontraktResponse response = new YtelseskontraktResponse();
        response.setRettighetsgruppe(hentRettighetsgruppe(wsResponse));
        response.setYtelser(forEach(wsResponse.getYtelseskontraktListe(), this::mapYtelse));
        return response;
    }

    public Ytelse mapYtelse(FimYtelseskontrakt source) {
        Ytelse ytelse;
        logger.info("Starter mapping av ytelse av type: " + source.getYtelsestype());
        System.out.println("Starter mapping av ytelse av type: " + source.getYtelsestype());
        if (source instanceof FimDagpengekontrakt) {
            System.out.println("Treff på dagpengekontrakt");
            logger.info("Treff på dagpengekontrakt");
            ytelse = mapDagpengekontrakt((FimDagpengekontrakt)source);
        } else {
            System.out.println("Ikke treff på dagpengekontrakt, blir vanlig ytelse!");
            logger.info("Ikke treff på dagpengekontrakt, blir vanlig ytelse!");
            ytelse = new Ytelse();
        }
        ytelse.setType(source.getYtelsestype());
        ytelse.setStatus(source.getStatus());
        ytelse.setDatoKravMottatt(map(source.getDatoKravMottatt()));
        ytelse.setDagerIgjenMedBortfall(source.getBortfallsprosentDagerIgjen());
        ytelse.setUkerIgjenMedBortfall(source.getBortfallsprosentUkerIgjen());

        ytelse.setFom(map(source.getFomGyldighetsperiode()));
        ytelse.setTom(map(source.getTomGyldighetsperiode()));
        ArrayList<Vedtak> vedtak = new ArrayList<>();
        for (FimVedtak fimVedtak : source.getIhtVedtak()) {
            vedtak.add(map(fimVedtak));
        }
        ytelse.setVedtak(vedtak);
        logger.info("Ytelsestypen som returneres er: " + ytelse.getClass());
        return ytelse;
    }

    private Dagpengeytelse mapDagpengekontrakt(FimDagpengekontrakt source) {
        Dagpengeytelse dagpengeytelse = new Dagpengeytelse();
        dagpengeytelse.setAntallDagerIgjen(source.getAntallDagerIgjen());
        dagpengeytelse.setAntallUkerIgjen(source.getAntallUkerIgjen());
        dagpengeytelse.setAntallDagerIgjenPermittering(source.getAntallDagerIgjenUnderPermittering());
        dagpengeytelse.setAntallUkerIgjenPermittering(source.getAntallUkerIgjenUnderPermittering());

        return dagpengeytelse;
    }

    private Vedtak map(FimVedtak source) {
        Vedtak vedtak = new Vedtak();
        vedtak.setVedtakstatus(source.getStatus());
        vedtak.setVedtaksdato(map(source.getBeslutningsdato()));
        vedtak.setVedtakstype(source.getVedtakstype());
        vedtak.setAktivitetsfase(source.getAktivitetsfase());
        vedtak.setActiveFrom(Optional.ofNullable(source.getVedtaksperiode()).map(FimPeriode::getFom).map(this::map).orElse(null));
        vedtak.setActiveTo(Optional.ofNullable(source.getVedtaksperiode()).map(FimPeriode::getTom).map(this::map).orElse(null));
        return vedtak;
    }

    private String hentRettighetsgruppe(FimHentYtelseskontraktListeResponse wsResponse) {
        return Optional.ofNullable(wsResponse)
                .map(FimHentYtelseskontraktListeResponse::getBruker)
                .map(FimBruker::getRettighetsgruppe)
                .map(FimRettighetsgruppe::getRettighetsGruppe)
                .orElse(null);
    }

    private LocalDate map(XMLGregorianCalendar source) {
        if (source == null) {
            return null;
        }
        return new LocalDate(source.getYear(), source.getMonth(), source.getDay());
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
            throw new RuntimeException("Could not map to XMLGregorianCalendar", e);
        }
    }

    private static <S, T> List<T> forEach(List<S> list, Function<S, T> fn) {
        return list.stream().map(fn).collect(Collectors.toList());
    }
}
