package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.unleash;

import no.finn.unleash.UnleashContext;
import no.finn.unleash.UnleashContextProvider;
import no.nav.brukerdialog.security.context.SubjectHandler;
import no.nav.brukerdialog.security.context.ThreadLocalSubjectHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UnleashContextProviderImplTest {

    public static final String SESSION_ID = "1234abc";
    public static final String IDENT = "Z999999";
    public static final String REMOTE_ADDR = "123.123.123.123";
    public static final String VALGT_ENHET = "0118";
    public static final String ENHET_COOKIE_PREFIX = "saksbehandlerinnstillinger-";
    public static final String NULL_IDENT_GRUNNET_MOCK_PROBLEM = "null";
    public static final String JENKINS_IDENT = "z123456";

    private SubjectHandler subjectHandler;
    private ServletRequestAttributes requestAttributes;
    private MockHttpServletRequest request;
    private UnleashContextProvider contextProvider;
    private HttpSession session;

    @BeforeEach
    void init() {
        System.setProperty("no.nav.brukerdialog.security.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
        subjectHandler = mock(SubjectHandler.class);

        request = new MockHttpServletRequest();
        session = mock(HttpSession.class);
        request.setSession(session);
        request.setRemoteAddr(REMOTE_ADDR);
        request.setCookies(new Cookie(ENHET_COOKIE_PREFIX + NULL_IDENT_GRUNNET_MOCK_PROBLEM, VALGT_ENHET), new Cookie(ENHET_COOKIE_PREFIX + JENKINS_IDENT, VALGT_ENHET));
        requestAttributes = new ServletRequestAttributes(request);

        RequestContextHolder.setRequestAttributes(requestAttributes);

        contextProvider = new UnleashContextProviderImpl(subjectHandler);
    }

    @Test
    void getContextPopulatesAllFieldsCorrectly() {
        when(subjectHandler.getUid()).thenReturn(IDENT);
        when(session.getId()).thenReturn(SESSION_ID);

        UnleashContext context = contextProvider.getContext();

        assertThat(context.getUserId().get(), is(IDENT));
        assertThat(context.getSessionId().get(), is(SESSION_ID));
        assertThat(context.getRemoteAddress().get(), is(REMOTE_ADDR));
        assertThat(context.getProperties().size(), is(1));
        assertThat(context.getProperties().get("valgtEnhet"), is(VALGT_ENHET));
    }
}