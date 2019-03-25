package no.nav.sbl.dialogarena.modiabrukerdialog.sak.rest;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.domain.dokumentvisning.DokumentFeilmelding;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.domain.dokumentvisning.DokumentResultat;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.domain.dokumentvisning.JournalpostResultat;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Dokument;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Feilmelding;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.TjenesteResultatWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.DokumentMetadataService;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.TilgangskontrollService;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.saf.SafService;
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
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Feilmelding.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.rest.mock.DokumentControllerMock.mockDokumentResponse;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.rest.mock.DokumentControllerMock.mockJournalpost;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.saf.SafDokumentMapperKt.VARIANTFORMAT_ARKIV;


@Path("/saksoversikt/{fnr}")
@Produces("application/json")
public class DokumentController {

    public static final Logger logger = LoggerFactory.getLogger(DokumentController.class);

    @Inject
    private SafService safService;

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

        ResultatWrapper<Object> hentDokumentResultat = safService.hentDokument(journalpostId, dokumentreferanse, VARIANTFORMAT_ARKIV);
        return Optional.of(hentDokumentResultat.resultat)
                .map(res -> ok(res).type("application/pdf").build())
                .orElse(status(NOT_FOUND).build());
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

        List<DokumentFeilmelding> feilmeldinger = hentFeilmeldinger(journalpostMetadata, journalpostId);
        List<DokumentResultat> pdfer = hentDokumentResultater(fnr, journalpostId, journalpostMetadata);

        JournalpostResultat resultat = new JournalpostResultat().withTittel(hovedtittel);
        resultat.withDokumentFeilmeldinger(feilmeldinger);
        resultat.withDokumenter(pdfer);
        return ok(resultat).build();
    }


    private boolean harIkkeTilgang(TjenesteResultatWrapper tjenesteResultat) {
        return !tjenesteResultat.result.isPresent();
    }

    private DokumentMetadata hentDokumentMetadata(String journalpostId, String fnr) {
        return dokumentMetadataService.hentDokumentMetadata(fnr)
                .resultat
                .stream()
                .filter(dokumentMetadata -> journalpostId.equals(dokumentMetadata.getJournalpostId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(format("Fant ikke metadata om journalpostId %s. Dette bør ikke skje.", journalpostId)));
    }

    private List<DokumentFeilmelding> hentFeilmeldinger(DokumentMetadata journalpostMetadata, String journalpostId) {
        List<Pair<String, TjenesteResultatWrapper>> refOgDokumenter = lagDokRefOgDokumenter(journalpostId, journalpostMetadata);
        return refOgDokumenter
                .stream()
                .filter(refOgDok -> harFeil(refOgDok.getRight()))
                .map(refOgDok -> TIL_FEIL.apply(journalpostMetadata, refOgDok.getRight().feilmelding))
                .collect(toList());
    }

    private List<DokumentResultat> hentDokumentResultater(String fnr, String journalpostId, DokumentMetadata journalpostMetadata) {
        try {
            List<Pair<String, TjenesteResultatWrapper>> refOgDokument = lagDokRefOgDokumenter(journalpostId, journalpostMetadata);
            ForkJoinPool forkJoinPool = new ForkJoinPool(4);
            return forkJoinPool.submit(() ->
                    refOgDokument.parallelStream()
                            .filter((Pair<String, TjenesteResultatWrapper> data) -> !harFeil(data.getRight()))
                            .map(dok -> lagDokumentResultat(fnr, journalpostId, journalpostMetadata, dok))
                            .collect(toList())).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Kunne ikke håndtere alle pdfer", e);
            return emptyList();
        }
    }

    private List<Pair<String, TjenesteResultatWrapper>> lagDokRefOgDokumenter(String journalpostId, DokumentMetadata journalpostMetadata) {
        Set<String> dokumentreferanser = getDokumentReferanser(journalpostMetadata);
        return dokumentreferanser
                .stream()
                .map(dokumentreferanse -> new ImmutablePair<>(dokumentreferanse, hentDokument(journalpostId, dokumentreferanse)))
                .collect(toList());
    }

    private TjenesteResultatWrapper hentDokument(String journalpostId, String dokumentreferanse) {
        ResultatWrapper<Object> dokumentWrapper = safService.hentDokument(journalpostId, dokumentreferanse, VARIANTFORMAT_ARKIV);
        if (isNull(dokumentWrapper.resultat)) {
            return new TjenesteResultatWrapper(UKJENT_FEIL);
        }

        return new TjenesteResultatWrapper(dokumentWrapper.resultat);
    }

    private Set<String> getDokumentReferanser(DokumentMetadata journalpostMetadata) {
        Set<String> dokumentreferanser = new TreeSet<>();
        dokumentreferanser.add(journalpostMetadata.getHoveddokument().getDokumentreferanse());
        dokumentreferanser.addAll(vedleggsreferanser(journalpostMetadata));
        return dokumentreferanser;
    }

    private boolean harFeil(TjenesteResultatWrapper tjenesteResultat) {
        return tjenesteResultat.feilmelding != null || harIkkeTilgang(tjenesteResultat);
    }

    private List<String> vedleggsreferanser(DokumentMetadata journalpostMetadata) {
        return journalpostMetadata.getVedlegg()
                .stream()
                .filter(dokument -> !dokument.isLogiskDokument())
                .map(Dokument::getDokumentreferanse)
                .collect(Collectors.toList());
    }

    private DokumentResultat lagDokumentResultat(String fnr, String journalpostId, DokumentMetadata journalpostMetadata, Pair<String, TjenesteResultatWrapper> dokRefOgDok) {
        int antallSider = hentAntallSiderIDokument(dokRefOgDok);
        boolean erHoveddokument = dokRefOgDok.getLeft().equals(journalpostMetadata.getHoveddokument().getDokumentreferanse());
        String tittel = getVedleggEllerHoveddokumentTittel(journalpostMetadata, dokRefOgDok);

        return new DokumentResultat(tittel, antallSider, fnr, journalpostId, dokRefOgDok.getLeft(), erHoveddokument);
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

    private String getVedleggEllerHoveddokumentTittel(DokumentMetadata journalpostMetadata, Pair<String, TjenesteResultatWrapper> refOgDok) {
        return journalpostMetadata.getVedlegg()
                .stream()
                .filter((vedlegg) -> refOgDok.getLeft().equals(vedlegg.getDokumentreferanse()))
                .findFirst()
                .map(Dokument::getTittel)
                .orElse(journalpostMetadata.getHoveddokument().getTittel());
    }

    private boolean finnesDokumentReferansenIMetadata(DokumentMetadata dokumentMetadata, String dokumentreferanse) {
        if (dokumentMetadata.getHoveddokument().getDokumentreferanse().equals(dokumentreferanse)) {
            return true;
        }

        return dokumentMetadata.getVedlegg()
                .stream()
                .anyMatch(dokument -> dokument.getDokumentreferanse().equals(dokumentreferanse));
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
