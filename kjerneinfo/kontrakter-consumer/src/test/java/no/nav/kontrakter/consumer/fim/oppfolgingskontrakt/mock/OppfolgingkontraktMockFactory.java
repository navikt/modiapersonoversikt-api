package no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.mock;

import no.nav.kjerneinfo.common.utils.DateUtils;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.mock.YtelseskontraktMockFactory;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Genererer Oppfoelgingskontrakt-objekter for testformål.
 */
public final class OppfolgingkontraktMockFactory {

    public static final String FAGSAKSTATUS_AKTIV = "Aktiv";
    public static final String FAGSAKSTATUS_INAKTIV = "Inaktiv";
    private static final String BRUKER_FORMIDLINGS_GRUPPE = "50000";
    public static final Date FOM = DateUtils.getDate(2020, 10, 13);
    public static final Date TOM = DateUtils.getDate(2020, 10, 13);
    private static final Date DATO_KRAV_MOTTATT = DateUtils.getDate(2020, 10, 13);
    private static final String YTELSESKONTRAKT_STATUS = "YtelseskontraktStatus";
    private static final String YTELSESTYPE = "Ytelsestype";
    private static final Date SYKMELDT_FRA = DateUtils.getDate(2020, 10, 13);
    private static final String SYFO_PUNKT_STATUS = "SYFOPunktStatus";
    private static final String SYFO_HENDELSE = "SYFOHendelse";
    private static final String SERVICE_GRUPPE = "Servicegruppe";
    private static final boolean MELDEPLIKT = true;

    private OppfolgingkontraktMockFactory() {

    }

    public static List<? extends WSOppfoelgingskontrakt> createOppfoelgingskontrakter(String fnr, Date fra, Date til) {

        if ("22222222222".equals(fnr)) {
            return filterByDates(createOppfoelgingskontrakterDonald(), fra, til);
        }

        return Arrays.asList(createOppfoelgingskontrakt());
    }

    private static List<WSSYFOkontrakt> filterByDates(List<WSSYFOkontrakt> oppfoelgingskontrakterDonald, Date fra, Date til) {
        for (WSSYFOkontrakt fimOppfoelgingskontrakt : oppfoelgingskontrakterDonald) {
            filterYtelseskontrakt(fra, til, fimOppfoelgingskontrakt);
            filterSYFO(fra, til, fimOppfoelgingskontrakt);
        }

        return oppfoelgingskontrakterDonald;
    }

    private static void filterSYFO(Date fra, Date til, WSSYFOkontrakt fimSYFOkontrakt) {
        List<WSSYFOPunkt> innPunkter = new ArrayList<>();
        for (WSSYFOPunkt punkt : fimSYFOkontrakt.getHarSYFOPunkt()) {
            if (DateUtils.datoInside(punkt.getDato(), fra, til)) {
                innPunkter.add(punkt);
            }
        }

        fimSYFOkontrakt.getHarSYFOPunkt().clear();
        fimSYFOkontrakt.getHarSYFOPunkt().addAll(innPunkter);
    }

    private static void filterYtelseskontrakt(Date fra, Date til, WSOppfoelgingskontrakt fimOppfoelgingskontrakt) {
        List<WSYtelseskontrakt> innYtelseskontrakt = new ArrayList<>();
        for (WSYtelseskontrakt ytelseskontrakt : fimOppfoelgingskontrakt.getAvYtelse()) {
            if (DateUtils.datoInside(ytelseskontrakt.getDatoKravMottatt(), fra, til)) {
                innYtelseskontrakt.add(ytelseskontrakt);
            }
        }
        fimOppfoelgingskontrakt.getAvYtelse().clear();
        fimOppfoelgingskontrakt.getAvYtelse().addAll(innYtelseskontrakt);
    }

    private static List<WSSYFOkontrakt> createOppfoelgingskontrakterDonald() {
        WSSYFOkontrakt kontrakt1 = new WSSYFOkontrakt();
        kontrakt1.setGjelderBruker(createBruker());
        kontrakt1.setStatus(FAGSAKSTATUS_INAKTIV);

        kontrakt1.setIhtGjeldendeVedtak(
                createVedtak(YtelseskontraktMockFactory.YTELSESSTATUS_INAKTIV, DateUtils.getDate(2012, 2, 11),
                        DateUtils.getDate(2012, 2, 14), DateUtils.getDate(2012, 2, 14))
        );

        kontrakt1.getAvYtelse().addAll(Arrays.asList(
                createYtelseskontrakt(YtelseskontraktMockFactory.YTELSESSTATUS_AKTIV, YtelseskontraktMockFactory.VEDTAKSTYPE_STANS, DateUtils.getDate(2012, 2, 10)),
                createYtelseskontrakt(YtelseskontraktMockFactory.YTELSESSTATUS_LUKKET, YtelseskontraktMockFactory.VEDTAKSTYPE_ENDRING, DateUtils.getDate(2012, 2, 5))
        ));

        kontrakt1.getHarSYFOPunkt().addAll(Arrays.asList(createSYFOPunkt(DateUtils.getDate(2012, 3, 15), true, "Godkjent", "8-ukers sykmelding: Vurdere aktivitetskrav"),
                createSYFOPunkt(DateUtils.getDate(2012, 5, 10), false, "Mottat", "Rapport fra arbeidsgiver"),
                createSYFOPunkt(DateUtils.getDate(2012, 5, 25), true, "Godkjent", "17-uker sykmelding: Kandidat til Dialogmøte 2"),
                createSYFOPunkt(DateUtils.getDate(2012, 8, 1), true, "Registrert", "26-ukers sykmelding: Dialogmøte 2")
        ));

        return Arrays.asList(kontrakt1);
    }

