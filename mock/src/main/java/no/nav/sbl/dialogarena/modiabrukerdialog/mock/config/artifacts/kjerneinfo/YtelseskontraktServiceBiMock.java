package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo;

import no.nav.kontrakter.consumer.fim.mapping.YtelseskontraktMapper;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.YtelseskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktRequest;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktResponse;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v1.informasjon.FimBruker;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v1.informasjon.FimDagpengekontrakt;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v1.informasjon.FimPeriode;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v1.informasjon.FimRettighetsgruppe;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v1.informasjon.FimVedtak;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v1.informasjon.FimYtelseskontrakt;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v1.meldinger.FimHentYtelseskontraktListeResponse;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import static java.lang.Math.round;
import static java.math.BigInteger.valueOf;
import static java.util.Arrays.asList;
import static no.nav.kjerneinfo.common.mockutils.DateUtils.convertDateToXmlGregorianCalendar;
import static no.nav.kjerneinfo.common.mockutils.DateUtils.getDate;
import static no.nav.kjerneinfo.common.mockutils.DateUtils.getRandomDate;
import static no.nav.kjerneinfo.common.mockutils.DateUtils.getRandomDatePair;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class YtelseskontraktServiceBiMock {

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
                .withUttaksgrad(new BigInteger("100"))
                .withVedtakBruttoBeloep(new BigInteger("10000"))
                .withVedtakNettoBeloep(new BigInteger("5000"));
        FimVedtak vedtak = populateVedtak(vedtak7, "Innvilget", "Annuler sanksjon", getDate(2012, 12, 12), createPeriode(getDate(2012, 1, 1), getDate(2012, 3, 1)));

        FimVedtak vedtak6 = new FimVedtak()
                .withPeriodetypeForYtelse("Kort periode")
                .withUttaksgrad(new BigInteger("75"))
                .withVedtakBruttoBeloep(new BigInteger("2000"))
                .withVedtakNettoBeloep(new BigInteger("1000"));
        FimVedtak vedtak2 = populateVedtak(vedtak6, "Avslått", "Forlenget ventetid", getDate(2012, 12, 12), createPeriode(getDate(2012, 2, 1), getDate(2012, 2, 15)));

        FimVedtak vedtak5 = new FimVedtak()
                .withPeriodetypeForYtelse("Kort periode")
                .withUttaksgrad(new BigInteger("75"))
                .withVedtakBruttoBeloep(new BigInteger("2000"))
                .withVedtakNettoBeloep(new BigInteger("1000"));
        FimVedtak vedtak3 = populateVedtak(vedtak5, "Avslått", "Tidsbegrenset bortfall", getDate(2012, 12, 12), createPeriode(getDate(2012, 2, 15), getDate(2012, 2, 25)));

        FimVedtak vedtak1 = new FimVedtak()
                .withPeriodetypeForYtelse("Laaang periode")
                .withUttaksgrad(new BigInteger("25"))
                .withVedtakBruttoBeloep(new BigInteger("3000"))
                .withVedtakNettoBeloep(new BigInteger("1500"));
        FimVedtak vedtak4 = populateVedtak(vedtak1, "Avbrutt", "Stans", getDate(2012, 12, 12), createPeriode(getDate(2012, 2, 20), getDate(2012, 2, 22)));

        return asList(
                createDagpengekontrakt(getDate(2012, 12, 10), getDate(2012, 12, 14), getDate(2013, 2, 10), "Aktiv", "Dagpenger", new BigInteger("34"), vedtak, vedtak2, vedtak3, vedtak4),
                createYtelsesKontrakt(getDate(2012, 12, 14), getDate(2012, 12, 19), getDate(2013, 1, 10), "Lukket", "Arbeidsavklaringspenger", "Avslått", "Endring"),
                createYtelsesKontrakt(getDate(2012, 12, 18), getDate(2012, 12, 31), getDate(2013, 5, 11), "Inaktiv", "Individstønad", "Avbrutt", "Kontroll"));
    }

    private static FimVedtak populateVedtak(FimVedtak vedtak, String status, String type, Date beslutningsDato, FimPeriode periode) {
        return vedtak
                .withStatus(status)
                .withVedtakstype(type)
                .withBeslutningsdato(convertDateToXmlGregorianCalendar(beslutningsDato))
                .withVedtaksperiode(periode);
    }

    private static FimPeriode createPeriode(Date fom, Date tom) {
        return new FimPeriode()
                .withFom(convertDateToXmlGregorianCalendar(fom))
                .withTom(convertDateToXmlGregorianCalendar(tom));
    }

    private static FimYtelseskontrakt createYtelsesKontrakt(Date datoKravMottat, Date fom, Date tom, String status, String ytelsestype, String vedtaksstatus, String vedtakstype) {
        return createYtelsesKontrakt(datoKravMottat, fom, tom, status, ytelsestype)
                .withIhtVedtak(createVedtakWithRandomDates(vedtaksstatus, vedtakstype));
    }

    private static FimYtelseskontrakt createYtelsesKontrakt(Date datoKravMottat, Date fom, Date tom, String status, String ytelsestype) {
        return populateYtelsesKontrakt(new FimYtelseskontrakt(), datoKravMottat, fom, tom, status, ytelsestype);
    }

    private static FimVedtak createVedtakWithRandomDates(String status, String type) {
        Date[] datePair = getRandomDatePair();
        return populateVedtak(new FimVedtak(), status, type, getRandomDate(), createPeriode(datePair[0], datePair[1]));
    }

    private static FimDagpengekontrakt createDagpengekontrakt(Date datoKravMottat, Date fom, Date tom, String status, String ytelsestype, BigInteger antallDagerIgjen, FimVedtak... vedtak) {
        FimDagpengekontrakt kontrakt = new FimDagpengekontrakt();
        populateYtelsesKontrakt(kontrakt, datoKravMottat, fom, tom, status, ytelsestype);
        return kontrakt
                .withAntallDagerIgjen(antallDagerIgjen)
                .withAntallUkerIgjen(valueOf(round((antallDagerIgjen.floatValue() / 7))))
                .withIhtVedtak(vedtak);
    }

    private static FimYtelseskontrakt populateYtelsesKontrakt(FimYtelseskontrakt kontrakt, Date datoKravMottat, Date fom, Date tom, String status, String ytelsestype) {
        return kontrakt
                .withDatoKravMottatt(convertDateToXmlGregorianCalendar(datoKravMottat))
                .withFomGyldighetsperiode(convertDateToXmlGregorianCalendar(fom))
                .withTomGyldighetsperiode(convertDateToXmlGregorianCalendar(tom))
                .withStatus(status)
                .withYtelsestype(ytelsestype);
    }

}
