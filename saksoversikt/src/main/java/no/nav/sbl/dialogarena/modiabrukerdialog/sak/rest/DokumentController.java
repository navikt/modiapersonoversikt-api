package no.nav.sbl.dialogarena.modiabrukerdialog.sak.rest;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.domain.dokumentvisning.DokumentFeilmelding;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.domain.dokumentvisning.DokumentResultat;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.domain.dokumentvisning.JournalpostResultat;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Dokument;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Feilmelding;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.TjenesteResultatWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.DokumentMetadataService;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.JournalV2ServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.SaksService;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.TilgangskontrollService;
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
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Feilmelding.DOKUMENT_IKKE_FUNNET;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Feilmelding.MANGLER_DOKUMENTMETADATA;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.rest.mock.DokumentControllerMock.mockDokumentResponse;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.rest.mock.DokumentControllerMock.mockJournalpost;


//Single Responsibility Principle
@SuppressWarnings("squid:S1200")
@Path("/saksoversikt/{fnr}")
@Produces("application/json")
public class DokumentController {

    public static final Logger logger = LoggerFactory.getLogger(DokumentController.class);

    @Inject
    private JournalV2ServiceImpl innsyn;

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

        if ("null".equals(journalpostId)) {
            return status(NOT_FOUND).build();
        }

        DokumentMetadata journalpostMetadata = hentDokumentMetadata(journalpostId, fnr);
        TjenesteResultatWrapper tilgangskontrollResult = tilgangskontrollService.harSaksbehandlerTilgangTilDokument(request, journalpostMetadata, fnr, journalpostMetadata.getTemakode());

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

        if ("null".equals(journalpostId)) {
            return ok(new JournalpostResultat().withDokumentFeilmelding(blurretDokumentReferanseResponse(MANGLER_DOKUMENTMETADATA, ""))).build();
        }

        DokumentMetadata journalpostMetadata = hentDokumentMetadata(journalpostId, fnr);
        String hovedtittel = journalpostMetadata.getHoveddokument().getTittel();

        TjenesteResultatWrapper tilgangskontrollResult = tilgangskontrollService.harSaksbehandlerTilgangTilDokument(request, journalpostMetadata, fnr, temakode);
        if (harIkkeTilgang(tilgangskontrollResult)) {
            JournalpostResultat feilside = new JournalpostResultat()
                    .withDokumentFeilmelding(blurretDokumentReferanseResponse(tilgangskontrollResult.feilmelding, hovedtittel, tilgangskontrollResult.ekstraFeilInfo));
            return ok(feilside).build();
        }

        JournalpostResultat resultat = new JournalpostResultat().withTittel(hovedtittel);
        Set<String> dokumentreferanser = new TreeSet<>();
        dokumentreferanser.add(journalpostMetadata.getHoveddokument().getDokumentreferanse());

        journalpostMetadata.getVedlegg()
                .stream()
                .filter(dokument -> !dokument.isLogiskDokument())
                .forEach(dokument -> dokumentreferanser.add(dokument.getDokumentreferanse()));

        List<Pair<String, TjenesteResultatWrapper>> dokumenter = dokumentreferanser
                .stream()
                .map(dokumentreferanse -> new ImmutablePair<>(dokumentreferanse, innsyn.hentDokument(journalpostId, dokumentreferanse)))
                .collect(toList());

        List<DokumentFeilmelding> feilmeldinger = dokumenter
                .stream()
                .filter((Pair<String, TjenesteResultatWrapper> data) -> harFeil(data.getRight()))
                .map((Pair<String, TjenesteResultatWrapper> data) -> TIL_FEIL.apply(journalpostMetadata, data.getRight().feilmelding))
                .collect(toList());

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
                            boolean erHoveddokument = data.getLeft().equals(journalpostMetadata.getHoveddokument().getDokumentreferanse());
                            String tittel = journalpostMetadata.getVedlegg()
                                    .stream()
                                    .filter((d) -> data.getLeft().equals(d.getDokumentreferanse()))
                                    .findFirst()
                                    .map(Dokument::getTittel)
                                    .orElse(journalpostMetadata.getHoveddokument().getTittel());

                            return new DokumentResultat(tittel, antallSider, fnr, journalpostId, data.getLeft(), erHoveddokument);
                        }).collect(toList());
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Kunne ikke håndtere alle pdfer", e);
            return emptyList();
        }
    }

    private int hentAntallSiderIDokument(Pair<String, TjenesteResultatWrapper> data) {
        byte[] dokumentdata = ((byte[]) data.getRight().result.get());
        InputStream is = new ByteArrayInputStream(dokumentdata);
        try {
            return PDDocument.load(is).getNumberOfPages();
        } catch (IOException e) {
            logger.error("Kunne ikke finne ut hvor mange sider dokumentet innehold", e);
            return 1;
        }
    }

    private DokumentMetadata hentDokumentMetadata(String journalpostId, String fnr) {
        return dokumentMetadataService.hentDokumentMetadata(fnr)
                .resultat
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

    private static final BiFunction<DokumentMetadata, Feilmelding, DokumentFeilmelding> TIL_FEIL =
            (dokumentMetadata, feilmelding) ->
                    new DokumentFeilmelding(dokumentMetadata.getHoveddokument().getTittel(), feilmelding.feilmeldingKey, BLURRED_DOKUMENT, new HashMap<>());
}