    public static WSOppfoelgingskontrakt createOppfoelgingskontrakt() {
        WSOppfoelgingskontrakt kontrakt = new WSOppfoelgingskontrakt();
        kontrakt.setGjelderBruker(createBruker());
        kontrakt.setStatus(FAGSAKSTATUS_AKTIV);

        kontrakt.setIhtGjeldendeVedtak(createVedtak(null, null, null, null));
        kontrakt.getAvYtelse().add(createYtelseskontrakt(null, null, DateUtils.getRandomDate()));

        return kontrakt;
    }

    public static WSSYFOkontrakt createSYFOkontrakt(Date sykmeldtFra, String status, WSBruker bruker, WSSYFOPunkt... syfopunkter) {
        WSSYFOkontrakt kontrakt = new WSSYFOkontrakt();
        if (sykmeldtFra == null) {
            kontrakt.setSykmeldtFra(DateUtils.convertDateToXmlGregorianCalendar(SYKMELDT_FRA));
        } else {
            kontrakt.setSykmeldtFra(DateUtils.convertDateToXmlGregorianCalendar(sykmeldtFra));
        }

        if (syfopunkter == null) {
            kontrakt.getHarSYFOPunkt().add(createSYFOPunkt(DateUtils.getRandomDate(), true));
            kontrakt.getHarSYFOPunkt().add(createSYFOPunkt(DateUtils.getRandomDate(), false));
        } else {
            kontrakt.getHarSYFOPunkt().addAll(Arrays.asList(syfopunkter));
        }

        kontrakt.setStatus(status);

        if (bruker == null) {
            kontrakt.setGjelderBruker(createBruker());
        } else {
            kontrakt.setGjelderBruker(bruker);
        }
        return kontrakt;
    }

    private static WSSYFOPunkt createSYFOPunkt(Date dato, boolean isFast) {
        WSSYFOPunkt punkt = new WSSYFOPunkt();
        punkt.setDato(DateUtils.convertDateToXmlGregorianCalendar(dato));
        punkt.setFastOppfoelgingspunkt(isFast);
        punkt.setStatus(SYFO_PUNKT_STATUS);
        punkt.setSYFOHendelse(SYFO_HENDELSE);
        return punkt;
    }

    private static WSSYFOPunkt createSYFOPunkt(Date dato, boolean isFast, String status, String hendelse) {
        WSSYFOPunkt punkt = new WSSYFOPunkt();
        punkt.setDato(DateUtils.convertDateToXmlGregorianCalendar(dato));
        punkt.setFastOppfoelgingspunkt(isFast);
        punkt.setStatus(status);
        punkt.setSYFOHendelse(hendelse);
        return punkt;
    }

    private static WSVedtak createVedtak(String status, Date fra, Date til, Date datoKravMottatt) {
        WSVedtak vedtak = new WSVedtak();
        if (status == null) {
            vedtak.setStatus(YtelseskontraktMockFactory.YTELSESSTATUS_AKTIV);
        } else {
            vedtak.setStatus(status);
        }

        if (fra == null) {
            vedtak.setVedtaksperiode(createPeriode());
        } else {
            vedtak.setVedtaksperiode(createPeriode(fra, til));
        }

        if (datoKravMottatt == null) {
            vedtak.setOmYtelse(createYtelseskontrakt(null, null, DATO_KRAV_MOTTATT));
        } else {
            vedtak.setOmYtelse(createYtelseskontrakt(null, null, datoKravMottatt));
        }
        return vedtak;
    }

    private static WSYtelseskontrakt createYtelseskontrakt(String status, String type, Date datoKravMottat) {
        WSYtelseskontrakt kontrakt = new WSYtelseskontrakt();

        if (datoKravMottat == null) {
            kontrakt.setDatoKravMottatt(DateUtils.convertDateToXmlGregorianCalendar(DATO_KRAV_MOTTATT));
        } else {
            kontrakt.setDatoKravMottatt(DateUtils.convertDateToXmlGregorianCalendar(datoKravMottat));
        }

        if (status == null) {
            kontrakt.setStatus(YTELSESKONTRAKT_STATUS);
        } else {
            kontrakt.setStatus(status);
        }

        if (type == null) {
            kontrakt.setYtelsestype(YTELSESTYPE);
        } else {
            kontrakt.setYtelsestype(type);
        }
        return kontrakt;
    }

    private static WSPeriode createPeriode() {
        return createPeriode(FOM, TOM);
    }

    private static WSPeriode createPeriode(Date fom, Date tom) {
        WSPeriode periode = new WSPeriode();
        periode.setFom(DateUtils.convertDateToXmlGregorianCalendar(fom));
        periode.setTom(DateUtils.convertDateToXmlGregorianCalendar(tom));
        return periode;
    }

    private static WSBruker createBruker() {
        WSBruker bruker = new WSBruker();
        bruker.setFormidlingsgruppe(BRUKER_FORMIDLINGS_GRUPPE);
        bruker.getServicegruppe().add(createServicegruppe());
        bruker.getMeldeplikt().add(createMeldeplikt());
        return bruker;
    }

    private static WSMeldeplikt createMeldeplikt() {
        WSMeldeplikt meldeplikt = new WSMeldeplikt();
        meldeplikt.setMeldeplikt(MELDEPLIKT);
        return meldeplikt;
    }

    private static WSServiceGruppe createServicegruppe() {
        WSServiceGruppe serviceGruppe = new WSServiceGruppe();
        serviceGruppe.setServiceGruppe(SERVICE_GRUPPE);
        return serviceGruppe;
    }

}
