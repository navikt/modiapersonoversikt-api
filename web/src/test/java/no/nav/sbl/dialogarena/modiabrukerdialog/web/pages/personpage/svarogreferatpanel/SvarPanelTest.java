package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel;


import no.nav.modig.lang.option.Optional;
import no.nav.modig.wicket.test.matcher.BehaviorMatchers;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.ConsumerServicesMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.EndpointMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.LeggTilbakePanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.TidligereMeldingPanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Kanal.TEKST;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        ConsumerServicesMockContext.class,
        EndpointMockContext.class})
public class SvarPanelTest extends WicketPageTest {

    @Inject
    protected HenvendelseUtsendingService henvendelseUtsendingService;

    @Test
    public void inneholderSporsmaalsspefikkeKomponenter() {
        wicket.goToPageWith(new TestSvarPanel("id", "fnr", asList(lagSporsmal())))
                .should().containComponent(withId("temagruppe").and(ofType(Label.class)))
                .should().containComponent(withId("sporsmal").and(ofType(TidligereMeldingPanel.class)))
                .should().containComponent(withId("svarliste").and(ofType(ListView.class)))
                .should().containComponent(withId("dato").and(ofType(Label.class)))
                .should().containComponent(withId("kanal").and(ofType(RadioGroup.class)))
                .should().containComponent(withId("kanalbeskrivelse").and(ofType(Label.class)))
                .should().containComponent(withId("svarform"))
                .should().containComponent(withId("leggtilbakepanel").and(ofType(LeggTilbakePanel.class)))
                .should().containComponent(withId("leggtilbake").and(ofType(AjaxLink.class)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void skalSendeSvarTilHenvendelseDersomManVelgerTekstSomKanal() throws HenvendelseUtsendingService.OppgaveErFerdigstilt {
        wicket.goToPageWith(new TestSvarPanel("id", "fnr", asList(lagSporsmal())))
                .inForm(withId("svarform"))
                .write("tekstfelt:text", "dette er en fritekst")
                .select("kanal", 0)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(any(Henvendelse.class), any(Optional.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void skalSendeReferatTilHenvendelseDersomManVelgerTelefonSomKanal() throws HenvendelseUtsendingService.OppgaveErFerdigstilt {
        wicket.goToPageWith(new TestSvarPanel("id", "fnr", asList(lagSporsmal())))
                .inForm(withId("svarform"))
                .write("tekstfelt:text", "dette er en fritekst")
                .select("kanal", 1)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(any(Henvendelse.class), any(Optional.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void skalSendeReferatTilHenvendelseDersomManVelgerOppmoteSomKanal() throws HenvendelseUtsendingService.OppgaveErFerdigstilt {
        wicket.goToPageWith(new TestSvarPanel("id", "fnr", asList(lagSporsmal())))
                .inForm(withId("svarform"))
                .write("tekstfelt:text", "dette er en fritekst")
                .select("kanal", 2)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(any(Henvendelse.class), any(Optional.class));
    }

    @Test
    public void girFeedbackOmPaakrevdeKomponenter() {
        wicket.goToPageWith(new TestSvarPanel("id", "fnr", asList(lagSporsmal())))
                .inForm(withId("svarform"))
                .submitWithAjaxButton(withId("send"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages.isEmpty(), is(false));
    }

    @Test
    public void tekstligSvarErValgtSomDefault() {
        wicket.goToPageWith(new TestSvarPanel("id", "fnr", asList(lagSporsmal())))
                .should().containComponent(withId("kanal").and(withModelObject(is(TEKST))));
    }

    @Test
    public void viserTemagruppenFraSporsmalet() {
        TestSvarPanel svarPanel = new TestSvarPanel("id", "fnr", asList(lagSporsmal()));
        wicket.goToPageWith(svarPanel)
                .should().containComponent(withId("temagruppe").and(withTextSaying(svarPanel.getString(Temagruppe.FMLI.name()))));
    }

    @Test
    public void viserKvitteringNaarManSenderInn() {
        wicket.goToPageWith(new TestSvarPanel("id", "fnr", asList(lagSporsmal())))
                .inForm(withId("svarform"))
                .write("tekstfelt:text", "dette er en fritekst")
                .submitWithAjaxButton(withId("send"))
                .should().containComponent(thatIsInvisible().withId("svarform"))
                .should().containComponent(thatIsVisible().ofType(KvitteringsPanel.class));
    }

    @Test
    public void skalViseTraadToggleLenkeHvisSvarFinnes() {
        wicket.goToPageWith(new TestSvarPanel("id", "fnr", asList(lagSporsmal(), lagSvar())))
                .should().containComponent(thatIsVisible().and(withId("vistraadcontainer")));
    }

    @Test
    public void skalIkkeViseTraadToggleLenkeHvisIngenSvarFinnes() {
        wicket.goToPageWith(new TestSvarPanel("id", "fnr", asList(lagSporsmal())))
                .should().containComponent(thatIsInvisible().and(withId("vistraadcontainer")));
    }

    @Test
    public void skalToggleVisningAvTraad() {
        wicket.goToPageWith(new TestSvarPanel("id", "fnr", asList(lagSporsmal(), lagSvar())))
                .should().containComponent(thatIsInvisible().and(withId("traadcontainer")))
                .onComponent(withId("vistraadcontainer")).executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxEventBehavior.class))
                .should().containComponent(thatIsVisible().and(withId("traadcontainer")));
    }

    @Test
    public void skalViseLeggTilbakePanel() {
        wicket.goToPageWith(new TestSvarPanel("id", "fnr", asList(lagSporsmal())))
                .should().containComponent(thatIsInvisible().and(withId("leggtilbakepanel")))
                .click().link(withId("leggtilbake"))
                .should().containComponent(thatIsVisible().and(withId("leggtilbakepanel")));
    }

    @Test
    public void leggTilbakeLenkeSkalHaTekstenLeggTilbake() {
        wicket.goToPageWith(new TestSvarPanel("id", "fnr", asList(lagSporsmal())));

        Label leggtilbaketekst = wicket.get().component(withId("leggtilbaketekst").and(ofType(Label.class)));
        String labeltekst = (String) leggtilbaketekst.getDefaultModelObject();
        String leggTilbakePropertyTekst = leggtilbaketekst.getString("svarpanel.avbryt.leggtilbake");

        assertThat(labeltekst, is(equalTo(leggTilbakePropertyTekst)));
    }

    @Test
    public void leggTilbakeLenkeSkalHaTekstenAvbryt() {
        wicket.goToPageWith(new TestSvarPanel("id", "fnr", asList(lagSporsmal())));

        Label leggtilbaketekst = wicket.get().component(withId("leggtilbaketekst").and(ofType(Label.class)));
        String labeltekst = (String) leggtilbaketekst.getDefaultModelObject();
        String leggTilbakePropertyTekst = leggtilbaketekst.getString("svarpanel.avbryt.avbryt");

        assertThat(labeltekst, is(equalTo(leggTilbakePropertyTekst)));
    }

    private Henvendelse lagSporsmal() {
        Henvendelse sporsmal = new Henvendelse().withId("id").withOpprettetDato(now());
        sporsmal.temagruppe = Temagruppe.FMLI.name();
        return sporsmal;
    }

    private Henvendelse lagSvar() {
        return new Henvendelse().withOpprettetDato(now()).withType(Henvendelse.Henvendelsetype.SVAR_SKRIFTLIG).withFritekst("fritekst").withTemagruppe(Temagruppe.FMLI.name());
    }

}