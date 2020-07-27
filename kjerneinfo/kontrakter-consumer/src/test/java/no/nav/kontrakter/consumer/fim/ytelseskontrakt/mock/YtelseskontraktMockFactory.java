package no.nav.kontrakter.consumer.fim.ytelseskontrakt.mock;

import no.nav.kjerneinfo.common.utils.DateUtils;
import no.nav.kontrakter.consumer.utils.FimPeriodeUtil;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.FimDagpengekontrakt;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.FimPeriode;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.FimVedtak;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.FimYtelseskontrakt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Genererer FimYtelseskontrakt-objekter for testformål.
 */
public final class YtelseskontraktMockFactory {

    public static final Date DATO_KRAV_MOTTAT = new Date();
    public static final Date FOM_GYLDIGHETSPERIODE = new Date();
    public static final Date TOM_GYLDIGHETSPERIODE = new Date();
    public static final Date VEDTAK_BESLUTNINGSDATO = new Date();
    public static final String PERIODE_FOR_YTELSE = "periodeForYtelse";
    public static final int UTTAKSGRAD = 200;
    public static final int VEDTAK_BRUTTO_BELOEP = 100000;
    public static final int VEDTAK_NETTO_BELOEP = 75000;
    public static final Date FOM = new Date();
    public static final Date TOM = new Date();
    public static final String YTELSESTYPE_DAG = "Dagpenger";
    public static final String YTELSESTYPE_IND = "Individstønad";
    public static final String YTELSESTYPE_APP = "Arbeidsavklaringspenger";
    public static final String YTELSESSTATUS_AKTIV = "Aktiv";
    public static final String YTELSESSTATUS_LUKKET = "Lukket";
    public static final String YTELSESSTATUS_INAKTIV = "Inaktiv";
    public static final String VEDTAKSTYPE_ENDRING = "Endring";
    public static final String VEDTAKSTYPE_STANS = "Stans";
    public static final String VEDTAKSTYPE_KONTROLL = "Kontroll";
    public static final String VEDTAKSSTATUS_INNVILGET = "Innvilget";
    public static final String VEDTAKSSTATUS_AVSLAAT = "Avslått";
    public static final String VEDTAKSSTATUS_AVBRUTT = "Avbrutt";
    public static final String VEDTAK_AKTIVITETSFASE = "Under arbeid";
    private static final int ANTALL_DAGER_IGJEN = 140;
    private static final int BORTFALL_PROSENT_DAGER_IGJEN = 60;
    private static final int DAGER_IGJEN_PERMITTERING = 50;

    /**
     * Returns a mock Ytelseskontrakt object. All values in the object are decleard as constanst in this class.
     *
     * @return A mock Ytelseskontrakt object
     */
    public static FimYtelseskontrakt createYtelsesKontrakt() {
        FimYtelseskontrakt ytelsesKontrakt = createYtelsesKontrakt(DATO_KRAV_MOTTAT, FOM_GYLDIGHETSPERIODE, TOM_GYLDIGHETSPERIODE, YTELSESSTATUS_AKTIV, YTELSESTYPE_APP, BORTFALL_PROSENT_DAGER_IGJEN);
        ytelsesKontrakt.withIhtVedtak(createVedtak());
        return ytelsesKontrakt;
    }

    /**
     * Returns a list with mocked ytelsekontrakter.
     *
     * @return List of Ytelseskontrakt mock objects
     */
    public static List<FimYtelseskontrakt> createYtelsesKontrakter() {
        return Arrays.asList(
                createDagpengekontrakt(),
                createYtelsesKontrakt(DateUtils.getRandomDate(), DateUtils.getRandomDate(), DateUtils.getRandomDate(), YTELSESSTATUS_LUKKET, YTELSESTYPE_IND, VEDTAKSSTATUS_AVBRUTT, VEDTAKSTYPE_STANS, BORTFALL_PROSENT_DAGER_IGJEN),
                createYtelsesKontrakt(DateUtils.getRandomDate(), DateUtils.getRandomDate(), DateUtils.getRandomDate(), YTELSESSTATUS_INAKTIV, YTELSESTYPE_APP, VEDTAKSSTATUS_INNVILGET, VEDTAKSTYPE_ENDRING, BORTFALL_PROSENT_DAGER_IGJEN));
    }

