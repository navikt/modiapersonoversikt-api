package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import org.apache.wicket.Component;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.*;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createMelding;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = {ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class TraaddetaljerPanelTest extends WicketPageTest {

    private static final String BESVAR_ID = "besvar";

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

    @Before
    public void setup() {
        List<IStringResourceLoader> stringResourceLoaders = wicket.tester.getApplication().getResourceSettings().getStringResourceLoaders();
        stringResourceLoaders.clear();
        stringResourceLoaders.add(new IStringResourceLoader() {
            @Override
            public String loadStringResource(Class<?> clazz, String key, Locale locale, String style, String variation) {
                return "";
            }

            @Override
            public String loadStringResource(Component component, String key, Locale locale, String style, String variation) {
                return "";
            }
        });
    }

    @Test
    public void skalKunneBesvareTraadInitiertAvBruker() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1")));

        wicket.goToPageWith(new TraaddetaljerPanel("id", new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsVisible().and(withId(BESVAR_ID)));
    }

    @Test
    public void skalIkkeKunneBesvareTraadInitiertAvSaksbehandler() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SAMTALEREFERAT_OPPMOTE, now().minusDays(1), "TEMA", "melding1")));

        wicket.goToPageWith(new TraaddetaljerPanel("id", new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsInvisible().and(withId(BESVAR_ID)));
    }

    @Test
    public void skalKunneBesvareTraadInitiertAvBrukerMedTidligereSvar() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1"),
                createMelding("melding2", SVAR_SKRIFTLIG, now(), "TEMA", "melding1")));

        wicket.goToPageWith(new TraaddetaljerPanel("id", new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsVisible().and(withId(BESVAR_ID)));
    }

    @Test
    public void skalKunneBesvareTraadSomErMarkertSomKontorsperret() {
        Melding melding = createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1");
        melding.kontorsperretEnhet = "kontorsperretEnhet";
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                melding));

        wicket.goToPageWith(new TraaddetaljerPanel("id", new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsVisible().and(withId(BESVAR_ID)));
    }

}