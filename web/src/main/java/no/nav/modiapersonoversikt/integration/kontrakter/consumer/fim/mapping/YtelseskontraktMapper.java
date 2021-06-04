package no.nav.modiapersonoversikt.integration.kontrakter.consumer.fim.mapping;

import no.nav.modiapersonoversikt.integration.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktRequest;
import no.nav.modiapersonoversikt.integration.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktResponse;
import no.nav.modiapersonoversikt.integration.kontrakter.domain.ytelse.Dagpengeytelse;
import no.nav.modiapersonoversikt.integration.kontrakter.domain.ytelse.Vedtak;
import no.nav.modiapersonoversikt.integration.kontrakter.domain.ytelse.Ytelse;
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
        if (source instanceof FimDagpengekontrakt) {
            ytelse = mapDagpengekontrakt((FimDagpengekontrakt)source);
        } else {
            ytelse = new Ytelse();
        }
        ytelse.setType(source.getYtelsestype());
        ytelse.setStatus(source.getStatus());
        ytelse.setDatoKravMottat(map(source.getDatoKravMottatt()));
        ytelse.setDagerIgjenMedBortfall(source.getBortfallsprosentDagerIgjen());
        ytelse.setUkerIgjenMedBortfall(source.getBortfallsprosentUkerIgjen());

        ytelse.setFom(map(source.getFomGyldighetsperiode()));
        ytelse.setTom(map(source.getTomGyldighetsperiode()));
        ArrayList<Vedtak> vedtak = new ArrayList<>();
        for (FimVedtak fimVedtak : source.getIhtVedtak()) {
            vedtak.add(map(fimVedtak));
        }
        ytelse.setVedtak(vedtak);
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