    /**
     * Mocks for test persons.
     *
     * @param fnr
     * @param fra
     * @param til
     * @return List of Yrkeskontrakt mock objects filtered by date and fnr.
     */
    public static List<FimYtelseskontrakt> createYtelsesKontrakter(String fnr, Date fra, Date til) {

        if ("22222222222".equals(fnr)) {
            return filterByDates(createYtelsesKontrakterDonald(), fra, til);
        }

        return createYtelsesKontrakter();
    }

    private static List<FimYtelseskontrakt> filterByDates(List<? extends FimYtelseskontrakt> ytelsesKontrakter, Date fra, Date til) {

        List<FimYtelseskontrakt> resultYtelseskontraktList = new ArrayList<>();

        for (FimYtelseskontrakt ytelseskontrakt : ytelsesKontrakter) {

            FimYtelseskontrakt ytelseskontraktFiltered = filterByDates(ytelseskontrakt, fra, til);

            if (ytelseskontraktFiltered != null) {
                resultYtelseskontraktList.add(ytelseskontraktFiltered);
            }
        }

        return resultYtelseskontraktList;
    }

    private static FimYtelseskontrakt filterByDates(FimYtelseskontrakt ytelsesKontrakt, Date fra, Date til) {

        if (ytelsesKontrakt instanceof FimDagpengekontrakt || DateUtils.datoInside(ytelsesKontrakt.getDatoKravMottatt(), fra, til)) {

            List<FimVedtak> vedtakResultList = new ArrayList<>();

            for (FimVedtak fimVedtak : ytelsesKontrakt.getIhtVedtak()) {

                if (FimPeriodeUtil.periodeInside(fimVedtak.getVedtaksperiode(), fra, til)) {
                    vedtakResultList.add(fimVedtak);
                }
            }

            ytelsesKontrakt.getIhtVedtak().clear();
            ytelsesKontrakt.withIhtVedtak(vedtakResultList);

            return ytelsesKontrakt;
        }
        return null;
    }

