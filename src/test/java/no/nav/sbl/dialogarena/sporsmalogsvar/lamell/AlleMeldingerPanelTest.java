package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.test.matcher.BehaviorMatchers;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createMelding;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = {ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class AlleMeldingerPanelTest extends WicketPageTest {

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

    @Before
    public void setUp() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("id1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "id1"),
                createMelding("id2", SPORSMAL_SKRIFTLIG, now().minusDays(2), "TEMA", "id2")));
        when(henvendelseBehandlingService.hentInnboks(anyString())).thenAnswer(new Answer<InnboksVM>(){
            @Override
            public InnboksVM answer(InvocationOnMock invocation) throws Throwable {
                return new InnboksVM(((String) invocation.getArguments()[0]), henvendelseBehandlingService);
            }
        });
    }

    @Test
    public void starterAlleMeldingerPanelUtenFeil() {
        wicket.goToPageWith(new TestAlleMeldingerPanel("id", henvendelseBehandlingService.hentInnboks("fnr"), ""));
    }

    @Test
    public void setterValgtMeldingDersomManTrykkerPaaDen() {
        InnboksVM innboksVM = henvendelseBehandlingService.hentInnboks("fnr");
        innboksVM.setValgtMelding("id1");

        wicket.goToPageWith(new TestAlleMeldingerPanel("id", innboksVM, ""))
                .executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxEventBehavior.class));

        assertThat(innboksVM.getValgtTraad().getNyesteMelding().melding.id, is("id2"));
    }
}
