package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo;

import no.nav.kontrakter.consumer.fim.mapping.YtelseskontraktMapper;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.YtelseskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktRequest;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktResponse;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.FimBruker;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.FimDagpengekontrakt;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.FimPeriode;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.FimRettighetsgruppe;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.FimVedtak;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.FimYtelseskontrakt;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.meldinger.FimHentYtelseskontraktListeResponse;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.kjerneinfo.common.mockutils.DateUtils.convertDateToXmlGregorianCalendar;
import static no.nav.kjerneinfo.common.mockutils.DateUtils.getDate;
import static no.nav.kjerneinfo.common.mockutils.DateUtils.getRandomDate;
import static no.nav.kjerneinfo.common.mockutils.DateUtils.getRandomDatePair;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class YtelseskontraktServiceBiMock {

    private static final int BORTFALL_PROSENT_DAGER_IGJEN = 60;
    private static final int DAGER_IGJEN_PERMITTERING = 50;
    public static final String VEDTAK_AKTIVITETSFASE = "Aktivitetsfase";

    public static YtelseskontraktServiceBi getYtelseskontraktServiceBiMock() {
        YtelseskontraktServiceBi mock = mock(YtelseskontraktServiceBi.class);
        when(mock.hentYtelseskontrakter(any(YtelseskontraktRequest.class))).thenReturn(lagYtelsesMockRespons());
        return mock;
    }

    private static YtelseskontraktResponse lagYtelsesMockRespons() {
        FimHentYtelseskontraktListeResponse respons = new FimHentYtelseskontraktListeResponse()
                .withBruker(new FimBruker().withRettighetsgruppe(new FimRettighetsgruppe().withRettighetsGruppe("test")))
                .withYtelseskontraktListe(createYtelsesKontrakter());
        return new YtelseskontraktMapper().map(respons, YtelseskontraktResponse.class);
    }

    private static List<FimYtelseskontrakt> createYtelsesKontrakter() {
        FimVedtak vedtak7 = new FimVedtak()
                .withPeriodetypeForYtelse("Lang periode")
                .withUttaksgrad(100)
                .withVedtakBruttoBeloep(10000)
                .withVedtakNettoBeloep(5000);
        FimVedtak vedtak = populateVedtak(vedtak7, "Innvilget", "Annuler sanksjon", getDate(2012, 12, 12), createPeriode(getDate(2012, 1, 1), getDate(2012, 3, 1)), null);

        FimVedtak vedtak6 = new FimVedtak()
                .withPeriodetypeForYtelse("Kort periode")
                .withUttaksgrad(75)
                .withVedtakBruttoBeloep(2000)
                .withVedtakNettoBeloep(1000);
        FimVedtak vedtak2 = populateVedtak(vedtak6, "Avslått", "Forlenget ventetid", getDate(2012, 12, 12), createPeriode(getDate(2012, 2, 1), getDate(2012, 2, 15)), VEDTAK_AKTIVITETSFASE);

        FimVedtak vedtak5 = new FimVedtak()
                .withPeriodetypeForYtelse("Kort periode")
                .withUttaksgrad(75)
                .withVedtakBruttoBeloep(2000)
                .withVedtakNettoBeloep(1000);
        FimVedtak vedtak3 = populateVedtak(vedtak5, "Avslått", "Tidsbegrenset bortfall", getDate(2012, 12, 12), createPeriode(getDate(2012, 2, 15), getDate(2012, 2, 25)), VEDTAK_AKTIVITETSFASE);

        FimVedtak vedtak1 = new FimVedtak()
                .withPeriodetypeForYtelse("Laaang periode")
                .withUttaksgrad(25)
                .withVedtakBruttoBeloep(3000)
                .withVedtakNettoBeloep(1500);
        FimVedtak vedtak4 = populateVedtak(vedtak1, "Avbrutt", "Stans", getDate(2012, 12, 12), createPeriode(getDate(2012, 2, 20), getDate(2012, 2, 22)), VEDTAK_AKTIVITETSFASE);

        return asList(
                createDagpengekontrakt(getDate(2012, 12, 10), getDate(2012, 12, 14), getDate(2013, 2, 10), "Aktiv", "Dagpenger", 34, vedtak, vedtak2, vedtak3, vedtak4),
                createYtelsesKontrakt(getDate(2012, 12, 14), getDate(2012, 12, 19), getDate(2013, 1, 10), "Lukket", "Arbeidsavklaringspenger", "Avslått", "Endring", BORTFALL_PROSENT_DAGER_IGJEN),
                createYtelsesKontrakt(getDate(2012, 12, 18), getDate(2012, 12, 31), getDate(2013, 5, 11), "Inaktiv", "Individstønad", "Avbrutt", "Kontroll", -14));
    }

    private static FimVedtak populateVedtak(FimVedtak vedtak, String status, String type, Date beslutningsDato, FimPeriode periode, String aktivitetsfase) {
        return vedtak
                .withStatus(status)
                .withVedtakstype(type)
                .withBeslutningsdato(convertDateToXmlGregorianCalendar(beslutningsDato))
                .withAktivitetsfase(aktivitetsfase)
                .withVedtaksperiode(periode);
    }

    private static FimPeriode createPeriode(Date fom, Date tom) {
        return new FimPeriode()
                .withFom(convertDateToXmlGregorianCalendar(fom))
                .withTom(convertDateToXmlGregorianCalendar(tom));
    }

    @SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.sizes.ParameterNumberCheck")
    private static FimYtelseskontrakt createYtelsesKontrakt(Date datoKravMottat, Date fom, Date tom, String status, String ytelsestype, String vedtaksstatus, String vedtakstype, Integer bortfallProsentDagerIgjen) {
        return createYtelsesKontrakt(datoKravMottat, fom, tom, status, ytelsestype, bortfallProsentDagerIgjen)
                .withIhtVedtak(createVedtakWithRandomDates(vedtaksstatus, vedtakstype));
    }

    private static FimYtelseskontrakt createYtelsesKontrakt(Date datoKravMottat, Date fom, Date tom, String status, String ytelsestype, Integer bortfallProsentDagerIgjen) {
        return populateYtelsesKontrakt(new FimYtelseskontrakt(), datoKravMottat, fom, tom, status, ytelsestype, bortfallProsentDagerIgjen);
    }

    private static FimVedtak createVedtakWithRandomDates(String status, String type) {
        Date[] datePair = getRandomDatePair();
        return populateVedtak(new FimVedtak(), status, type, getRandomDate(), createPeriode(datePair[0], datePair[1]), VEDTAK_AKTIVITETSFASE);
    }

    private static FimDagpengekontrakt createDagpengekontrakt(Date datoKravMottat, Date fom, Date tom, String status, String ytelsestype, int antallDagerIgjen, FimVedtak... vedtak) {
        FimDagpengekontrakt kontrakt = new FimDagpengekontrakt();
        populateYtelsesKontrakt(kontrakt, datoKravMottat, fom, tom, status, ytelsestype, BORTFALL_PROSENT_DAGER_IGJEN);
        return kontrakt
                .withAntallDagerIgjen(antallDagerIgjen)
                .withAntallUkerIgjen(antallDagerIgjen / 5)
                .withAntallDagerIgjenUnderPermittering(DAGER_IGJEN_PERMITTERING)
                .withAntallUkerIgjenUnderPermittering(DAGER_IGJEN_PERMITTERING / 5)
                .withIhtVedtak(vedtak);
    }

    private static FimYtelseskontrakt populateYtelsesKontrakt(FimYtelseskontrakt kontrakt, Date datoKravMottat, Date fom, Date tom, String status, String ytelsestype, Integer bortfallProsentDagerIgjen) {
        return kontrakt
                .withDatoKravMottatt(convertDateToXmlGregorianCalendar(datoKravMottat))
                .withFomGyldighetsperiode(convertDateToXmlGregorianCalendar(fom))
                .withTomGyldighetsperiode(convertDateToXmlGregorianCalendar(tom))
                .withStatus(status)
                .withYtelsestype(ytelsestype)
                .withBortfallsprosentDagerIgjen(bortfallProsentDagerIgjen)
                .withBortfallsprosentUkerIgjen(bortfallProsentDagerIgjen / 5);
    }

}
