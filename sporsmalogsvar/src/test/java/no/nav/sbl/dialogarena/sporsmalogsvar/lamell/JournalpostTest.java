package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.MockServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.createMelding;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@DirtiesContext(classMode = AFTER_CLASS)
@ContextConfiguration(classes = {MockServiceTestContext.class})
@ExtendWith(SpringExtension.class)
public class JournalpostTest extends WicketPageTest {

    private Melding melding;
    private IModel<MeldingVM> meldingVMModel;

    @BeforeEach
    public void setUp() {
        melding = createMelding("id", Meldingstype.SPORSMAL_SKRIFTLIG, DateTime.now(), Temagruppe.ARBD, "id");
        meldingVMModel = new Model<>(new MeldingVM(melding, 1));
    }

    @Test
    public void skalViseIngenJournalpostDersomMeldingenIkkeErJournalfort() {
        wicket.goToPageWith(new Journalpost("id", meldingVMModel))
                .should().containComponent(thatIsVisible().and(withId("ingenJournalpostInformasjon")));
    }

    @Test
    public void skalIkkeViseJournalpostInformasjonEllerLenkeSomAapnerDenneDersomMeldingernIkkeErJournalfort() {
        wicket.goToPageWith(new Journalpost("id", meldingVMModel))
                .should().containComponent(thatIsInvisible().and(withId("aapneJournalpostInformasjon")))
                .should().containComponent(thatIsInvisible().and(withId("journalpostInformasjon")));
    }

    @Test
    public void skalViseAapneJournalpostInformasjonslenkeDersomMeldingerErJournalfort() {
        settMeldingSomJournalfort();

        wicket.goToPageWith(new Journalpost("id", meldingVMModel))
                .should().containComponent(thatIsInvisible().and(withId("ingenJournalpostInformasjon")))
                .should().containComponent(thatIsVisible().and(withId("aapneJournalpostInformasjon")))
                .should().containComponent(thatIsInvisible().and(withId("journalpostInformasjon")));
    }

    @Test
    public void skalViseJournalpostInformasjonDersomManKlikkerPaaLenke() {
        settMeldingSomJournalfort();

        wicket.goToPageWith(new Journalpost("id", meldingVMModel))
                .click().link(withId("aapneJournalpostInformasjon"))
                .should().containComponent(thatIsInvisible().and(withId("ingenJournalpostInformasjon")))
                .should().containComponent(thatIsVisible().and(withId("aapneJournalpostInformasjon")))
                .should().containComponent(thatIsVisible().and(withId("journalpostInformasjon")));
    }

    private void settMeldingSomJournalfort() {
        melding.journalfortDato = DateTime.now();
        melding.journalfortTema = "TEM";
        melding.journalfortTemanavn = "temanavn";
        melding.journalfortAvNavIdent = "navident";
        melding.journalfortSaksId = "saksid";
    }
}