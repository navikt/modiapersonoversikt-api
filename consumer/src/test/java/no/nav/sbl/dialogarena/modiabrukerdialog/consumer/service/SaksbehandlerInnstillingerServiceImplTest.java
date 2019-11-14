package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.brukerdialog.security.context.StaticSubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.servlet.http.Cookie;

import static java.lang.System.setProperty;
import static java.util.Arrays.asList;
import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SaksbehandlerInnstillingerServiceImplTest {

    @Mock
    private AnsattService ansattService;

    @InjectMocks
    private SaksbehandlerInnstillingerServiceImpl saksbehandlerInnstillingerService;

    private WicketTester tester = new WicketTester();

    @BeforeAll
    public static void setUpStatic() {
        setProperty(StaticSubjectHandler.SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
    }

    @BeforeEach
    public void setUp() {
        initMocks(this);
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
        assertThat(tester.getResponse().getCookies().get(0).getMaxAge(), is(equalTo(3600 * 12)));
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