    private static List<FimYtelseskontrakt> createYtelsesKontrakterDonald() {
        FimVedtak vedtak7 = new FimVedtak();
        vedtak7.setPeriodetypeForYtelse("Lang periode");
        vedtak7.setUttaksgrad(100);
        vedtak7.setVedtakBruttoBeloep(10000);
        vedtak7.setVedtakNettoBeloep(5000);
        FimVedtak vedtak = populateVedtak(vedtak7, "Innvilget", "Annuler sanksjon", DateUtils.getDate(2012, 12, 12), createPeriode(DateUtils.getDate(2012, 1, 1), DateUtils.getDate(2012, 3, 1)), VEDTAK_AKTIVITETSFASE);

        FimVedtak vedtak6 = new FimVedtak();
        vedtak6.setPeriodetypeForYtelse("Kort periode");
        vedtak6.setUttaksgrad(75);
        vedtak6.setVedtakBruttoBeloep(2000);
        vedtak6.setVedtakNettoBeloep(1000);
        FimVedtak vedtak2 = populateVedtak(vedtak6, "Avslått", "Forlenget ventetid", DateUtils.getDate(2012, 12, 12), createPeriode(DateUtils.getDate(2012, 2, 1), DateUtils.getDate(2012, 2, 15)), VEDTAK_AKTIVITETSFASE);

        FimVedtak vedtak5 = new FimVedtak();
        vedtak5.setPeriodetypeForYtelse("Kort periode");
        vedtak5.setUttaksgrad(75);
        vedtak5.setVedtakBruttoBeloep(2000);
        vedtak5.setVedtakNettoBeloep(1000);
        FimVedtak vedtak3 = populateVedtak(vedtak5, "Avslått", "Tidsbegrenset bortfall", DateUtils.getDate(2012, 12, 12), createPeriode(DateUtils.getDate(2012, 2, 15), DateUtils.getDate(2012, 2, 25)), VEDTAK_AKTIVITETSFASE);

        FimVedtak vedtak1 = new FimVedtak();
        vedtak1.setPeriodetypeForYtelse("Laaang periode");
        vedtak1.setUttaksgrad(25);
        vedtak1.setVedtakBruttoBeloep(3000);
        vedtak1.setVedtakNettoBeloep(1500);
        FimVedtak vedtak4 = populateVedtak(vedtak1, "Avbrutt", "Stans", DateUtils.getDate(2012, 12, 12), createPeriode(DateUtils.getDate(2012, 2, 20), DateUtils.getDate(2012, 2, 22)), VEDTAK_AKTIVITETSFASE);

        return Arrays.asList(
                createDagpengekontrakt(DateUtils.getDate(2012, 12, 10), DateUtils.getDate(2012, 12, 14), DateUtils.getDate(2013, 2, 10), "Aktiv", "Dagpenger", 34,
                vedtak,
                vedtak2,
                vedtak3,
                vedtak4),
                createYtelsesKontrakt(DateUtils.getDate(2012, 12, 14), DateUtils.getDate(2012, 12, 19), DateUtils.getDate(2013, 1, 10), "Lukket", "Arbeidsavklaringspenger", "Avslått", "Endring", BORTFALL_PROSENT_DAGER_IGJEN),
                createYtelsesKontrakt(DateUtils.getDate(2012, 12, 18), DateUtils.getDate(2012, 12, 31), DateUtils.getDate(2013, 5, 11), YTELSESSTATUS_INAKTIV, YTELSESTYPE_IND, VEDTAKSSTATUS_AVBRUTT,
                VEDTAKSTYPE_KONTROLL, BORTFALL_PROSENT_DAGER_IGJEN));
    }

    private static FimDagpengekontrakt createDagpengekontrakt() {
        FimDagpengekontrakt kontrakt = new FimDagpengekontrakt();
        populateYtelsesKontrakt(kontrakt, DateUtils.getRandomDate(), DateUtils.getRandomDate(), DateUtils.getRandomDate(), YTELSESSTATUS_AKTIV, YTELSESTYPE_DAG, BORTFALL_PROSENT_DAGER_IGJEN);
        kontrakt.setAntallDagerIgjen(ANTALL_DAGER_IGJEN);
        kontrakt.setAntallUkerIgjen(ANTALL_DAGER_IGJEN / 5);
        kontrakt.setAntallDagerIgjenUnderPermittering(DAGER_IGJEN_PERMITTERING);
        kontrakt.setAntallUkerIgjenUnderPermittering(DAGER_IGJEN_PERMITTERING / 5);
        kontrakt.withIhtVedtak(createVedtakWithRandomDates(VEDTAKSSTATUS_INNVILGET, VEDTAKSTYPE_ENDRING));
        kontrakt.withIhtVedtak(createVedtakWithRandomDates(VEDTAKSSTATUS_AVSLAAT, VEDTAKSTYPE_KONTROLL));
        return kontrakt;
    }

    private static FimDagpengekontrakt createDagpengekontrakt(Date datoKravMottat, Date fom, Date tom, String status, String ytelsestype, int antallDagerIgjen, FimVedtak... vedtak) {
        FimDagpengekontrakt kontrakt = new FimDagpengekontrakt();
        populateYtelsesKontrakt(kontrakt, datoKravMottat, fom, tom, status, ytelsestype, BORTFALL_PROSENT_DAGER_IGJEN);
        kontrakt.setAntallDagerIgjen(antallDagerIgjen);
        kontrakt.setAntallUkerIgjen(antallDagerIgjen / 5);
        kontrakt.setAntallDagerIgjenUnderPermittering(DAGER_IGJEN_PERMITTERING);
        kontrakt.setAntallUkerIgjenUnderPermittering(DAGER_IGJEN_PERMITTERING / 5);
        kontrakt.withIhtVedtak(vedtak);

        return kontrakt;
    }

