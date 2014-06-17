package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.HenvendelseMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.KjerneinfoPepMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.WicketPageTest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSSendHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.sendhenvendelse.SendHenvendelsePortType;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {KjerneinfoPepMockContext.class, HenvendelseMockContext.class})
public class ReferatPanelTest extends WicketPageTest {

    @Inject
    protected SendHenvendelsePortType ws;

    @Captor
    ArgumentCaptor<WSSendHenvendelseRequest> captor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
    }

    @Test
    public void inneholderSporsmaalsspefikkeKomponenter() {
        wicket.goToPageWith(new ReferatPanel("id", "fnr"))
                .should().containComponent(withId("kanal").and(ofType(RadioGroup.class)));
    }

    @Test
    public void skalSendeReferattypeTilHenvendelse() {
        wicket.goToPageWith(new ReferatPanel("id", "fnr"))
                .inForm(withId("dialogform"))
                .write("tekstfelt:text", "dette er en fritekst")
                .select("tema", 0)
                .select("kanal", 0)
                .submitWithAjaxButton(withId("send"));

        verify(ws).sendHenvendelse(captor.capture());
        assertThat(captor.getValue().getType(), is(REFERAT.name()));
    }
}