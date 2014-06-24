package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;


import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Svar;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SakService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.KjerneinfoPepMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SakServiceMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.WicketPageTest;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withModelObject;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withTextSaying;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {KjerneinfoPepMockContext.class, SakServiceMockContext.class})
public class SvarPanelTest extends WicketPageTest {

    @Inject
    protected SakService sakService;

    @Before
    public void init() {
        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
    }

    @Test
    public void inneholderSporsmaalsspefikkeKomponenter() {
        wicket.goToPageWith(new SvarPanel("id", "fnr", new Sporsmal("id", now())))
                .should().containComponent(withId("tema").and(ofType(Label.class)))
                .should().containComponent(withId("sporsmal").and(ofType(URLParsingMultiLineLabel.class)))
                .should().containComponent(withId("dato").and(ofType(Label.class)))
                .should().containComponent(withId("kanal").and(ofType(RadioGroup.class)))
                .should().containComponent(withId("kanalbeskrivelse").and(ofType(Label.class)));
    }

    @Test
    public void skalSendeSporsmaalstypeTilHenvendelse() {
        Sporsmal sporsmal = new Sporsmal("id", now());
        sporsmal.tema = Tema.OVRIGE_HENVENDELSER.name();
        wicket.goToPageWith(new SvarPanel("id", "fnr", sporsmal))
                .inForm(withId("dialogform"))
                .write("tekstfelt:text", "dette er en fritekst")
                .submitWithAjaxButton(withId("send"));

        verify(sakService).sendSvar(any(Svar.class));
    }

    @Test
    public void tekstligSvarErValgtSomDefault() {
        wicket.goToPageWith(new SvarPanel("id", "fnr", new Sporsmal("id", now())))
                .should().containComponent(withId("kanal").and(withModelObject(is(SvarKanal.TEKST))));
    }

    @Test
    public void viserTemaetFraSporsmalet() {
        Sporsmal sporsmal = new Sporsmal("id", now());
        sporsmal.tema = Tema.FAMILIE_OG_BARN.name();

        SvarPanel svarPanel = new SvarPanel("id", "fnr", sporsmal);
        wicket.goToPageWith(svarPanel)
                .should().containComponent(withId("tema").and(withTextSaying(svarPanel.getString(Tema.FAMILIE_OG_BARN.name()))));
    }
}