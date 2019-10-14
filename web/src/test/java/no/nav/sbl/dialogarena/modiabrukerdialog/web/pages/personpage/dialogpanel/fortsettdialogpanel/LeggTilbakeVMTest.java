package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel;

import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.brukerdialog.tools.SecurityConstants;
import no.nav.brukerdialog.security.context.SubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SaksbehandlerInnstillingerPanelMockContext;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = AFTER_CLASS)
@ContextConfiguration(classes = {SaksbehandlerInnstillingerPanelMockContext.class})
public class LeggTilbakeVMTest {

    private static final String NAVIDENT = "navident";
    private static final String ENHET = "enhet";

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    @Before
    public void setUp() {
        innloggetBrukerEr(NAVIDENT);
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn(ENHET);

        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis()); //Setter klokka til en fast tid for testen
    }

    @After
    public void tearDown() {
        DateTimeUtils.setCurrentMillisSystem(); //Frigir klokka igjen slik at andre tester fortsatt vil funke
    }

    @Test
    public void skalGiRiktigBeskrivelse() {
        String beskrivelseStart = "arsak";
        String beskrivelse = new LeggTilbakeVM().lagBeskrivelse(beskrivelseStart);

        assertThat(beskrivelse, is(beskrivelseStart));
    }

    public static void innloggetBrukerEr(String userId) {
//        System.setProperty(SubjectHandler.SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getCanonicalName());
//        System.setProperty(SecurityConstants.SYSTEMUSER_USERNAME, "srvHenvendelse");
//        SubjectHandlerUtils.setSubject(new SubjectHandlerUtils.SubjectBuilder(userId, IdentType.EksternBruker).withAuthLevel(4).getSubject());
    }
}
