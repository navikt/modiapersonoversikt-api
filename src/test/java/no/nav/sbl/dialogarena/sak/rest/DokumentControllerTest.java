package no.nav.sbl.dialogarena.sak.rest;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.sbl.dialogarena.sak.service.InnsynImpl;
import no.nav.sbl.dialogarena.sak.service.interfaces.TilgangskontrollService;
import no.nav.sbl.dialogarena.sak.viewdomain.dokumentvisning.JournalpostResultat;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sak;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.AlleSakerResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.DokumentMetadataResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.TjenesteResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.service.DokumentMetadataService;
import no.nav.sbl.dialogarena.saksoversikt.service.service.SaksService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static no.nav.sbl.dialogarena.sak.rest.DokumentController.TEMAKODE_BIDRAG;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DokumentControllerTest {


    @Mock
    private InnsynImpl innsyn;

    @Mock
    private SaksService saksService;

    @Mock
    private DokumentMetadataService dokumentMetadataService;

    @Mock
    private TilgangskontrollService tilgangskontrollService;

    @InjectMocks
    private DokumentController dokumentController = new DokumentController();

    private final MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
    private final String VALGT_ENHET = "0000";
    private final String TEMA_KODE = "temakode";
    private final String ANNEN_TEMAKODE = "annentemakode";

    private final String FNR = "1234567810";
    private final String JOURNALPOSTID = "123";
    private final String DOKUMENTREFERANSE = "321";

    @Before
    public void setup() {
        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
//        System.setProperty("dokumentressurs.withmock", "true");
        httpServletRequest.setCookies(lagSaksbehandlerCookie(VALGT_ENHET));
        when(tilgangskontrollService.harGodkjentEnhet(any(String.class), any(HttpServletRequest.class))).thenReturn(empty());
        when(saksService.hentAlleSaker(anyString())).thenReturn(new AlleSakerResultatWrapper(asList(new Sak()), null));
        when(innsyn.hentDokument(anyString(), anyString())).thenReturn(new TjenesteResultatWrapper("null"));
    }

    @Test
    public void returnererFeilmeldingStatusOmSaksbehandlerIkkeHarTilgangTilValgtEnhet() {
        when(tilgangskontrollService.harGodkjentEnhet(any(String.class), any(HttpServletRequest.class))).thenReturn(mockIkkeTomHttpServletResponse());

        Response response = dokumentController.hentJournalpostMetadata(FNR, JOURNALPOSTID, TEMA_KODE, httpServletRequest);

        assertThat(response.getStatus(), is(401));
    }

    @Test
    public void returnererFeilmeldingOmSaksbehandlerIkkeHarTilgangTilTema() {
        when(tilgangskontrollService.harSaksbehandlerTilgangTilDokument(any(String.class), any(String.class))).thenReturn(false);
        when(dokumentMetadataService.hentDokumentMetadata(any(List.class), any(String.class))).thenReturn(lagDokumentMetadataListe(TEMA_KODE));

        Response response = dokumentController.hentJournalpostMetadata(FNR, JOURNALPOSTID, TEMA_KODE, httpServletRequest);

        assertThat(((JournalpostResultat) response.getEntity()).getFeilendeDokumenter().get(0).getFeilmeldingEnonicKey(), is("feilmelding.saksbehandlerikketilgang"));
    }

    @Test
    public void returnererDokumentOmSaksbehandlerHarTilgangTilTema() {
        when(dokumentMetadataService.hentDokumentMetadata(any(List.class), any(String.class))).thenReturn(lagDokumentMetadataListe(TEMA_KODE));
        when(tilgangskontrollService.harSaksbehandlerTilgangTilDokument(any(String.class), any(String.class))).thenReturn(true);

        Response response = dokumentController.hentJournalpostMetadata(FNR, JOURNALPOSTID, TEMA_KODE, httpServletRequest);

        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void returnererFeilmeldingOmDokumentErJournalfortPaAnnetTema() {
        when(dokumentMetadataService.hentDokumentMetadata(any(List.class), any(String.class))).thenReturn(lagDokumentMetadataListe(TEMA_KODE));
        when(tilgangskontrollService.harSaksbehandlerTilgangTilDokument(any(String.class), any(String.class))).thenReturn(true);

        Response response = dokumentController.hentJournalpostMetadata(FNR, JOURNALPOSTID, ANNEN_TEMAKODE, httpServletRequest);

        assertThat(((JournalpostResultat) response.getEntity()).getFeilendeDokumenter().get(0).getFeilmeldingEnonicKey(), is("feilmelding.journalfortannettema"));
    }

    @Test
    public void returnererFeilmeldingOmTemaErBidrag() {
        when(dokumentMetadataService.hentDokumentMetadata(any(List.class), any(String.class))).thenReturn(lagDokumentMetadataListe(TEMAKODE_BIDRAG));
        when(tilgangskontrollService.harSaksbehandlerTilgangTilDokument(any(String.class), any(String.class))).thenReturn(true);

        Response response = dokumentController.hentJournalpostMetadata(FNR, JOURNALPOSTID, TEMAKODE_BIDRAG, httpServletRequest);

        assertThat(((JournalpostResultat) response.getEntity()).getFeilendeDokumenter().get(0).getFeilmeldingEnonicKey(), is("feilmelding.temakode.bidrag"));
    }

    @Test
    public void returnerFeilmeldingHvisSaksbehandlerIkkeHarTilgangTilValgtEnhet() throws IOException {
        when(tilgangskontrollService.harGodkjentEnhet(any(String.class), any(HttpServletRequest.class))).thenReturn(mockIkkeTomHttpServletResponse());

        Response response = dokumentController.hentDokument(FNR, JOURNALPOSTID, DOKUMENTREFERANSE, httpServletRequest);

        assertThat(response.getStatus(), is(401));
    }

    @Test
    public void returnererFeilmeldingHvisJournalpostMetadataIkkeFinnesIJoarkPaaBruker() throws IOException {
        when(dokumentMetadataService.hentDokumentMetadata(any(List.class), any(String.class))).thenReturn(lagDokumentMetadataIkkeJournalfortListe(TEMA_KODE));

        Response response = dokumentController.hentDokument(FNR, JOURNALPOSTID, DOKUMENTREFERANSE, httpServletRequest);

        assertThat(response.getStatus(), is(403));
    }

    @Test
    public void returnererFeilmeldingHvisTemaErBidrag() throws IOException {
        when(dokumentMetadataService.hentDokumentMetadata(any(List.class), any(String.class))).thenReturn(lagDokumentMetadataListe(TEMAKODE_BIDRAG));

        Response response = dokumentController.hentDokument(FNR, JOURNALPOSTID, DOKUMENTREFERANSE, httpServletRequest);

        assertThat(response.getStatus(), is(403));
    }


    @Test
    public void returnererFeilmeldingHvisSaksbehandlerIkkeHarTilgangTilTema() throws IOException {
        when(dokumentMetadataService.hentDokumentMetadata(any(List.class), any(String.class))).thenReturn(lagDokumentMetadataListe(TEMA_KODE));
        when(tilgangskontrollService.harSaksbehandlerTilgangTilDokument(any(String.class), any(String.class))).thenReturn(false);

        Response response = dokumentController.hentDokument(FNR, JOURNALPOSTID, DOKUMENTREFERANSE, httpServletRequest);

        assertThat(response.getStatus(), is(403));
    }

    private DokumentMetadataResultatWrapper lagDokumentMetadataListe(String temakode) {
        return new DokumentMetadataResultatWrapper(asList(
                new DokumentMetadata()
                        .withJournalpostId("123")
                        .withHoveddokument(new Dokument().withTittel("Tittel for hoveddokument"))
                        .withTemakode(temakode)
                        .withVedlegg(asList())

        ), null);
    }

    private DokumentMetadataResultatWrapper lagDokumentMetadataIkkeJournalfortListe(String temakode) {
        return new DokumentMetadataResultatWrapper(asList(
                new DokumentMetadata()
                        .withJournalpostId("123")
                        .withIsJournalfort(false)
                        .withHoveddokument(new Dokument().withTittel("Tittel for hoveddokument"))
                        .withTemakode(temakode)
                        .withVedlegg(asList())
        ), null);
    }

    private Cookie[] lagSaksbehandlerCookie(String valgtEnhet) {
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie("saksbehandlerinnstillinger-null", valgtEnhet);
        return cookies;
    }

    private Optional<Response> mockIkkeTomHttpServletResponse() {
        return of(Response.status(Response.Status.UNAUTHORIZED).build());
    }

}
