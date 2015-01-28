package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgavewrapper;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GsakKodeTema;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.GsakKodeverk;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.NyOppgave;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper.NyOppgaveFormWrapper;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.behavior.Behavior;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsInvisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createMelding;
import static org.apache.wicket.util.tester.WicketTesterHelper.findBehavior;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@ContextConfiguration(classes = {ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class NyOppgaveFormWrapperTest extends WicketPageTest {

    public static final List<GsakKodeTema.Tema> TEMALISTE_MOCK =
            new ArrayList<>(Arrays.asList(new GsakKodeTema.Tema("kode", "tekst",
                    new ArrayList<>(Arrays.asList(new GsakKodeTema.OppgaveType("oppgKode", "oppgTekst", 1))),
                    new ArrayList<>(Arrays.asList(new GsakKodeTema.Prioritet("priKode", "priTekst"))),
                    new ArrayList<>(Arrays.asList(new GsakKodeTema.Underkategori("underkategoriKode", "underkategoriTekst"))))));

    @Inject
    private GsakService gsakService;
    @Inject
    private GsakKodeverk gsakKodeverk;

    private InnboksVM innboksVM;

    @Before
    public void setUp() {
        initMocks(this);
        innboksVM = mock(InnboksVM.class);
        Melding melding = createMelding("id", SPORSMAL_SKRIFTLIG, now(), "temagruppe", "id");
        when(innboksVM.getValgtTraad()).thenReturn(new TraadVM(asList(new MeldingVM(melding, 1))));
        when(gsakKodeverk.hentTemaListe()).thenReturn(TEMALISTE_MOCK);
    }

    @Test
    public void girFeilmeldingHvisIkkeAlleFeltUtfyltt() {
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
    @SuppressWarnings("unchecked")
    public void oppretterOppgave() {
        when(gsakService.hentForeslattEnhet(anyString(), anyString(), anyString(), any(Optional.class))).thenReturn(optional(new AnsattEnhet("1231", "Sinsen")));

        wicket.goToPageWith(new NyOppgaveFormWrapper("panel", innboksVM));

        Behavior temaBehavior = findBehavior(wicket.get().component(withId("tema")), AjaxFormComponentUpdatingBehavior.class);
        Behavior typeBehavior = findBehavior(wicket.get().component(withId("type")), AjaxFormComponentUpdatingBehavior.class);

        wicket.inForm("panel:nyoppgaveform")
                .select("tema", 0);
        wicket.tester.executeBehavior((AbstractAjaxBehavior) temaBehavior);

        wicket.inForm("panel:nyoppgaveform")
                .select("typeContainer:type", 0);
        wicket.tester.executeBehavior((AbstractAjaxBehavior) typeBehavior);

        wicket.tester.getRequest().setParameter("enhetContainer:enhet", "1231");

        wicket.inForm("panel:nyoppgaveform")
                .select("prioritetContainer:prioritet", 0)
                .write("beskrivelse", "Beskrivelse")
                .submitWithAjaxButton(withId("opprettoppgave"));

        verify(gsakService).opprettGsakOppgave(any(NyOppgave.class));
    }

}
