package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgavewrapper;

import no.nav.modig.wicket.test.matcher.BehaviorMatchers;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.NyOppgave;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper.NyOppgaveFormWrapper;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsInvisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createMelding;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@ContextConfiguration(classes = {ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class NyOppgaveFormWrapperTest extends WicketPageTest {

    @Captor
    private ArgumentCaptor<NyOppgave> nyOppgaveArgumentCaptor;

    @Inject
    private GsakService gsakService;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void skalGiFeilmeldingHvisManSenderInnSkjemaUtenAAFylleInnAlleFeltene() {
        NyOppgaveFormWrapper nyOppgaveWrapper = new NyOppgaveFormWrapper("panel", mock(InnboksVM.class));
        wicket.goToPageWith(nyOppgaveWrapper)
                .inForm("panel:nyoppgaveform")
                .submit()
                .should().containComponent(thatIsInvisible().withId("enhetContainer"))
                .should().containComponent(thatIsInvisible().withId("typeContainer"))
                .should().containComponent(thatIsInvisible().withId("prioritetContainer"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertTrue(errorMessages.contains(nyOppgaveWrapper.getString("nyoppgaveform.tema.Required")));
        assertTrue(errorMessages.contains(nyOppgaveWrapper.getString("nyoppgaveform.beskrivelse.Required")));
    }

    @Test
    public void skalSendeNyOppgaveObjektetTilGsakTjenestenForAaOppretteNy() {
        InnboksVM innboksVM = mock(InnboksVM.class);
        Melding melding = createMelding("id", SPORSMAL_SKRIFTLIG, now(), "temagruppe", "id");
        when(innboksVM.getValgtTraad()).thenReturn(new TraadVM(asList(new MeldingVM(melding, 1))));

        String beskrivelse = "Dette er en beskrivelse";
        NyOppgaveFormWrapper nyOppgaveFormWrapper = new NyOppgaveFormWrapper("panel", innboksVM);
        wicket.goToPageWith(nyOppgaveFormWrapper)
                .inForm("panel:nyoppgaveform")
                .select("tema", 0).andReturn()
                .executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxFormComponentUpdatingBehavior.class))
                .inForm("panel:nyoppgaveform")
                .select("enhetContainer:enhet", 0)
                .select("typeContainer:type", 0)
                .select("prioritetContainer:prioritet", 0)
                .write("beskrivelse", beskrivelse)
                .submitWithAjaxButton(withId("opprettoppgave"));

        verify(gsakService).opprettGsakOppgave(nyOppgaveArgumentCaptor.capture());
        NyOppgave nyOppgave = nyOppgaveArgumentCaptor.getValue();
        assertThat(nyOppgave.beskrivelse, is(beskrivelse));
        assertThat(nyOppgave.henvendelseId, is(melding.id));
    }
}
