package no.nav.sbl.dialogarena.sak.rest;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.sak.service.InnsynImpl;
import no.nav.sbl.dialogarena.sak.service.interfaces.TilgangskontrollService;
import no.nav.sbl.dialogarena.sak.viewdomain.dokumentvisning.JournalpostResultat;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
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
import javax.ws.rs.core.Response;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.sak.rest.DokumentController.TEMAKODE_BIDRAG;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DokumentControllerTest {


    @Mock
    private InnsynImpl innsyn;

    @Mock
    private SaksService saksService;

    @Mock
    private AnsattService ansattService;

    @Mock
    private DokumentMetadataService dokumentMetadataService;

    @Mock
    private TilgangskontrollService tilgangskontrollService;

    @InjectMocks
    private DokumentController dokumentController = new DokumentController();

    private final MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
    private final String VALGT_ENHET = "0000";
    private final String ANNEN_ENHET = "0002";
    private final String TEMA_KODE = "temakode";
    private final String ANNEN_TEMAKODE = "annentemakode";

    @Before
    public void setup() {
        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
        httpServletRequest.setCookies(lagSaksbehandlerCookie(VALGT_ENHET));
    }

    @Test
    public void returnererFeilmeldingStatusOmSaksbehandlerIkkeHarTilgangTilValgtEnhet() {
        when(tilgangskontrollService.harSaksbehandlerTilgangTilDokument(any(String.class), any(String.class))).thenReturn(false);
        when(ansattService.hentEnhetsliste()).thenReturn(asList(new AnsattEnhet(ANNEN_ENHET, "enhetsnavn")));

        Response response = dokumentController.hentJournalpostMetadata("11111111111", "123", "temakode", httpServletRequest);

        assertThat(response.getStatus(), is(401));
    }

    @Test
    public void returnererFeilmeldingOmSaksbehandlerIkkeHarTilgangTilTema() {
        when(tilgangskontrollService.harSaksbehandlerTilgangTilDokument(any(String.class), any(String.class))).thenReturn(false);
        when(dokumentMetadataService.hentDokumentMetadata(any(List.class), any(String.class))).thenReturn(lagDokumentMetadataListe(TEMA_KODE));
        when(ansattService.hentEnhetsliste()).thenReturn(asList(new AnsattEnhet(VALGT_ENHET, "enhetsnavn")));

        Response response = dokumentController.hentJournalpostMetadata("11111111111", "123", "temakode", httpServletRequest);

        assertThat(((JournalpostResultat) response.getEntity()).getFeilendeDokumenter().get(0).getFeilmeldingEnonicKey(), is("feilmelding.saksbehandlerikketilgang"));
    }

    @Test
    public void returnererDokumentOmSaksbehandlerHarTilgangTilTema() {
        when(ansattService.hentEnhetsliste()).thenReturn(asList(new AnsattEnhet("0000", "enhetsnavn")));
        when(dokumentMetadataService.hentDokumentMetadata(any(List.class), any(String.class))).thenReturn(lagDokumentMetadataListe(TEMA_KODE));
        when(tilgangskontrollService.harSaksbehandlerTilgangTilDokument(any(String.class), any(String.class))).thenReturn(true);

        Response response = dokumentController.hentJournalpostMetadata("11111111111", "123", "temakode", httpServletRequest);

        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void returnererFeilmeldingOmDokumentErJournalfortPaAnnetTema() {
        when(ansattService.hentEnhetsliste()).thenReturn(asList(new AnsattEnhet(VALGT_ENHET, "enhetsnavn")));
        when(dokumentMetadataService.hentDokumentMetadata(any(List.class), any(String.class))).thenReturn(lagDokumentMetadataListe(TEMA_KODE));
        when(tilgangskontrollService.harSaksbehandlerTilgangTilDokument(any(String.class), any(String.class))).thenReturn(true);

        Response response = dokumentController.hentJournalpostMetadata("11111111111", "123", ANNEN_TEMAKODE, httpServletRequest);

        assertThat(((JournalpostResultat) response.getEntity()).getFeilendeDokumenter().get(0).getFeilmeldingEnonicKey(), is("feilmelding.journalfortannettema"));
    }

    @Test
    public void returnererFeilmeldingOmTemaErBidrag() {
        when(ansattService.hentEnhetsliste()).thenReturn(asList(new AnsattEnhet(VALGT_ENHET, "enhetsnavn")));
        when(dokumentMetadataService.hentDokumentMetadata(any(List.class), any(String.class))).thenReturn(lagDokumentMetadataListe(TEMAKODE_BIDRAG));
        when(tilgangskontrollService.harSaksbehandlerTilgangTilDokument(any(String.class), any(String.class))).thenReturn(true);

        Response response = dokumentController.hentJournalpostMetadata("11111111111", "123", TEMAKODE_BIDRAG, httpServletRequest);

        assertThat(((JournalpostResultat) response.getEntity()).getFeilendeDokumenter().get(0).getFeilmeldingEnonicKey(), is("feilmelding.temakode.bidrag"));
    }

    private List<DokumentMetadata> lagDokumentMetadataListe(String temakode) {
        return asList(
                new DokumentMetadata()
                        .withJournalpostId("123")
                        .withHoveddokument(new Dokument().withTittel("Tittel for hoveddokument"))
                        .withTemakode(temakode)
                        .withVedlegg(asList())
        );
    }

    private Cookie[] lagSaksbehandlerCookie(String valgtEnhet) {
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie("saksbehandlerinnstillinger-null", valgtEnhet);
        return cookies;
    }

}
