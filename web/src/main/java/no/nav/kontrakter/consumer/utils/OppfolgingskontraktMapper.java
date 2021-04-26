package no.nav.kontrakter.consumer.utils;

import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktRequest;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktResponse;
import no.nav.kontrakter.domain.oppfolging.Bruker;
import no.nav.kontrakter.domain.oppfolging.SYFOPunkt;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.WSHentOppfoelgingskontraktListeRequest;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.WSHentOppfoelgingskontraktListeResponse;
import org.joda.time.LocalDate;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapperklasse som benytter OppfolgingkontraktFIMMapper for custom mapping.
 */
public class OppfolgingskontraktMapper {
    private static OppfolgingskontraktMapper instance = null;

    private OppfolgingskontraktMapper() {}

    public static OppfolgingskontraktMapper getInstance() {
        if (instance == null) {
            instance = new OppfolgingskontraktMapper();
        }

        return instance;
    }

    public WSHentOppfoelgingskontraktListeRequest map(OppfolgingskontraktRequest request) {
        WSHentOppfoelgingskontraktListeRequest wsRequest = new WSHentOppfoelgingskontraktListeRequest();
        wsRequest.setPersonidentifikator(request.getFodselsnummer());
        WSPeriode wsPeriode = new WSPeriode();
        wsPeriode.setFom(map(request.getFrom()));
        wsPeriode.setTom(map(request.getTo()));
        wsRequest.setPeriode(wsPeriode);
        return wsRequest;
    }

    public OppfolgingskontraktResponse map(WSHentOppfoelgingskontraktListeResponse from) {
        OppfolgingskontraktResponse response = new OppfolgingskontraktResponse();
        if (from == null || from.getOppfoelgingskontraktListe() == null) {
            return response;
        }

        List<WSOppfoelgingskontrakt> oppfolgingskontraktListe = from.getOppfoelgingskontraktListe();
        if (!oppfolgingskontraktListe.isEmpty()) {
            WSOppfoelgingskontrakt wsKontrakt = oppfolgingskontraktListe.get(0);
            response.setBruker(map(wsKontrakt.getGjelderBruker()));
            response.setVedtaksdato(map(wsKontrakt.getIhtGjeldendeVedtak()));
        }

        oppfolgingskontraktListe
                .stream()
                .filter((wsKontrakt) -> wsKontrakt instanceof WSSYFOkontrakt)
                .map((wsKontrakt) -> (WSSYFOkontrakt)wsKontrakt)
                .forEach((WSSYFOkontrakt syfoKontrakt) -> {
                    List<SYFOPunkt> syfoPunkter = new ArrayList<>();
                    for (WSSYFOPunkt syfoPunkt : syfoKontrakt.getHarSYFOPunkt()) {
                        syfoPunkter.add(map(syfoPunkt));
                    }
                    response.setSyfoPunkter(syfoPunkter);

                    Bruker bruker = map(syfoKontrakt.getGjelderBruker());
                    bruker.setSykmeldtFrom(map(syfoKontrakt.getSykmeldtFra()));
                    response.setBruker(bruker);
                });

        return response;
    }

    private LocalDate map(WSVedtak source) {
        if (source == null || source.getVedtaksperiode() == null) {
            return null;
        }
        return map(source.getVedtaksperiode().getFom());
    }

    private SYFOPunkt map(WSSYFOPunkt source) {
        if (source == null) {
            return null;
        }
        SYFOPunkt syfoPunkt = new SYFOPunkt();
        syfoPunkt.setDato(map(source.getDato()));
        syfoPunkt.setFastOppfolgingspunkt(source.isFastOppfoelgingspunkt());
        syfoPunkt.setStatus(source.getStatus());
        syfoPunkt.setSyfoHendelse(source.getSYFOHendelse());
        return syfoPunkt;
    }

    private LocalDate map(XMLGregorianCalendar source) {
        if (source == null) {
            return null;
        }
        return new LocalDate(source.getYear(), source.getMonth(), source.getDay());
    }

    private Bruker map(WSBruker source) {
        if (source == null) {
            return null;
        }
        Bruker bruker = new Bruker();
        bruker.setFormidlingsgruppe(source.getFormidlingsgruppe());
        bruker.setInnsatsgruppe(hentInnsatsgruppe(source.getServicegruppe()));
        bruker.setMeldeplikt(hentMeldeplikt(source.getMeldeplikt()));
        return bruker;
    }

    private Boolean hentMeldeplikt(List<WSMeldeplikt> source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        return source.get(0).isMeldeplikt();
    }

    private String hentInnsatsgruppe(List<WSServiceGruppe> source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        return source.get(0).getServiceGruppe();
    }

    private static XMLGregorianCalendar map(LocalDate source) {
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
}
