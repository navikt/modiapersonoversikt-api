package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel;

import no.nav.modig.core.context.ModigSecurityConstants;
import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.core.context.SubjectHandlerUtils;
import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.core.domain.IdentType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.lang.String.format;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.LeggTilbakeVM.LINJESKILLER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class LeggTilbakeVMTest {

    private static final String NAVIDENT = "navident";
    private static final String ENHET = "enhet";

    @Before
    public void setUp() {
        innloggetBrukerEr(NAVIDENT);

        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis()); //Setter klokka til en fast tid for testen
    }

    @After
    public void tearDown() {
        DateTimeUtils.setCurrentMillisSystem(); //Frigir klokka igjen slik at andre tester fortsatt vil funke
    }

    @Test
    public void skalGiRiktigBeskrivelse() {
        String beskrivelseStart = "arsak";
        String beskrivelse = new LeggTilbakeVM().lagBeskrivelse(beskrivelseStart, ENHET);

        assertThat(beskrivelse, is(format("- %s (%s, %s) -%s%s", LeggTilbakeVM.getFormatertTimestamp(), NAVIDENT, ENHET, LINJESKILLER, beskrivelseStart)));
    }

    public static void innloggetBrukerEr(String userId) {
        System.setProperty(SubjectHandler.SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getCanonicalName());
        System.setProperty(ModigSecurityConstants.SYSTEMUSER_USERNAME, "srvHenvendelse");
        SubjectHandlerUtils.setSubject(new SubjectHandlerUtils.SubjectBuilder(userId, IdentType.EksternBruker).withAuthLevel(4).getSubject());
    }

}
