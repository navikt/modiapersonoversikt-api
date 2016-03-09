package no.nav.sbl.dialogarena.sak.rest;

import no.nav.sbl.dialogarena.sak.service.InnsynImpl;
import no.nav.sbl.dialogarena.sak.service.interfaces.TilgangskontrollService;
import no.nav.sbl.dialogarena.sak.viewdomain.dokumentvisning.DokumentFeilmelding;
import no.nav.sbl.dialogarena.sak.viewdomain.dokumentvisning.DokumentResultat;
import no.nav.sbl.dialogarena.sak.viewdomain.dokumentvisning.JournalpostResultat;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Feilmelding;
import no.nav.sbl.dialogarena.saksoversikt.service.service.DokumentMetadataService;
import no.nav.sbl.dialogarena.saksoversikt.service.service.SaksService;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.TjenesteResultatWrapper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiFunction;

import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.Response.Status.*;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static no.nav.sbl.dialogarena.sak.rest.mock.DokumentControllerMock.mockDokumentResponse;
import static no.nav.sbl.dialogarena.sak.rest.mock.DokumentControllerMock.mockJournalpost;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Feilmelding.*;

@Path("/saksoversikt/{fnr}")
@Produces("application/json")
public class DokumentController {

    public static final Logger logger = LoggerFactory.getLogger(DokumentController.class);

    @Inject
    private InnsynImpl innsyn;

    @Inject
    private SaksService saksService;

    @Inject
    private DokumentMetadataService dokumentMetadataService;

    @Inject
    private TilgangskontrollService tilgangskontrollService;


    public final static String BLURRED_DOKUMENT = "/modiabrukerdialog/img/saksoversikt/Dummy_dokument.jpg";

    @GET
    @Path("/dokument/{journalpostId}/{dokumentreferanse}")
    public Response hentDokument(@PathParam("fnr") String fnr, @PathParam("journalpostId") String journalpostId,
                                 @PathParam("dokumentreferanse") String dokumentreferanse,
                                 @Context HttpServletRequest request) throws IOException {
        if (getProperty("dokumentressurs.withmock", "false").equalsIgnoreCase("true")) {
            return mockDokumentResponse();
        }

        DokumentMetadata journalpostMetadata = hentDokumentMetadata(journalpostId, fnr);
        TjenesteResultatWrapper tilgangskontrollResult = tilgangskontrollService.harSaksbehandlerTilgangTilDokument(request, journalpostMetadata);

        if (harIkkeTilgang(tilgangskontrollResult) || !finnesDokumentReferansenIMetadata(journalpostMetadata, dokumentreferanse)) {
            return status(FORBIDDEN).build();
        }

        TjenesteResultatWrapper hentDokumentResultat = innsyn.hentDokument(journalpostId, dokumentreferanse);
        return hentDokumentResultat.result
                .map(res -> ok(res).type("application/pdf").build())
                .orElse(status(NOT_FOUND).build());
    }

    private boolean harIkkeTilgang(TjenesteResultatWrapper tilgangskontrollResult) {
        return !tilgangskontrollResult.result.isPresent();
    }

