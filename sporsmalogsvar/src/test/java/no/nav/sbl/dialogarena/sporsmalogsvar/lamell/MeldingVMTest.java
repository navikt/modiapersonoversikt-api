package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.modia.widget.utils.WidgetDateFormatter;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.DateUtils;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static java.util.Optional.empty;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.VisningUtils.FRA_NAV;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.VisningUtils.lagMeldingStatusTekstKey;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM.*;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@ExtendWith(SpringExtension.class)
public class MeldingVMTest extends WicketPageTest {

    public static final DateTime FERDIGSTILT_DATO = DateTime.now().minusDays(2);
    public static final String NAV_IDENT = "navIdent";
    private MeldingVM meldingVM;

    @BeforeEach
    public void setUp() {
        Melding melding = createMelding(ID_1, SPORSMAL_SKRIFTLIG, FERDIGSTILT_DATO, TEMAGRUPPE_1, ID_1);
        melding.ferdigstiltDato = FERDIGSTILT_DATO;
        meldingVM = new MeldingVM(melding, 1);
    }

    @Test
    public void henterAvsenderDatoBasertPaaDato() {
        String avsenderTekst = meldingVM.getVisningsDato();

        assertThat(avsenderTekst, is(DateUtils.toString(FERDIGSTILT_DATO)));
    }

    @Test
    public void lagerStatusTekstKey() {
        assertThat(meldingVM.getMeldingStatusTekstKey(), is(lagMeldingStatusTekstKey(meldingVM.melding.meldingstype)));
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

        meldingVM.melding.markertSomFeilsendtAv = new Saksbehandler("", "", NAV_IDENT);

        assertThat(meldingVM.erFeilsendt(), is(true));
    }

    @Test
    public void henterMarkertSomFeilsendtAv() {
        assertThat(meldingVM.getMarkertSomFeilsendtAv(), is(empty()));

        meldingVM.melding.markertSomFeilsendtAv = new Saksbehandler("", "", NAV_IDENT);

        assertThat(meldingVM.getMarkertSomFeilsendtAv().get().getIdent(), is(NAV_IDENT));
    }

    @Test
    public void henterAvsenderBildeUrl() {
        assertThat(meldingVM.getAvsenderBildeUrl().contains(BRUKER_LOGO_SVG), is(true));

        Melding melding = createMelding(ID_1, FRA_NAV.get(0), FERDIGSTILT_DATO, TEMAGRUPPE_1, ID_1);
        meldingVM = new MeldingVM(melding, 1);

        assertThat(meldingVM.getAvsenderBildeUrl().contains(NAV_LOGO_SVG), is(true));
    }

    @Test
    public void henterAvsenderBilderAltKey() {
        assertThat(meldingVM.getAvsenderBildeAltKey(), is(BRUKER_AVSENDER_BILDE_ALT_KEY));

        Melding melding = createMelding(ID_1, FRA_NAV.get(0), FERDIGSTILT_DATO, TEMAGRUPPE_1, ID_1);
        meldingVM = new MeldingVM(melding, 1);

        assertThat(meldingVM.getAvsenderBildeAltKey(), is(NAV_AVSENDER_BILDE_ALT_KEY));
    }

    @Test
    public void setterIkkeErDokumentMeldingOmVanligMelding() {
        assertThat(meldingVM.erDokumentMelding, is(false));
    }

    @Test
    public void setterErDokumentMeldingOmMeldingErDokumentVarsel() {
        Melding melding = createMelding(ID_1, SPORSMAL_SKRIFTLIG, FERDIGSTILT_DATO, TEMAGRUPPE_1, ID_1);
        melding.erDokumentMelding = true;
        meldingVM = new MeldingVM(melding, 1);
        assertThat(meldingVM.erDokumentMelding, is(true));
    }
}
