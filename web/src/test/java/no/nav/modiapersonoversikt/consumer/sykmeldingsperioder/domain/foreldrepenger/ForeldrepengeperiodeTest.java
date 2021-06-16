package no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.foreldrepenger;


import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Periode;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.HistoriskUtbetaling;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.Kodeverkstype;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.KommendeUtbetaling;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ForeldrepengeperiodeTest {

    public static final LocalDate PERIODEFRA = new LocalDate(2013, 1, 1);
    public static final LocalDate PERIODETOM = new LocalDate(2014, 1, 1);

    public static final boolean ALENEOMSORGFAR = false;
    public static final boolean ALENEOMSORGMOR = true;
    public static final Double ARBEIDSPROSENTMOR = 0.50;
    public static final LocalDate AVSLAGSDATO = new LocalDate(2013, 2, 1);
    public static final Double DISPONIBELGRADERING = 60.0;
    public static final boolean FEDREKVOTE = false;
    public static final Kodeverkstype FORSKYVELSESAARSAK = new Kodeverkstype("kode", "term");
    public static final Periode FORSKYVELSESPERIODE = new Periode(PERIODEFRA, PERIODETOM);
    public static final LocalDate IDDATO = new LocalDate(2015, 1, 1);
    public static final Kodeverkstype RETTTILFEDREKVOTE = new Kodeverkstype("fkode", "fnei");
    public static final Kodeverkstype RETTTILMODREKVOTE = new Kodeverkstype("mkode", "mja");
    public static final Kodeverkstype MORSSITUASJON = new Kodeverkstype("kode", "term");
    public static final Periode UTSETTELSE = new Periode(PERIODEFRA, PERIODETOM);
    public static final HistoriskUtbetaling HISTORISK_UTBETALING = new HistoriskUtbetaling();
    public static final KommendeUtbetaling KOMMENDE_UTBETALING = new KommendeUtbetaling();
    public static final List<KommendeUtbetaling> KOMMENDE_UTBETALING_LIST = new ArrayList<>();

    @Test
    public void testBean() {
        Foreldrepengeperiode foreldrepengeperiode = new Foreldrepengeperiode();
        foreldrepengeperiode.setHarAleneomsorgFar(ALENEOMSORGFAR);
        foreldrepengeperiode.setHarAleneomsorgMor(ALENEOMSORGMOR);
        foreldrepengeperiode.setArbeidsprosentMor(ARBEIDSPROSENTMOR);
        foreldrepengeperiode.setAvslaatt(AVSLAGSDATO);
        foreldrepengeperiode.setDisponibelGradering(DISPONIBELGRADERING);
        foreldrepengeperiode.setErFedrekvote(FEDREKVOTE);
        foreldrepengeperiode.setForskyvelsesaarsak1(FORSKYVELSESAARSAK);
        foreldrepengeperiode.setForskyvelsesperiode(FORSKYVELSESPERIODE);
        foreldrepengeperiode.setForeldrepengerFom(IDDATO);
        foreldrepengeperiode.setMorSituasjon(MORSSITUASJON);
        foreldrepengeperiode.setRettTilFedrekvote(RETTTILFEDREKVOTE);
        foreldrepengeperiode.setRettTilModrekvote(RETTTILMODREKVOTE);
        foreldrepengeperiode.getHistoriskeUtbetalinger().add(HISTORISK_UTBETALING);
        KOMMENDE_UTBETALING_LIST.add(KOMMENDE_UTBETALING);
        foreldrepengeperiode.setKommendeUtbetalinger(KOMMENDE_UTBETALING_LIST);

        assertEquals(ALENEOMSORGFAR, foreldrepengeperiode.isHarAleneomsorgFar());
        assertEquals(ALENEOMSORGMOR, foreldrepengeperiode.isHarAleneomsorgMor());
        assertEquals(ARBEIDSPROSENTMOR, foreldrepengeperiode.getArbeidsprosentMor());
        assertEquals(AVSLAGSDATO, foreldrepengeperiode.getAvslaatt());
        assertEquals(DISPONIBELGRADERING, foreldrepengeperiode.getDisponibelGradering());
        assertEquals(FEDREKVOTE, foreldrepengeperiode.isErFedrekvote());
        assertEquals(FORSKYVELSESAARSAK, foreldrepengeperiode.getForskyvelsesaarsak1());
        assertEquals(FORSKYVELSESPERIODE, foreldrepengeperiode.getForskyvelsesperiode());
        assertEquals(IDDATO, foreldrepengeperiode.getForeldrepengerFom());
        assertEquals(MORSSITUASJON, foreldrepengeperiode.getMorSituasjon());
        assertEquals(RETTTILFEDREKVOTE, foreldrepengeperiode.getRettTilFedrekvote());
        assertEquals(RETTTILMODREKVOTE, foreldrepengeperiode.isRettTilModrekvote());
        assertEquals(HISTORISK_UTBETALING, foreldrepengeperiode.getHistoriskeUtbetalinger().get(0));
        assertEquals(KOMMENDE_UTBETALING, foreldrepengeperiode.getKommendeUtbetalinger().get(0));
    }
}
