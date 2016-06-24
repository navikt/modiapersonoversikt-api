package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgavewrapper;

import no.nav.modig.lang.option.Optional;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.GsakKodeTema;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
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
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.createMelding;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper.NyOppgaveFormWrapper.GYLDIG_ENHET;
import static org.apache.wicket.util.tester.WicketTesterHelper.findBehavior;
import static org.hamcrest.Matchers.is;
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
        when(gsakService.hentForeslatteEnheter(anyString(), anyString(), anyString(), any(Optional.class))).thenReturn(asList(createEnhet("1231", "Sinsen")));

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
    public void enheterMedEnhetsIdUnder100FiltreresBort(){
        List<AnsattEnhet> enheter = new ArrayList<>();
        enheter.add(createEnhet( "1", "en"));
        enheter.add(createEnhet("99", "nittiNi"));
        enheter.add(createEnhet("100", "hundre"));
        enheter.add(createEnhet("200", "toHundre"));
        List<AnsattEnhet> gyldigeEnheter = on(enheter).filter(GYLDIG_ENHET).collect();

        Assert.assertThat(gyldigeEnheter.size(), is(2));
    }



    @Test
    public void enheterMedNavnSomInneholderTekstenAvikletFiltreresBort(){
        List<AnsattEnhet> enheter = new ArrayList<>();
        enheter.add(createEnhet("111", "avviklet kontor"));
        enheter.add(createEnhet("222", "kontor2 (avviklet)"));
        enheter.add(createEnhet("333", "kontor3"));
        List<AnsattEnhet> gyldigeEnheter = on(enheter).filter(GYLDIG_ENHET).collect();

        Assert.assertThat(gyldigeEnheter.size(), is(1));
    }

    @Test
    public void enheterMedIngenTilknyttedeAnsatteFiltreresBort(){
        List<AnsattEnhet> enheter = new ArrayList<>();
        enheter.add(createEnhet("707", "enmanns kontor", 1));
        enheter.add(createEnhet("101", "lite kontor", 5));
        enheter.add(createEnhet("202", "fiktivt kontor 1", 0));
        enheter.add(createEnhet("303", "mellomstort kontor", 20));
        enheter.add(createEnhet("404", "fiktivt kontor 2", 0));
        enheter.add(createEnhet("505", "fiktivt kontor 3", 0));
        enheter.add(createEnhet("606", "stort kontor", 100));
        List<AnsattEnhet> gyldigeEnheter = on(enheter).filter(GYLDIG_ENHET).collect();

        Assert.assertThat(gyldigeEnheter.size(), is(4));
    }

    private AnsattEnhet createEnhet(String enhetId, String enhetNavn, int antallRessurser) {
        return new AnsattEnhet(enhetId, enhetNavn, antallRessurser);
    }

    private AnsattEnhet createEnhet(String enhetId, String enhetNavn) {
        return createEnhet(enhetId, enhetNavn, 25);
    }

}
