package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel;


import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.wicket.test.matcher.BehaviorMatchers;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Svar;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SakService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SakServiceMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.LeggTilbakePanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.SvarKanal;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.TidligereMeldingPanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsInvisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withModelObject;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withTextSaying;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SakServiceMockContext.class})
public class SvarPanelTest extends WicketPageTest {

    @Inject
    protected SakService sakService;

    @Before
    public void init() {
        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
    }

    @Test
    public void inneholderSporsmaalsspefikkeKomponenter() {
        wicket.goToPageWith(new TestSvarPanel("id", "fnr", lagSporsmal()))
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
    public void skalSendeSporsmaalstypeTilHenvendelse() {
        wicket.goToPageWith(new TestSvarPanel("id", "fnr", lagSporsmal()))
                .inForm(withId("svarform"))
                .write("tekstfelt:text", "dette er en fritekst")
                .submitWithAjaxButton(withId("send"));

        verify(sakService).sendSvar(any(Svar.class));
    }

    @Test
    public void girFeedbackOmPaakrevdeKomponenter() {
        TestSvarPanel svarPanel = new TestSvarPanel("id", "fnr", lagSporsmal());
        wicket.goToPageWith(svarPanel)
                .inForm(withId("svarform"))
                .submitWithAjaxButton(withId("send"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages.isEmpty(), is(false));
    }

    @Test
    public void tekstligSvarErValgtSomDefault() {
        wicket.goToPageWith(new TestSvarPanel("id", "fnr", lagSporsmal()))
                .should().containComponent(withId("kanal").and(withModelObject(is(SvarKanal.TEKST))));
    }

    @Test
    public void viserTemagruppenFraSporsmalet() {
        TestSvarPanel svarPanel = new TestSvarPanel("id", "fnr", lagSporsmal());
        wicket.goToPageWith(svarPanel)
                .should().containComponent(withId("temagruppe").and(withTextSaying(svarPanel.getString(Temagruppe.FAMILIE_OG_BARN.name()))));
    }

    @Test
    public void viserKvitteringNaarManSenderInn() {
        wicket.goToPageWith(new TestSvarPanel("id", "fnr", lagSporsmal()))
                .inForm(withId("svarform"))
                .write("tekstfelt:text", "dette er en fritekst")
                .submitWithAjaxButton(withId("send"))
                .should().containComponent(thatIsInvisible().withId("svarform"))
                .should().containComponent(thatIsVisible().ofType(KvitteringsPanel.class));
    }

    @Test
    public void skalViseTraadToggleLenkeHvisSvarFinnes() {
        wicket.goToPageWith(new TestSvarPanel("id", "fnr", lagSporsmal(), lagSvar()))
                .should().containComponent(thatIsVisible().and(withId("vistraadcontainer")));
    }

    @Test
    public void skalIkkeViseTraadToggleLenkeHvisIngenSvarFinnes() {
        wicket.goToPageWith(new TestSvarPanel("id", "fnr", lagSporsmal(), Collections.<Svar>emptyList()))
                .should().containComponent(thatIsInvisible().and(withId("vistraadcontainer")));
    }

    @Test
    public void skalToggleVisningAvTraad() {
        wicket.goToPageWith(new TestSvarPanel("id", "fnr", lagSporsmal(), lagSvar()))
                .should().containComponent(thatIsInvisible().and(withId("traadcontainer")))
                .onComponent(withId("vistraadcontainer")).executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxEventBehavior.class))
                .should().containComponent(thatIsVisible().and(withId("traadcontainer")));
    }

    @Test
    public void skalViseLeggTilbakePanel() {
        wicket.goToPageWith(new TestSvarPanel("id", "fnr", lagSporsmal(), Collections.<Svar>emptyList()))
                .should().containComponent(thatIsInvisible().and(withId("leggtilbakepanel")))
                .click().link(withId("leggtilbake"))
                .should().containComponent(thatIsVisible().and(withId("leggtilbakepanel")));
    }

    @Test
    public void leggTilbakeLenkeSkalHaTekstenLeggTilbake() {
        wicket.goToPageWith(new TestSvarPanel("id", "fnr", lagSporsmal(), Collections.<Svar>emptyList()));

        Label leggtilbaketekst = wicket.get().component(withId("leggtilbaketekst").and(ofType(Label.class)));
        String labeltekst = (String) leggtilbaketekst.getDefaultModelObject();
        String leggTilbakePropertyTekst = leggtilbaketekst.getString("svarpanel.avbryt.leggtilbake");

        assertThat(labeltekst, is(equalTo(leggTilbakePropertyTekst)));
    }

    @Test
    public void leggTilbakeLenkeSkalHaTekstenAvbryt() {
        wicket.goToPageWith(new TestSvarPanel("id", "fnr", lagSporsmal(), lagSvar()));

        Label leggtilbaketekst = wicket.get().component(withId("leggtilbaketekst").and(ofType(Label.class)));
        String labeltekst = (String) leggtilbaketekst.getDefaultModelObject();
        String leggTilbakePropertyTekst = leggtilbaketekst.getString("svarpanel.avbryt.avbryt");

        assertThat(labeltekst, is(equalTo(leggTilbakePropertyTekst)));
    }

    private Sporsmal lagSporsmal() {
        Sporsmal sporsmal = new Sporsmal("id", now());
        sporsmal.temagruppe = Temagruppe.FAMILIE_OG_BARN.name();
        return sporsmal;
    }

    private List<Svar> lagSvar() {
        return asList(new Svar().withOpprettetDato(now()).withFritekst("fritekst").withTemagruppe(Temagruppe.HJELPEMIDLER.name()));
    }
}