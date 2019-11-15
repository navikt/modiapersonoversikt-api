package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;


import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.common.auth.SsoToken;
import no.nav.common.auth.Subject;
import no.nav.common.auth.SubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Sakstema;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.TjenesteResultatWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.TilgangskontrollService;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.Cookie;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Feilmelding.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TilgangskontrollServiceTest {
    private static final Subject TEST_SUBJECT = new Subject("null", IdentType.InternBruker, SsoToken.oidcToken("token", emptyMap()));

    @Mock
    private AnsattService ansattService;

    private MockHttpServletRequest mockRequest = new MockHttpServletRequest();

    @InjectMocks
    private TilgangskontrollService tilgangskontrollService = new TilgangskontrollServiceImpl();

    private static final String BRUKERS_IDENT = "11111111111";
    private static final String GODKJENT_ENHET = "0000";
    private static final String ANNEN_ENHET = "1000";
    private final static String TEMAKODE = "DAG";

    @Before
    public void setup() {
        mockRequest.setCookies(lagSaksbehandlerCookie(GODKJENT_ENHET));
        HentAktoerIdForIdentResponse hentAktoerIdForIdentResponse = new HentAktoerIdForIdentResponse();
        hentAktoerIdForIdentResponse.setAktoerId(BRUKERS_IDENT);
    }

    @Test
    public void markererIkkeJournalforteMedFeil() {
        List<Sakstema> sakstema = asList(
                new Sakstema().withDokumentMetadata(asList(new DokumentMetadata().withIsJournalfort(false)))
        );
        tilgangskontrollService.markerIkkeJournalforte(sakstema);

        assertThat(sakstema.get(0).dokumentMetadata.get(0).getFeilWrapper().getInneholderFeil(), is(true));
        assertThat(sakstema.get(0).dokumentMetadata.get(0).getFeilWrapper().getFeilmelding(), is(IKKE_JOURNALFORT));
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

        boolean harGodkjentEnhet = SubjectHandler.withSubject(TEST_SUBJECT, () -> tilgangskontrollService.harGodkjentEnhet(mockRequest));

        assertThat(harGodkjentEnhet, is(true));
    }

    @Test
    public void returnererResponseMedFeilmeldingOmSaksbehandlerHarValgtGodkjentEnhet() {
        when(ansattService.hentEnhetsliste()).thenReturn(mockEnhetsListe());
        mockRequest.setCookies(lagSaksbehandlerCookie(ANNEN_ENHET));

        boolean harGodkjentEnhet = SubjectHandler.withSubject(TEST_SUBJECT, () -> tilgangskontrollService.harGodkjentEnhet(mockRequest));

        assertThat(harGodkjentEnhet, is(false));
    }

    @Test
    public void saksbehandlerHarTilgangTilDokumentOk() {
        DokumentMetadata journalpostMetadata = new DokumentMetadata().withTemakode(TEMAKODE);
        TjenesteResultatWrapper result = tilgangskontrollService.harSaksbehandlerTilgangTilDokument(mockRequest, journalpostMetadata, BRUKERS_IDENT, TEMAKODE);


        assertThat(result.result.isPresent(), is(TRUE));
        assertThat(result.result.get(), is(TRUE));
    }

    @Test
    public void temaBidragGirFeilmelding() {
        DokumentMetadata journalpostMetadata = new DokumentMetadata().withTemakode("BID");
        TjenesteResultatWrapper result = tilgangskontrollService.harSaksbehandlerTilgangTilDokument(mockRequest, journalpostMetadata, BRUKERS_IDENT, TEMAKODE);


        assertThat(result.result.isPresent(), is(FALSE));
        assertThat(result.feilmelding, is(TEMAKODE_ER_BIDRAG));
    }

    @Test
    public void journalfortAnnetTema() {
        DokumentMetadata journalpostMetadata = new DokumentMetadata().withTemakode("FOR");
        TjenesteResultatWrapper result = tilgangskontrollService.harSaksbehandlerTilgangTilDokument(mockRequest, journalpostMetadata, BRUKERS_IDENT, TEMAKODE);


        assertThat(result.result.isPresent(), is(FALSE));
        assertThat(result.feilmelding, is(JOURNALFORT_ANNET_TEMA));
        assertThat(result.ekstraFeilInfo.size(), is(2));
    }

    @Test
    public void journalfortAnnenBruker() {
        DokumentMetadata journalpostMetadata = new DokumentMetadata().withTemakode(TEMAKODE).withIsJournalfort(false);
        TjenesteResultatWrapper result = tilgangskontrollService.harSaksbehandlerTilgangTilDokument(mockRequest, journalpostMetadata, BRUKERS_IDENT, TEMAKODE);


        assertThat(result.result.isPresent(), is(FALSE));
        assertThat(result.feilmelding, is(IKKE_JOURNALFORT));
        assertThat(result.ekstraFeilInfo.size(), is(1));
    }

    @Test
    public void inneholderFeil() {
        DokumentMetadata journalpostMetadata = new DokumentMetadata().withTemakode(TEMAKODE).withFeilWrapper(UKJENT_FEIL);
        TjenesteResultatWrapper result = tilgangskontrollService.harSaksbehandlerTilgangTilDokument(mockRequest, journalpostMetadata, BRUKERS_IDENT, TEMAKODE);


        assertThat(result.result.isPresent(), is(FALSE));
        assertThat(result.feilmelding, is(UKJENT_FEIL));
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
