package no.nav.sbl.dialogarena.modiabrukerdialog.sak.rest;

import no.nav.brukerdialog.security.context.ThreadLocalSubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.domain.dokumentvisning.JournalpostResultat;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Dokument;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.TjenesteResultatWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.DokumentMetadataService;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.TilgangskontrollService;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.saf.SafService;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.saf.SafService;
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

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Feilmelding.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.TilgangskontrollServiceImpl.TEMAKODE_BIDRAG;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DokumentControllerTest {

    @Mock
    private SafService safService;

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
        System.setProperty("no.nav.brukerdialog.security.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
        httpServletRequest.setCookies(lagSaksbehandlerCookie(VALGT_ENHET));
        when(dokumentMetadataService.hentDokumentMetadata(anyString())).thenReturn(lagDokumentMetadataListe("DAG"));
        when(tilgangskontrollService.harSaksbehandlerTilgangTilDokument(any(HttpServletRequest.class), any(DokumentMetadata.class), anyString(), anyString())).thenReturn(new TjenesteResultatWrapper("result"));
        when(safService.hentDokument(anyString(), anyString(), anyString())).thenReturn(new ResultatWrapper<>("null"));
    }

    @Test
    public void returnererFeilmeldingOmSaksbehandlerIkkeHarTilgangTilTema() {
        when(tilgangskontrollService.harSaksbehandlerTilgangTilDokument(any(HttpServletRequest.class), any(DokumentMetadata.class), anyString(), anyString())).thenReturn(new TjenesteResultatWrapper(SAKSBEHANDLER_IKKE_TILGANG));
        when(dokumentMetadataService.hentDokumentMetadata(any(String.class))).thenReturn(lagDokumentMetadataListe(TEMA_KODE));

        Response response = dokumentController.hentJournalpostMetadata(FNR, JOURNALPOSTID, TEMA_KODE, httpServletRequest);

        assertThat(((JournalpostResultat) response.getEntity()).getFeilendeDokumenter().get(0).getFeilmeldingEnonicKey(), is("feilmelding.saksbehandlerikketilgang"));
    }

    @Test
    public void returnererDokumentOmSaksbehandlerHarTilgangTilTema() {
        when(tilgangskontrollService.harSaksbehandlerTilgangTilDokument(any(HttpServletRequest.class), any(DokumentMetadata.class), anyString(), anyString())).thenReturn(new TjenesteResultatWrapper(TRUE));
        when(dokumentMetadataService.hentDokumentMetadata(any(String.class))).thenReturn(lagDokumentMetadataListe(TEMA_KODE));

        Response response = dokumentController.hentJournalpostMetadata(FNR, JOURNALPOSTID, TEMA_KODE, httpServletRequest);

        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void returnererFeilmeldingOmDokumentErJournalfortPaAnnetTema() {
        when(tilgangskontrollService.harSaksbehandlerTilgangTilDokument(any(HttpServletRequest.class), any(DokumentMetadata.class), anyString(), anyString())).thenReturn(new TjenesteResultatWrapper(JOURNALFORT_ANNET_TEMA));
        when(dokumentMetadataService.hentDokumentMetadata(any(String.class))).thenReturn(lagDokumentMetadataListe(TEMA_KODE));

        Response response = dokumentController.hentJournalpostMetadata(FNR, JOURNALPOSTID, ANNEN_TEMAKODE, httpServletRequest);

        assertThat(((JournalpostResultat) response.getEntity()).getFeilendeDokumenter().get(0).getFeilmeldingEnonicKey(), is("feilmelding.journalfortannettema"));
    }

    @Test
    public void returnererFeilmeldingOmTemaErBidrag() {
        when(tilgangskontrollService.harSaksbehandlerTilgangTilDokument(any(HttpServletRequest.class), any(DokumentMetadata.class), anyString(), anyString())).thenReturn(new TjenesteResultatWrapper(TEMAKODE_ER_BIDRAG));
        when(dokumentMetadataService.hentDokumentMetadata(any(String.class))).thenReturn(lagDokumentMetadataListe(TEMAKODE_BIDRAG));

        Response response = dokumentController.hentJournalpostMetadata(FNR, JOURNALPOSTID, TEMAKODE_BIDRAG, httpServletRequest);

        assertThat(((JournalpostResultat) response.getEntity()).getFeilendeDokumenter().get(0).getFeilmeldingEnonicKey(), is("feilmelding.sikkerhetsbegrensning"));
    }

    @Test
    public void returnerFeilmeldingHvisSaksbehandlerIkkeHarTilgangTilValgtEnhet() throws IOException {
        Response response = dokumentController.hentDokument(FNR, JOURNALPOSTID, DOKUMENTREFERANSE, httpServletRequest);

        assertThat(response.getStatus(), is(403));
    }

    @Test
    public void returnererFeilmeldingHvisJournalpostMetadataIkkeFinnesIJoarkPaaBruker() throws IOException {
        when(dokumentMetadataService.hentDokumentMetadata(any(String.class))).thenReturn(lagDokumentMetadataIkkeJournalfortListe(TEMA_KODE));

        Response response = dokumentController.hentDokument(FNR, JOURNALPOSTID, DOKUMENTREFERANSE, httpServletRequest);

        assertThat(response.getStatus(), is(403));
    }

    @Test
    public void returnererFeilmeldingHvisTemaErBidrag() throws IOException {
        when(dokumentMetadataService.hentDokumentMetadata(any(String.class))).thenReturn(lagDokumentMetadataListe(TEMAKODE_BIDRAG));

        Response response = dokumentController.hentDokument(FNR, JOURNALPOSTID, DOKUMENTREFERANSE, httpServletRequest);

        assertThat(response.getStatus(), is(403));
    }


    @Test
    public void returnererFeilmeldingHvisSaksbehandlerIkkeHarTilgangTilTema() throws IOException {
        when(dokumentMetadataService.hentDokumentMetadata(any(String.class))).thenReturn(lagDokumentMetadataListe(TEMA_KODE));
        when(tilgangskontrollService.harSaksbehandlerTilgangTilDokument(any(HttpServletRequest.class), any(DokumentMetadata.class), anyString(), anyString())).thenReturn(new TjenesteResultatWrapper(FALSE));

        Response response = dokumentController.hentDokument(FNR, JOURNALPOSTID, DOKUMENTREFERANSE, httpServletRequest);

        assertThat(response.getStatus(), is(403));
    }

    private ResultatWrapper<List<DokumentMetadata>> lagDokumentMetadataListe(String temakode) {
        return new ResultatWrapper(asList(
                new DokumentMetadata()
                        .withJournalpostId("123")
                        .withHoveddokument(new Dokument().withTittel("Tittel for hoveddokument").withDokumentreferanse("123"))
                        .withTemakode(temakode)
                        .withVedlegg(asList())

        ), null);
    }

    private ResultatWrapper<List<DokumentMetadata>> lagDokumentMetadataIkkeJournalfortListe(String temakode) {
        return new ResultatWrapper(asList(
                new DokumentMetadata()
                        .withJournalpostId("123")
                        .withIsJournalfort(false)
                        .withHoveddokument(new Dokument().withTittel("Tittel for hoveddokument").withDokumentreferanse("123"))
                        .withTemakode(temakode)
                        .withVedlegg(asList())
        ), null);
    }

    private Cookie[] lagSaksbehandlerCookie(String valgtEnhet) {
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie("saksbehandlerinnstillinger-null", valgtEnhet);
        return cookies;
    }
}
