package no.nav.sbl.dialogarena.utbetaling.filter;


import no.nav.sbl.dialogarena.utbetaling.domain.Bilag;
import no.nav.sbl.dialogarena.utbetaling.domain.BilagBuilder;
import no.nav.sbl.dialogarena.utbetaling.domain.FilterParametereBuilder;
import no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetalj;
import no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetaljBuilder;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.UtbetalingBuilder;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.HashSet;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.utbetaling.filter.Filter.filtrer;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FilterTest {

    public static final String DAGPENGER = "Dagpenger";
    public static final String SYKEPENGER = "Sykepenger";
    public static final String BARNETRYGD = "Barnetrygd";
    public static final String SKATT = "Skatt";

    @Test
    public void testFilter() throws Exception {
        DateTime startDate = new LocalDate().minusMonths(1).plusDays(1).toDateTimeAtStartOfDay();

        PosteringsDetalj dagpenger = new PosteringsDetaljBuilder().setHovedBeskrivelse(DAGPENGER).createPosteringsDetalj();
        PosteringsDetalj barnetrygd = new PosteringsDetaljBuilder().setHovedBeskrivelse(BARNETRYGD).createPosteringsDetalj();
        PosteringsDetalj skatt = new PosteringsDetaljBuilder().setHovedBeskrivelse(SKATT).createPosteringsDetalj();
        Bilag bilag1 = new BilagBuilder().setPosteringsDetaljer(asList(dagpenger, barnetrygd, skatt)).createBilag();
        Utbetaling utbetaling1 = new UtbetalingBuilder().setUtbetalingsDato(startDate).setBilag(asList(bilag1)).createUtbetaling();

        filterOgAssert(utbetaling1, true, BARNETRYGD, SYKEPENGER);
        filterOgAssert(utbetaling1, false, SYKEPENGER);
        filterOgAssert(utbetaling1, true, BARNETRYGD, DAGPENGER);
    }

    private void filterOgAssert(Utbetaling utbetaling1, boolean value, String... barnetrygd1) {
        FilterParametere filterparams1 = new FilterParametereBuilder().setHovedYtelser(new HashSet<>(asList(barnetrygd1))).createFilterParametere();
        boolean filterResultat1 = filtrer(utbetaling1, filterparams1);
        assertThat(filterResultat1, is(value));
    }
}
