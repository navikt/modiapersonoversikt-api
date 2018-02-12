package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgavewrapper;

import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.GsakKodeTema;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.MockServiceTestContext;
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
import org.junit.Assert;
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
import static java.util.Collections.singletonList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.createMelding;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper.NyOppgaveFormWrapper.erGyldigEnhet;
import static org.apache.wicket.util.tester.WicketTesterHelper.findBehavior;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@ContextConfiguration(classes = {MockServiceTestContext.class})
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
    @Inject
    private ArbeidsfordelingV1Service arbeidsfordeling;

    @Inject
    private EnforcementPoint pep;
    private InnboksVM innboksVM;

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    @Before
    public void setUp() {
        initMocks(this);
        innboksVM = mock(InnboksVM.class);
        Melding melding = createMelding("id", SPORSMAL_SKRIFTLIG, now(), Temagruppe.ARBD, "id");
        when(innboksVM.getValgtTraad()).thenReturn(new TraadVM(asList(new MeldingVM(melding, 1)), pep, saksbehandlerInnstillingerService));
        when(gsakKodeverk.hentTemaListe()).thenReturn(TEMALISTE_MOCK);
    }

    @Test
    public void girFeilmeldingHvisIkkeAlleFeltUtfyltt() {
        NyOppgaveFormWrapper nyOppgaveWrapper = new NyOppgaveFormWrapper("panel", mock(InnboksVM.class));
        wicket.goToPageWith(nyOppgaveWrapper)
                .inForm("panel:nyoppgaveform")
                .submit()
                .should().containComponent(withId("enhetContainer"))
                .should().containComponent(withId("typeContainer"))
                .should().containComponent(withId("prioritetContainer"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertTrue(errorMessages.contains(nyOppgaveWrapper.getString("nyoppgaveform.tema.Required")));
        assertTrue(errorMessages.contains(nyOppgaveWrapper.getString("nyoppgaveform.beskrivelse.Required")));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void oppretterOppgave() {
        when(arbeidsfordeling.finnBehandlendeEnhetListe(anyString(), anyString(), anyString(), anyString())).thenReturn(singletonList(createEnhet("1231", "Sinsen")));

        wicket.goToPageWith(new NyOppgaveFormWrapper("panel", innboksVM));

        Behavior temaBehavior = findBehavior(wicket.get().component(withId("tema")), AjaxFormComponentUpdatingBehavior.class);
        Behavior typeBehavior = findBehavior(wicket.get().component(withId("type")), AjaxFormComponentUpdatingBehavior.class);

        wicket.inForm("panel:nyoppgaveform")
                .select("tema", 0);
        wicket.tester.executeBehavior((AbstractAjaxBehavior) temaBehavior);

        wicket.inForm("panel:nyoppgaveform")
                .select("typeContainer:type", 0);
        wicket.tester.executeBehavior((AbstractAjaxBehavior) typeBehavior);

        wicket.inForm("panel:nyoppgaveform")
                .select("prioritetContainer:prioritet", 0)
                .write("beskrivelse", "Beskrivelse")
                .submitWithAjaxButton(withId("opprettoppgave"));

        verify(gsakService).opprettGsakOppgave(any(NyOppgave.class));
    }

    @Test
    public void enheterMedEnhetsIdUnder100FiltreresBort() {
        List<AnsattEnhet> enheter = new ArrayList<>();
        enheter.add(createEnhet("1", "en"));
        enheter.add(createEnhet("99", "nittiNi"));
        enheter.add(createEnhet("100", "hundre"));
        enheter.add(createEnhet("200", "toHundre"));
        List<AnsattEnhet> gyldigeEnheter = on(enheter).filter(enhet -> erGyldigEnhet(enhet)).collect();

        Assert.assertThat(gyldigeEnheter.size(), is(2));
    }

    @Test
    public void enheterSomIkkeErAktiveFiltreresBort() {
        List<AnsattEnhet> enheter = new ArrayList<>();
        enheter.add(createEnhet("707", "enmanns kontor", "AKTIV"));
        enheter.add(createEnhet("101", "lite kontor", "AKTIV"));
        enheter.add(createEnhet("202", "fiktivt kontor 1", "underEtablering"));
        enheter.add(createEnhet("303", "mellomstort kontor", "AKTIV"));
        enheter.add(createEnhet("404", "fiktivt kontor 2", "underAvvikling"));
        enheter.add(createEnhet("505", "fiktivt kontor 3", "nedlagt"));
        enheter.add(createEnhet("606", "stort kontor", "AKTIV"));
        List<AnsattEnhet> gyldigeEnheter = on(enheter).filter(enhet -> erGyldigEnhet(enhet)).collect();

        Assert.assertThat(gyldigeEnheter.size(), is(4));
    }

    private AnsattEnhet createEnhet(String enhetId, String enhetNavn, String status) {
        return new AnsattEnhet(enhetId, enhetNavn, status);
    }

    private AnsattEnhet createEnhet(String enhetId, String enhetNavn) {
        return createEnhet(enhetId, enhetNavn, "AKTIV");
    }

}
