package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.getBuilder;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.hentUtbetalingerFraPeriode;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.splittUtbetalingerPerMaaned;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class UtbetalingListeUtilsTest {

    private static List<Utbetaling> utbetalingsliste;

    @BeforeClass
    public static void settOppUtbetalingsliste() {
        Utbetaling utbetaling1 = getBuilder().withUtbetalingsDato(now()).withMelding("utbetaling1").createUtbetaling();
        Utbetaling utbetaling2 = getBuilder().withUtbetalingsDato(now()).withMelding("utbetaling2").createUtbetaling();
        Utbetaling utbetaling3 = getBuilder().withUtbetalingsDato(now().minusMonths(1)).withMelding("utbetaling3").createUtbetaling();
        Utbetaling utbetaling4 = getBuilder().withUtbetalingsDato(now().minusMonths(3)).withMelding("utbetaling4").createUtbetaling();

        utbetalingsliste = asList(utbetaling1, utbetaling2, utbetaling3, utbetaling4);
    }

    @Test
    public void splittUtbetalingerPerMaaned_splittetIRiktigAntallMaaneder() {
        List<List<Utbetaling>> maanedsliste = splittUtbetalingerPerMaaned(utbetalingsliste);

        assertThat(maanedsliste.size(), is(3));
    }

    @Test
    public void splittUtbetalingerPerMaaned_hverMaanedHarRiktigAntallUtbetalinger() {
        List<List<Utbetaling>> maanedsliste = splittUtbetalingerPerMaaned(utbetalingsliste);

        assertThat(maanedsliste.get(0).size(), is(2));
        assertThat(maanedsliste.get(1).size(), is(1));
        assertThat(maanedsliste.get(2).size(), is(1));
    }

    @Test
    public void splittUtbetalingerPerMaaned_inneholderRiktigUtbetalingPerMaaned() {
        List<List<Utbetaling>> maanedsliste = splittUtbetalingerPerMaaned(utbetalingsliste);

        assertThat(maanedsliste.get(0).get(0).getMelding(), is("utbetaling1"));
        assertThat(maanedsliste.get(0).get(1).getMelding(), is("utbetaling2"));
        assertThat(maanedsliste.get(1).get(0).getMelding(), is("utbetaling3"));
        assertThat(maanedsliste.get(2).get(0).getMelding(), is("utbetaling4"));
    }

    @Test
    public void hentUtbetalingerFraPeriode_inneholderRiktigAntallUtbetalinger() {
        List<Utbetaling> utbetalingsperiode = hentUtbetalingerFraPeriode(utbetalingsliste, now().toLocalDate().minusMonths(2), now().toLocalDate());

        assertThat(utbetalingsperiode.size(), is(3));
    }

    @Test
    public void hentUtbetalingerFraPeriode_inneholderKunUtbetalingerInnenforPeriode() {
        DateTime startDato = now().minusMonths(2);
        DateTime sluttDato = now();
        Interval intervall = new Interval(startDato, sluttDato.plusDays(1));

        List<Utbetaling> utbetalingsperiode = hentUtbetalingerFraPeriode(utbetalingsliste, startDato.toLocalDate(), sluttDato.toLocalDate());

        for (Utbetaling utbetaling : utbetalingsperiode) {
            assertTrue(intervall.contains(utbetaling.getUtbetalingsDato()));
        }
    }

}
