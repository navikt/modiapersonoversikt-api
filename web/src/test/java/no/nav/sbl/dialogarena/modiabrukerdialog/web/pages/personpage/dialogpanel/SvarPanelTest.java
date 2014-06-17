package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;


import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.HenvendelseMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.KjerneinfoPepMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.WicketPageTest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSSendHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.sendhenvendelse.SendHenvendelsePortType;
import org.apache.wicket.markup.html.basic.Label;
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

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withModelObject;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {KjerneinfoPepMockContext.class, HenvendelseMockContext.class})
public class SvarPanelTest extends WicketPageTest {

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

        verify(ws).sendHenvendelse(captor.capture());
        assertThat(captor.getValue().getType(), is(SVAR.name()));
    }

    @Test
    public void tekstligSvarErValgtSomDefault() {
        wicket.goToPageWith(new SvarPanel("id", "fnr", "meldingsId"))
                .should().containComponent(withId("kanal").and(withModelObject(is(SvarKanal.TEKST))));
    }
}