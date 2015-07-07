package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.lang.option.Optional;
import no.nav.modig.modia.widget.utils.WidgetDateFormatter;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.DateUtils;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.VisningUtils.*;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM.*;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
public class MeldingVMTest extends WicketPageTest {

    public static final DateTime OPPRETTET_DATO = DateTime.now();
    public static final String NAV_IDENT = "navIdent";
    private MeldingVM meldingVM;

    @Before
    public void setUp() {
        Melding melding = createMelding(ID_1, SPORSMAL_SKRIFTLIG, OPPRETTET_DATO, TEMAGRUPPE_1, ID_1);
        meldingVM = new MeldingVM(melding, 1);
    }

    @Test
    public void henterAvsenderDatoBasertPaaDato() {
        String avsenderTekst = meldingVM.getAvsenderDato();

        assertThat(avsenderTekst, is(DateUtils.dateTime(OPPRETTET_DATO)));
    }

    @Test
    public void lagerStatusTekstKey() {
        assertThat(meldingVM.getMeldingStatusTekstKey(), is(lagMeldingStatusTekstKey(meldingVM.melding)));
    }

    @Test
    public void formattererJournalfortDato() {
        String formatert = meldingVM.getJournalfortDatoFormatert();

        assertThat(formatert.isEmpty(), is(true));

        meldingVM.melding.journalfortDato = DateTime.now();
        formatert = meldingVM.getJournalfortDatoFormatert();

        assertThat(formatert, is(WidgetDateFormatter.date(meldingVM.melding.journalfortDato)));
    }

    @Test
    public void sjekkerOmMeldingErJournalfort() {
        assertThat(meldingVM.isJournalfort(), is(false));

        meldingVM.melding.journalfortDato = DateTime.now();

        assertThat(meldingVM.isJournalfort(), is(true));
    }

    @Test
    public void sjekkerOmMeldingErMarkertSomFeilsendt() {
        assertThat(meldingVM.erFeilsendt(), is(false));

        meldingVM.melding.markertSomFeilsendtAv = NAV_IDENT;

        assertThat(meldingVM.erFeilsendt(), is(true));
    }

    @Test
    public void henterMarkertSomFeilsendtAv() {
        assertThat(meldingVM.getMarkertSomFeilsendtAv(), is(Optional.<String>none()));

        meldingVM.melding.markertSomFeilsendtAv = NAV_IDENT;

        assertThat(meldingVM.getMarkertSomFeilsendtAv(), is(optional(NAV_IDENT)));
    }

    @Test
    public void henterAvsenderBildeUrl() {
        assertThat(meldingVM.getAvsenderBildeUrl().contains(BRUKER_LOGO_SVG), is(true));

        Melding melding = createMelding(ID_1, FRA_NAV.get(0), OPPRETTET_DATO, TEMAGRUPPE_1, ID_1);
        meldingVM = new MeldingVM(melding, 1);

        assertThat(meldingVM.getAvsenderBildeUrl().contains(NAV_LOGO_SVG), is(true));
    }

    @Test
    public void henterAvsenderBilderAltKey() {
        assertThat(meldingVM.getAvsenderBildeAltKey(), is(BRUKER_AVSENDER_BILDE_ALT_KEY));

        Melding melding = createMelding(ID_1, FRA_NAV.get(0), OPPRETTET_DATO, TEMAGRUPPE_1, ID_1);
        meldingVM = new MeldingVM(melding, 1);

        assertThat(meldingVM.getAvsenderBildeAltKey(), is(NAV_AVSENDER_BILDE_ALT_KEY));
    }
}
