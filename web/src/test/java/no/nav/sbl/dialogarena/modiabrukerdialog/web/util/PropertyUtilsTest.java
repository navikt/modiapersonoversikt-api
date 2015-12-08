package no.nav.sbl.dialogarena.modiabrukerdialog.web.util;

import org.junit.Before;
import org.junit.Test;

import static java.lang.System.setProperty;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PropertyUtilsTest {

    @Before
    public void setUp() {
        System.clearProperty(PropertyUtils.VIS_UTBETALINGER);
        System.clearProperty(PropertyUtils.UTBETALING_TILGANG_LISTE);
    }

    @Test
    public void returnererTrueHvisVisUtbetalingerErSann() {
        setProperty(PropertyUtils.VIS_UTBETALINGER, "true");

        assertThat(PropertyUtils.utbetalingerErPaa(), is(true));
    }

    @Test
    public void rerturnererFalseHvisVisUtbetalingerErUsann() {
        setProperty(PropertyUtils.VIS_UTBETALINGER, "false");

        assertThat(PropertyUtils.utbetalingerErPaa(), is(false));
    }

    @Test
    public void returnererTrueHvisEnhetsIdErITilgangslisteMedEnEnhet() {
        String enhetsId = "1";
        String enhetsIdListe = "[1]";

        setProperty(PropertyUtils.UTBETALING_TILGANG_LISTE, enhetsIdListe);

        assertThat(PropertyUtils.enhetHarTilgangTilUtbetaling(enhetsId), is(true));
    }

    @Test
    public void returnererTrueHvisEnhetsIdErITilgangslisteMedFlereEnheter() {
        String enhetsId = "1";
        String enhetsIdListe = "[1,2]";

        setProperty(PropertyUtils.UTBETALING_TILGANG_LISTE, enhetsIdListe);

        assertThat(PropertyUtils.enhetHarTilgangTilUtbetaling(enhetsId), is(true));
    }

    @Test
    public void returnererTrueHvisEnhetsIdIkkeErITilgangsliste() {
        String enhetsId = "1";
        String enhetsIdListe = "[2,3]";

        setProperty(PropertyUtils.UTBETALING_TILGANG_LISTE, enhetsIdListe);

        assertThat(PropertyUtils.enhetHarTilgangTilUtbetaling(enhetsId), is(false));
    }

    @Test
    public void returnererTrueHvisEnhetsIdErITilgangslisteOgVisUtbetalingerErSann() {
        String enhetsId = "1";
        String enhetsIdListe = "[1]";

        setProperty(PropertyUtils.VIS_UTBETALINGER, "true");
        setProperty(PropertyUtils.UTBETALING_TILGANG_LISTE, enhetsIdListe);

        assertThat(PropertyUtils.visUtbetalinger(enhetsId), is(true));
    }

    @Test
    public void returnererFalseHvisEnhetsIdErIkkeITilgangslisteOgVisUtbetalingerErSann() {
        String enhetsId = "1";
        String enhetsIdListe = "[1]";

        setProperty(PropertyUtils.VIS_UTBETALINGER, "false");
        setProperty(PropertyUtils.UTBETALING_TILGANG_LISTE, enhetsIdListe);

        assertThat(PropertyUtils.visUtbetalinger(enhetsId), is(false));
    }

    @Test
    public void returnererFalseHvisEnhetsIdErITilgangslisteOgVisUtbetalingerErUsann() {
        String enhetsId = "2";
        String enhetsIdListe = "[1]";

        setProperty(PropertyUtils.VIS_UTBETALINGER, "true");
        setProperty(PropertyUtils.UTBETALING_TILGANG_LISTE, enhetsIdListe);

        assertThat(PropertyUtils.visUtbetalinger(enhetsId), is(false));
    }

    @Test
    public void enhetHarTilgangHvisUtbetalingListeErTom() {
        String enhetsId = "2";
        String enhetsIdListe = "[]";

        setProperty(PropertyUtils.VIS_UTBETALINGER, "true");
        setProperty(PropertyUtils.UTBETALING_TILGANG_LISTE, enhetsIdListe);

        assertThat(PropertyUtils.visUtbetalinger(enhetsId), is(true));
    }

}