    @GET
    @Path("/journalpostmetadata/{journalpostId}")
    public Response hentJournalpostMetadata(@PathParam("fnr") String fnr, @PathParam("journalpostId") String journalpostId,
                                            @QueryParam("temakode") String temakode, @Context HttpServletRequest request) {
        if (getProperty("dokumentressurs.withmock", "false").equalsIgnoreCase("true")) {
            return ok(mockJournalpost().withDokumentFeilmelding(blurretDokumentReferanseResponse(DOKUMENT_IKKE_FUNNET, "Dokument 1"))).build();
        }

        DokumentMetadata journalpostMetadata = hentDokumentMetadata(journalpostId, fnr);
        TjenesteResultatWrapper tilgangskontrollResult = tilgangskontrollService.harSaksbehandlerTilgangTilDokument(request, journalpostMetadata);
        String hovedtittel = journalpostMetadata.getHoveddokument().getTittel();
        JournalpostResultat resultat = new JournalpostResultat()
                .withTittel(hovedtittel);

        if (harIkkeTilgang(tilgangskontrollResult)) {
            resultat.withDokumentFeilmelding(blurretDokumentReferanseResponse(tilgangskontrollResult.feilmelding, hovedtittel, tilgangskontrollResult.ekstraFeilInfo));
            return ok(resultat).build();
        }

        Set<String> dokumentreferanser = new HashSet<>();
        dokumentreferanser.add(journalpostMetadata.getHoveddokument().getDokumentreferanse());

        journalpostMetadata.getVedlegg()
                .stream()
                .forEach(dokument -> dokumentreferanser.add(dokument.getDokumentreferanse()));

        //1. Gå igjennom alle dokumentreferanser. Gjør kall til innsyn.hentDokument(dokumenreferenase, journalpostId).
        List<Pair<String, TjenesteResultatWrapper>> dokumenter = dokumentreferanser
                .stream()
                .map(dokumentreferanse -> new ImmutablePair<>(dokumentreferanse, innsyn.hentDokument(journalpostId, dokumentreferanse)))
                .collect(toList());

        //2. Dersom feilmelding: Legg til i resultat.withDokumentFeilmelding med riktig feilmelding.
        List<DokumentFeilmelding> feilmeldinger = dokumenter
                .stream()
                .filter((Pair<String, TjenesteResultatWrapper> data) -> harFeil(data.getRight()))
                .map((Pair<String, TjenesteResultatWrapper> data) -> TIL_FEIL.apply(journalpostMetadata, data.getRight().feilmelding))
                .collect(toList());

        //3. Ellers: Finn antall sider ved å bruke en ny dependency. Legg til i resultat.withDokument
        List<DokumentResultat> pdfer = hentDokumentResultater(fnr, journalpostId, journalpostMetadata, dokumenter);

        resultat.withDokumentFeilmeldinger(feilmeldinger);
        resultat.withDokumenter(pdfer);

        return ok(resultat).build();
    }

    private boolean harFeil(TjenesteResultatWrapper tjenesteResultat) {
        return tjenesteResultat.feilmelding != null || harIkkeTilgang(tjenesteResultat);
    }

    private List<DokumentResultat> hentDokumentResultater(String fnr, String journalpostId, DokumentMetadata journalpostMetadata, List<Pair<String, TjenesteResultatWrapper>> dokumenter) {
        try {
            ForkJoinPool forkJoinPool = new ForkJoinPool(4);
            return forkJoinPool.submit(() -> {
                return dokumenter
                        .parallelStream()
                        .filter((Pair<String, TjenesteResultatWrapper> data) -> !harFeil(data.getRight()))
                        .map((Pair<String, TjenesteResultatWrapper> data) -> {
                            int antallSider = hentAntallSiderIDokument(data);
                            return new DokumentResultat(journalpostMetadata.getHoveddokument().getTittel(), antallSider, fnr, journalpostId, data.getLeft());
                        }).collect(toList());
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Kunne ikke håndtere alle pdfer", e);
            return Collections.emptyList();
        }
    }

    private int hentAntallSiderIDokument(Pair<String, TjenesteResultatWrapper> data) {
        byte[] dokumentdata = ((byte[]) data.getRight().result.get());
        InputStream is = new ByteArrayInputStream(dokumentdata);
        try {
            return PDDocument.load(is).getNumberOfPages();
        } catch (IOException e) {
            logger.error("Kunne ikke finne ut hvor mange sider dokumentet innehold", e);
            return 0;
        }
    }

    private DokumentMetadata hentDokumentMetadata(String journalpostId, String fnr) {
        return dokumentMetadataService.hentDokumentMetadata(saksService.hentAlleSaker(fnr).alleSaker, fnr)
                .dokumentMetadata
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

    private DokumentFeilmelding blurretDokumentReferanseResponse(Feilmelding feilmelding, String tittel) {
        return blurretDokumentReferanseResponse(feilmelding, tittel, new HashMap<String, String>());
    }

    private DokumentFeilmelding blurretDokumentReferanseResponse(Feilmelding feilmelding, String tittel, Map ekstrafeilinfo) {
        return new DokumentFeilmelding(tittel, feilmelding.feilmeldingKey, BLURRED_DOKUMENT, ekstrafeilinfo);
    }

    private static BiFunction<DokumentMetadata, Feilmelding, DokumentFeilmelding> TIL_FEIL = (dokumentMetadata, feilmelding) -> new DokumentFeilmelding(dokumentMetadata.getHoveddokument().getTittel(), feilmelding.feilmeldingKey, BLURRED_DOKUMENT, null);
}
