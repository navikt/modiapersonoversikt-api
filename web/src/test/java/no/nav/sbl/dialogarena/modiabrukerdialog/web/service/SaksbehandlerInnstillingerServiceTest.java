package no.nav.sbl.dialogarena.modiabrukerdialog.web.service;

import no.nav.modig.core.context.StaticSubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.AnsattService;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.Cookie;

import static java.lang.System.setProperty;
import static java.util.Arrays.asList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SaksbehandlerInnstillingerServiceTest  {

    @Mock
    private AnsattService ansattService;

    @InjectMocks
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    private WicketTester tester = new WicketTester();

    @BeforeClass
    public static void setUpStatic() {
        setProperty(StaticSubjectHandler.SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
    }

    @Before
    public void setUp() {
        when(ansattService.hentEnhetsliste()).thenReturn(asList(
                new AnsattEnhet("1111", "Enhet1"),
                new AnsattEnhet("2222", "Enhet2")));
    }

    @Test
    public void getSaksbehandlerValgtEnhetHenterForsteEnhetFraTjenestenOmCookieIkkeFinnes() {
        assertThat(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet(), is(equalTo("1111")));
    }

    @Test
    public void getSaksbehandlerValgtEnhetHenterEnhetFraCookieOmDenFinnesOgBrukerErTilknyttet() {
        tester.getRequest().addCookie(new Cookie("saksbehandlerinnstillinger-" + getSubjectHandler().getUid(), "2222"));

        assertThat(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet(), is(equalTo("2222")));
    }

    @Test
    public void getSaksbehandlerValgtEnhetHenterIkkeEnheterFraCookieSomBrukerIkkeErTilknyttet() {
        tester.getRequest().addCookie(new Cookie("saksbehandlerinnstillinger-" + getSubjectHandler().getUid(), "3333"));

        assertThat(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet(), is(equalTo("1111")));
    }

    @Test
    public void setSaksbehandlerValgtEnhetCookieHarLevetidPaaEttAar() {
        saksbehandlerInnstillingerService.setSaksbehandlerValgtEnhetCookie("1111");

        assertThat(tester.getResponse().getCookies().get(0).getName(), is(equalTo("saksbehandlerinnstillinger-" + getSubjectHandler().getUid())));
        assertThat(tester.getResponse().getCookies().get(0).getMaxAge(), is(equalTo(3600 * 24 * 365)));
    }

    @Test
    public void setSaksbehandlerValgtEnhetCookieLagerTimeoutCookieMedLevetidPaaTolvTimer() {
        saksbehandlerInnstillingerService.setSaksbehandlerValgtEnhetCookie("1111");

        assertThat(tester.getResponse().getCookies().get(1).getName(), is(equalTo("saksbehandlerinnstillinger-timeout-" + getSubjectHandler().getUid())));
        assertThat(tester.getResponse().getCookies().get(1).getMaxAge(), is(equalTo(3600 * 12)));
    }

    @Test
    public void saksbehandlerInnstillingerErUtdatertHvisTimeoutCookieIkkeFinnes() {
        assertTrue(saksbehandlerInnstillingerService.saksbehandlerInnstillingerErUtdatert());
    }

    @Test
    public void saksbehandlerInnstillingerErIkkeUtdatertHvisTimeoutCookieFinnes() {
        tester.getRequest().addCookie(new Cookie("saksbehandlerinnstillinger-timeout-" + getSubjectHandler().getUid(), ""));

        assertFalse(saksbehandlerInnstillingerService.saksbehandlerInnstillingerErUtdatert());
    }
}