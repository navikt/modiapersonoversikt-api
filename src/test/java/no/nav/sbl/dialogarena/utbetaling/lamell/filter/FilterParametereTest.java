package no.nav.sbl.dialogarena.utbetaling.lamell.filter;


import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Mottakertype;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.defaultSluttDato;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.defaultStartDato;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.*;

public class FilterParametereTest {

    private static final String DAGPENGER = "Dagpenger";
    private static final String BARNETRYGD = "Barnetrygd";
    private static final String ID = "id";

    private FilterParametere filterparams;

    @Before
    public void settOppFilter() {
        filterparams = new FilterParametere(new HashSet<>(asList(DAGPENGER, BARNETRYGD)));
    }

    @Test
    public void filtrererBortUtbetalingUtenforDatointervall() {
        Record<Hovedytelse> ytelse = new Record<Hovedytelse>()
                .with(Hovedytelse.id, ID)
                .with(Hovedytelse.hovedytelsedato, now().minusYears(2))
                .with(Hovedytelse.mottakertype, Mottakertype.BRUKER)
                .with(Hovedytelse.ytelse, DAGPENGER);

        assertFalse(filterparams.evaluate(ytelse));
    }

    @Test
    public void filtrererBortUtbetalingForAnnenMottakertype() {
        Record<Hovedytelse> ytelse = new Record<Hovedytelse>()
                .with(Hovedytelse.id, ID)
                .with(Hovedytelse.hovedytelsedato, now())
                .with(Hovedytelse.mottakertype, Mottakertype.ANNEN_MOTTAKER)
                .with(Hovedytelse.ytelse, DAGPENGER);

        filterparams.toggleMottaker(Mottakertype.ANNEN_MOTTAKER);

        assertFalse(filterparams.evaluate(ytelse));
    }

    @Test
    public void skalBeholdeYtelsenHvisUtbetalingenInneholderEnYtelseManVilHa() {
        Record<Hovedytelse> ytelse = new Record<Hovedytelse>()
                .with(Hovedytelse.id, ID)
                .with(Hovedytelse.hovedytelsedato, now())
                .with(Hovedytelse.ytelse, BARNETRYGD)
                .with(Hovedytelse.mottakertype, Mottakertype.BRUKER);

        filterparams.leggTilOnsketYtelse(BARNETRYGD);

        assertTrue(filterparams.evaluate(ytelse));
    }

    @Test
    public void skalIkkeBeholdeYtelsenHvisAlleUtbetalingerErUonskede() {
        Record<Hovedytelse> ytelse = new Record<Hovedytelse>()
                .with(Hovedytelse.id, ID)
                .with(Hovedytelse.hovedytelsedato, now())
                .with(Hovedytelse.mottakertype, Mottakertype.BRUKER)
                .with(Hovedytelse.ytelse, DAGPENGER);

        filterparams.velgEnYtelse(BARNETRYGD);

        assertFalse(filterparams.evaluate(ytelse));
    }

    @Test
    public void skalViseAlleUtbetalingerHvisAlleYtelserErValgtOgNyeYtelserBlirSatt() {
        filterparams = new FilterParametere(new HashSet<String>());

        Record<Hovedytelse> ytelse = new Record<Hovedytelse>()
                .with(Hovedytelse.id, ID)
                .with(Hovedytelse.hovedytelsedato, now())
                .with(Hovedytelse.mottakertype, Mottakertype.BRUKER)
                .with(Hovedytelse.ytelse, DAGPENGER);
        filterparams.setYtelser(new HashSet<>(asList(ytelse.get(Hovedytelse.ytelse))));

        assertThat(filterparams.evaluate(ytelse), is(true));
    }

    @Test
    public void skalToggleVerdiBasertPaaMottaker() {
        assertThat(filterparams.viseMottaker(Mottakertype.BRUKER.BRUKER), is(true));
        filterparams.toggleMottaker(Mottakertype.BRUKER);
        assertThat(filterparams.viseMottaker(Mottakertype.BRUKER), is(false));
        filterparams.toggleMottaker(Mottakertype.BRUKER);
        assertThat(filterparams.viseMottaker(Mottakertype.BRUKER), is(true));
    }

    @Test
    public void skalIkkeSetteDatoerHvisParameterErNull() {
        filterparams.setStartDato(null);
        assertThat(filterparams.getStartDato(), is(defaultStartDato()));

        filterparams.setSluttDato(null);
        assertThat(filterparams.getSluttDato(), is(defaultSluttDato()));
    }

    @Test
    public void skalSetteDatoer() {
        LocalDate startDato = now().toLocalDate();
        filterparams.setStartDato(startDato);
        assertThat(filterparams.getStartDato(), is(startDato));

        LocalDate sluttDato = now().plusDays(1).toLocalDate();
        filterparams.setSluttDato(sluttDato);
        assertThat(filterparams.getSluttDato(), is(sluttDato));
    }

    @Test
    public void skalToggleAlleYtelserSomOnsket() {
        Record<Hovedytelse> ytelse = new Record<Hovedytelse>()
                .with(Hovedytelse.id, ID)
                .with(Hovedytelse.hovedytelsedato, now())
                .with(Hovedytelse.mottakertype, Mottakertype.BRUKER);

        filterparams.toggleAlleYtelser(true);

        assertThat(filterparams.evaluate(ytelse.with(Hovedytelse.ytelse, DAGPENGER)), is(true));
        assertThat(filterparams.evaluate(ytelse.with(Hovedytelse.ytelse ,BARNETRYGD)), is(true));
    }

    @Test
    public void skalToggleAlleYtelserSomUonsket() {
        Record<Hovedytelse> ytelse = new Record<Hovedytelse>()
                .with(Hovedytelse.id, ID)
                .with(Hovedytelse.hovedytelsedato, now())
                .with(Hovedytelse.mottakertype, Mottakertype.BRUKER);

        filterparams.toggleAlleYtelser(false);
        assertThat(filterparams.evaluate(ytelse.with(Hovedytelse.ytelse, DAGPENGER)), is(false));
        assertThat(filterparams.evaluate(ytelse.with(Hovedytelse.ytelse, BARNETRYGD)), is(false));
    }
}
