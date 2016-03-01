package no.nav.sbl.dialogarena.sak.rest;

import no.nav.sbl.dialogarena.sak.service.InnsynImpl;
import no.nav.sbl.dialogarena.sak.viewdomain.dokumentvisning.DokumentFeilmelding;
import no.nav.sbl.dialogarena.sak.viewdomain.dokumentvisning.DokumentResultat;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Feilmelding;
import no.nav.sbl.dialogarena.saksoversikt.service.service.DokumentMetadataService;
import no.nav.sbl.dialogarena.saksoversikt.service.service.SaksService;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.TjenesteResultatWrapper;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static java.lang.System.getProperty;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static no.nav.sbl.dialogarena.sak.rest.mock.DokumentControllerMock.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Feilmelding.DOKUMENT_IKKE_FUNNET;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Feilmelding.JOURNALFORT_ANNET_TEMA;

@Path("/saksoversikt/{fnr}")
@Produces("application/json")
public class DokumentController {

    @Inject
    private InnsynImpl innsyn;

    @Inject
    private SaksService saksService;

    @Inject
    private DokumentMetadataService dokumentMetadataService;

    private final String DOKUMENTID_IKKE_FUNNET = "x";
    public final static String BLURRED_DOKUMENT = getProperty("tjenester.url") + "/modiabrukerdialog/img/saksoversikt/Dummy_dokument.jpg";

    @GET
    @Path("/dokument/{journalpostId}/{dokumentreferanse}")
    public Response hentDokument(@PathParam("fnr") String fnr, @PathParam("journalpostId") String journalpostId, @PathParam("dokumentreferanse") String dokumentreferanse) throws IOException {
        if (getProperty("dokumentressurs.withmock", "false").equalsIgnoreCase("true")) {
            return mockDokumentResponse();
        }

        TjenesteResultatWrapper hentDokumentResultat = innsyn.hentDokument(dokumentreferanse, journalpostId);
        return hentDokumentResultat.result
                .map(res -> ok(res).type("application/pdf").build())
                .orElse(status(404).build());
    }

    @GET
    @Path("/dokumentmetadata/{journalpostId}/{dokumentreferanse}")
    public Response hentDokumentMetadata(@PathParam("fnr") String fnr, @PathParam("journalpostId") String journalpostId, @PathParam("dokumentreferanse") String dokumentreferanse, @QueryParam("temakode") String temakode) {
        if (getProperty("dokumentressurs.withmock", "false").equalsIgnoreCase("true")) {
            return ok(mockDokumentReferanserResponse(journalpostId, dokumentreferanse, fnr)).build();
        }

        if (DOKUMENTID_IKKE_FUNNET.equals(dokumentreferanse)) {
            return blurretDokumentReferanseResponse(DOKUMENT_IKKE_FUNNET);
        }

        DokumentMetadata dokumentMetadata = hentDokumentMetadata(journalpostId, fnr);
        if (!finnesDokumentReferansenIMetadata(dokumentMetadata, dokumentreferanse)) {
            return blurretDokumentReferanseResponse(DOKUMENT_IKKE_FUNNET);
        }

        if (erJournalfortPaAnnetTema(temakode, dokumentMetadata)) {
            return blurretDokumentReferanseResponse(JOURNALFORT_ANNET_TEMA, journalfortAnnetTemaEktraFeilInfo(journalpostId, dokumentreferanse, dokumentMetadata.getTemakodeVisning()));
        }

        String tittel = dokumentMetadata.getHoveddokument().getTittel();
        String pdfUrl = getProperty("tjenester.url") + "/modiabrukerdialog/rest/saksoversikt/" + fnr + "/dokument/" + journalpostId + "/" + dokumentreferanse;
        Integer antallsider = 1;
        return Response.ok(new DokumentResultat(pdfUrl, tittel, antallsider)).build();
    }

    @GET
    @Path("/journalpostmetadata/{journalpostId}")
    public Response hentJournalpostMetadata(@PathParam("fnr") String fnr, @PathParam("journalpostId") String journalpostId) {
        if (getProperty("dokumentressurs.withmock", "false").equalsIgnoreCase("true")) {
            return ok(mockDokumentMetaData(journalpostId)).build();
        }
        return ok(hentDokumentMetadata(journalpostId, fnr)).build();
    }

    private DokumentMetadata hentDokumentMetadata(String journalpostId, String fnr) {
        return dokumentMetadataService.hentDokumentMetadata(saksService.hentAlleSaker(fnr), fnr)
                .stream()
                .filter(dokumentMetadata -> journalpostId.equals(dokumentMetadata.getJournalpostId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(format("Fant ikke metadata om journalpostId %s. Dette bør ikke skje.", journalpostId)));
    }

    private boolean finnesDokumentReferansenIMetadata(DokumentMetadata dokumentMetadata, String dokumentreferanse) {
        if (dokumentMetadata.getHoveddokument().getDokumentreferanse().equals(dokumentreferanse)) {
            return true;
        }

        return dokumentMetadata.getVedlegg()
                .stream()
                .filter(dokument -> dokument.getDokumentreferanse().equals(dokumentreferanse))
                .findAny()
                .isPresent();
    }

    private boolean erJournalfortPaAnnetTema(String temakode, DokumentMetadata dokumentMetadata) {
        return temakode != null && !dokumentMetadata.getTemakode().equals(temakode);
    }

    private Response blurretDokumentReferanseResponse(Feilmelding feilmelding) {
        return blurretDokumentReferanseResponse(feilmelding, new HashMap<String, String>());
    }

    private Response blurretDokumentReferanseResponse(Feilmelding feilmelding, Map ekstrafeilinfo) {
        return ok(new DokumentFeilmelding(feilmelding.feilmeldingKey, BLURRED_DOKUMENT)).build();
    }

    private Map journalfortAnnetTemaEktraFeilInfo(String journalpostId, String dokumentreferanse, String temanavn) {
        Map map = new HashMap<>();
        map.put("temanavn", temanavn);
        map.put("dokumentreferanse", dokumentreferanse);
        map.put("journalpostid", journalpostId);
        return map;
    }
}
