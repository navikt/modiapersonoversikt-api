package no.nav.sbl.dialogarena.sak.service;


import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sak.service.interfaces.TilgangskontrollService;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.Cookie;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Feilmelding.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TilgangskontrollServiceTest {

    @Mock
    private EnforcementPoint pep;
    @Mock
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Mock
    private AnsattService ansattService;
    private MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    @InjectMocks
    private TilgangskontrollService tilgangskontrollService = new TilgangskontrollServiceImpl();

    public static final String BRUKERS_IDENT = "12345678901";
    private static final String GODKJENT_ENHET = "0000";
    private static final String ANNEN_ENHET = "1000";

    @Before
    public void setup() throws HentAktoerIdForIdentPersonIkkeFunnet {
        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
        mockRequest.setCookies(lagSaksbehandlerCookie(GODKJENT_ENHET));
        HentAktoerIdForIdentResponse hentAktoerIdForIdentResponse = new HentAktoerIdForIdentResponse();
        hentAktoerIdForIdentResponse.setAktoerId(BRUKERS_IDENT);
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn("0219");
    }

    @Test
    public void markererIkkeJournalforteMedFeil() {
        List<Sakstema> sakstema = asList(
                new Sakstema().withDokumentMetadata(asList(new DokumentMetadata().withIsJournalfort(false)))
        );
        tilgangskontrollService.markerIkkeJournalforte(sakstema);

        assertThat(sakstema.get(0).dokumentMetadata.get(0).getFeilWrapper().getInneholderFeil(), is(true));
        assertThat(sakstema.get(0).dokumentMetadata.get(0).getFeilWrapper().getFeilmelding(), is(IKKE_JOURNALFORT_ELLER_ANNEN_BRUKER));
    }

    @Test
    public void ikkeMarkererJournalforteMedFeil() {
        List<Sakstema> sakstema = asList(
                new Sakstema().withDokumentMetadata(asList(new DokumentMetadata().withIsJournalfort(true)))
        );
        tilgangskontrollService.markerIkkeJournalforte(sakstema);

        assertThat(sakstema.get(0).dokumentMetadata.get(0).getFeilWrapper().getInneholderFeil(), is(false));
    }

    @Test
    public void returnererFeilmeldingSaksbehandlerHarValgtGodkjentEnhet() {
        when(ansattService.hentEnhetsliste()).thenReturn(mockEnhetsListe());

        boolean harGodkjentEnhet = tilgangskontrollService.harGodkjentEnhet(mockRequest);

        assertThat(harGodkjentEnhet, is(true));
    }

    @Test
    public void returnererResponseMedFeilmeldingOmSaksbehandlerHarValgtGodkjentEnhet() {
        when(ansattService.hentEnhetsliste()).thenReturn(mockEnhetsListe());
        mockRequest.setCookies(lagSaksbehandlerCookie(ANNEN_ENHET));

        boolean harGodkjentEnhet = tilgangskontrollService.harGodkjentEnhet(mockRequest);

        assertThat(harGodkjentEnhet, is(false));
    }

    private Cookie[] lagSaksbehandlerCookie(String valgtEnhet) {
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie("saksbehandlerinnstillinger-null", valgtEnhet);
        return cookies;
    }

    private List<AnsattEnhet> mockEnhetsListe() {
        return asList(
                new AnsattEnhet(GODKJENT_ENHET, "ansattenhet-1"),
                new AnsattEnhet("0001", "ansattenhet-2")
        );
    }


}
