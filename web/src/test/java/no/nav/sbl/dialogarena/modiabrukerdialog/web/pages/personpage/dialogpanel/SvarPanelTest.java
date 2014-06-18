package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;


import no.nav.modig.core.context.ThreadLocalSubjectHandler;
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
import static org.hamcrest.Matchers.is;
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
        wicket.goToPageWith(new SvarPanel("id", "fnr", "meldingsId"))
                .should().containComponent(withId("sporsmal").and(ofType(Label.class)))
                .should().containComponent(withId("dato").and(ofType(Label.class)))
                .should().containComponent(withId("kanal").and(ofType(RadioGroup.class)))
                .should().containComponent(withId("kanalbeskrivelse").and(ofType(Label.class)));
    }

    @Test
    public void skalSendeSporsmaalstypeTilHenvendelse() {
        wicket.goToPageWith(new SvarPanel("id", "fnr", "meldingsId"))
                .inForm(withId("dialogform"))
                .write("tekstfelt:text", "dette er en fritekst")
                .select("tema", 0)
                .submitWithAjaxButton(withId("send"));

        verify(sakService).sendSvar(any(Svar.class));
    }

    @Test
    public void tekstligSvarErValgtSomDefault() {
        wicket.goToPageWith(new SvarPanel("id", "fnr", "meldingsId"))
                .should().containComponent(withId("kanal").and(withModelObject(is(SvarKanal.TEKST))));
    }

}