    private static FimYtelseskontrakt createYtelsesKontrakt(Date datoKravMottat, Date fom, Date tom, String status, String ytelsestype, String vedtaksstatus, String vedtakstype, Integer bortfallProsentDagerIgjen) {
        FimYtelseskontrakt kontrakt = createYtelsesKontrakt(datoKravMottat, fom, tom, status, ytelsestype, bortfallProsentDagerIgjen);
        kontrakt.withIhtVedtak(createVedtakWithRandomDates(vedtaksstatus, vedtakstype));
        return kontrakt;
    }

    private static FimYtelseskontrakt createYtelsesKontrakt(Date datoKravMottat, Date fom, Date tom, String status, String ytelsestype, Integer bortfallProsentDagerIgjen) {
        FimYtelseskontrakt kontrakt = new FimYtelseskontrakt();
        populateYtelsesKontrakt(kontrakt, datoKravMottat, fom, tom, status, ytelsestype, bortfallProsentDagerIgjen);
        return kontrakt;
    }

    private static FimYtelseskontrakt populateYtelsesKontrakt(FimYtelseskontrakt kontrakt, Date datoKravMottat, Date fom, Date tom, String status, String ytelsestype, Integer bortfallProsentDagerIgjen) {
        kontrakt.setDatoKravMottatt(DateUtils.convertDateToXmlGregorianCalendar(datoKravMottat));

        kontrakt.setFomGyldighetsperiode(DateUtils.convertDateToXmlGregorianCalendar(fom));
        kontrakt.setTomGyldighetsperiode(DateUtils.convertDateToXmlGregorianCalendar(tom));
        kontrakt.setStatus(status);
        kontrakt.setYtelsestype(ytelsestype);
        kontrakt.setBortfallsprosentDagerIgjen(bortfallProsentDagerIgjen);
        kontrakt.setBortfallsprosentUkerIgjen(bortfallProsentDagerIgjen / 5);
        return kontrakt;
    }

    private static FimVedtak createVedtakWithRandomDates(String status, String type) {
        FimVedtak vedtak = new FimVedtak();
        Date[] datePair = DateUtils.getRandomDatePair();
        return populateVedtak(vedtak, status, type, DateUtils.getRandomDate(), createPeriode(datePair[0], datePair[1]), VEDTAK_AKTIVITETSFASE);
    }

    private static FimVedtak createVedtak() {
        FimVedtak vedtak = new FimVedtak();
        vedtak.setPeriodetypeForYtelse(PERIODE_FOR_YTELSE);
        vedtak.setUttaksgrad(UTTAKSGRAD);
        vedtak.setVedtakBruttoBeloep(VEDTAK_BRUTTO_BELOEP);
        vedtak.setVedtakNettoBeloep(VEDTAK_NETTO_BELOEP);
        return populateVedtak(vedtak, VEDTAKSSTATUS_INNVILGET, VEDTAKSTYPE_KONTROLL, VEDTAK_BESLUTNINGSDATO, createPeriode(), VEDTAK_AKTIVITETSFASE);
    }

    private static FimVedtak populateVedtak(FimVedtak vedtak, String status, String type, Date beslutningsDato, FimPeriode periode, String aktivitetsfase) {
        vedtak.setStatus(status);
        vedtak.setVedtakstype(type);
        vedtak.setBeslutningsdato(DateUtils.convertDateToXmlGregorianCalendar(beslutningsDato));
        vedtak.setVedtaksperiode(periode);
        vedtak.setAktivitetsfase(aktivitetsfase);
        return vedtak;
    }

    private static FimPeriode createPeriode() {
        return createPeriode(FOM, TOM);
    }

    private static FimPeriode createPeriode(Date fom, Date tom) {
        FimPeriode periode = new FimPeriode();
        periode.setFom(DateUtils.convertDateToXmlGregorianCalendar(fom));
        periode.setTom(DateUtils.convertDateToXmlGregorianCalendar(tom));
        return periode;
    }
}
