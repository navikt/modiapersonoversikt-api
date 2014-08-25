package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel;

import no.nav.modig.core.context.ModigSecurityConstants;
import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.core.context.SubjectHandlerUtils;
import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.core.domain.IdentType;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SaksbehandlerInnstillingerPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.SaksbehandlerInnstillingerService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.LeggTilbakeVM.LINJESKILLER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {SaksbehandlerInnstillingerPanelMockContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class LeggTilbakeVMTest {

    private static final String NAVIDENT = "navident";
    private static final String ENHET = "enhet";

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    @InjectMocks
    private LeggTilbakeVM leggTilbakeVM;

    @Before
    public void setUp() {
        leggTilbakeVM = new LeggTilbakeVM(saksbehandlerInnstillingerService);
        innloggetBrukerEr(NAVIDENT);
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn(ENHET);
    }

    @Test
    public void skalGiRiktigBeskrivelse() {
        String beskrivelseStart = "arsak";
        DateTime date = DateTime.now();

        String beskrivelse = leggTilbakeVM.lagBeskrivelse(beskrivelseStart, date);

        assertThat(beskrivelse, is("- " + LeggTilbakeVM.formaterTimestamp(date) + " (" + NAVIDENT + ", " + ENHET + ") -" + LINJESKILLER + beskrivelseStart));
    }

    public static void innloggetBrukerEr(String userId) {
        System.setProperty(SubjectHandler.SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getCanonicalName());
        System.setProperty(ModigSecurityConstants.SYSTEMUSER_USERNAME, "srvHenvendelse");
        SubjectHandlerUtils.setSubject(new SubjectHandlerUtils.SubjectBuilder(userId, IdentType.EksternBruker).withAuthLevel(4).getSubject());
    }

}
