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
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.TjenesteResultatWrapper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.inject.Inject;
import javax.ws.rs.*;
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
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static no.nav.sbl.dialogarena.sak.rest.mock.DokumentControllerMock.mockDokumentResponse;
import static no.nav.sbl.dialogarena.sak.rest.mock.DokumentControllerMock.mockJournalpost;
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

    @Inject
    private TilgangskontrollService tilgangskontrollService;

    private final String DOKUMENTID_IKKE_FUNNET = "x";
    public final static String BLURRED_DOKUMENT = "/modiabrukerdialog/img/saksoversikt/Dummy_dokument.jpg";

    @GET
    @Path("/dokument/{journalpostId}/{dokumentreferanse}")
    public Response hentDokument(@PathParam("fnr") String fnr, @PathParam("journalpostId") String journalpostId, @PathParam("dokumentreferanse") String dokumentreferanse) throws IOException {
        if (getProperty("dokumentressurs.withmock", "false").equalsIgnoreCase("true")) {
            return mockDokumentResponse();
        }

        TjenesteResultatWrapper hentDokumentResultat = innsyn.hentDokument(journalpostId, dokumentreferanse);
        return hentDokumentResultat.result
                .map(res -> ok(res).type("application/pdf").build())
                .orElse(status(404).build());
    }

    @GET
    @Path("/journalpostmetadata/{journalpostId}")
    public Response hentJournalpostMetadata(@PathParam("fnr") String fnr, @PathParam("journalpostId") String journalpostId, @QueryParam("temakode") String temakode) throws ExecutionException, InterruptedException {
        if (getProperty("dokumentressurs.withmock", "false").equalsIgnoreCase("true")) {
            return ok(mockJournalpost().withDokumentFeilmelding(blurretDokumentReferanseResponse(DOKUMENT_IKKE_FUNNET, "Dokument 1"))).build();
        }

        DokumentMetadata journalpostMetadata = hentDokumentMetadata(journalpostId, fnr);
        JournalpostResultat resultat = new JournalpostResultat()
                .withTittel(journalpostMetadata.getHoveddokument().getTittel());

        if (erJournalfortPaAnnetTema(temakode, journalpostMetadata)) {
            resultat.withDokumentFeilmelding(blurretDokumentReferanseResponse(JOURNALFORT_ANNET_TEMA, journalpostMetadata.getHoveddokument().getTittel(), journalfortAnnetTemaEktraFeilInfo(journalpostId, journalpostMetadata.getTemakodeVisning())));
        }

        Set<String> dokumentreferanser = new HashSet<>();
        dokumentreferanser.add(journalpostMetadata.getHoveddokument().getDokumentreferanse());

        journalpostMetadata.getVedlegg()
                .stream()
                .forEach(dokument -> dokumentreferanser.add(dokument.getDokumentreferanse()));

        //TODO
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
        return tjenesteResultat.feilmelding != null || !tjenesteResultat.result.isPresent();
    }

    private List<DokumentResultat> hentDokumentResultater(String fnr, String journalpostId, DokumentMetadata journalpostMetadata, List<Pair<String, TjenesteResultatWrapper>> dokumenter) throws InterruptedException, ExecutionException {
        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        return forkJoinPool.submit(() -> {
            return dokumenter
                    .parallelStream()
                    .filter((Pair<String, TjenesteResultatWrapper> data) -> !harFeil(data.getRight()))
                    .map((Pair<String, TjenesteResultatWrapper> data) -> {
                        try {
                            byte[] dokumentdata = ((byte[]) data.getRight().result.get());
                            InputStream is = new ByteArrayInputStream(dokumentdata);
                            int antallSider = PDDocument.load(is).getNumberOfPages();

                            String pdfUrl = format("/modiabrukerdialog/rest/saksoversikt/%s/dokument/%s/%s",
                                    fnr,
                                    journalpostId,
                                    data.getLeft()
                            );

                            return new DokumentResultat(pdfUrl, journalpostMetadata.getHoveddokument().getTittel(), antallSider);
                        } catch (IOException e) {
                            throw new RuntimeException("Kunne ikke laste inn pdf", e);
                        }
                    }).collect(toList());
        }).get();
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

    private DokumentFeilmelding blurretDokumentReferanseResponse(Feilmelding feilmelding, String tittel) {
        return blurretDokumentReferanseResponse(feilmelding, tittel, new HashMap<String, String>());
    }

    private DokumentFeilmelding blurretDokumentReferanseResponse(Feilmelding feilmelding, String tittel, Map ekstrafeilinfo) {
        return new DokumentFeilmelding(tittel, feilmelding.feilmeldingKey, BLURRED_DOKUMENT, ekstrafeilinfo);
    }

    private Map journalfortAnnetTemaEktraFeilInfo(String journalpostId, String temanavn) {
        Map map = new HashMap<>();
        map.put("temanavn", temanavn);
        map.put("journalpostid", journalpostId);
        return map;
    }

    private static BiFunction<DokumentMetadata, Feilmelding, DokumentFeilmelding> TIL_FEIL = (dokumentMetadata, feilmelding) -> new DokumentFeilmelding(dokumentMetadata.getHoveddokument().getTittel(), feilmelding.feilmeldingKey, BLURRED_DOKUMENT, null);
}
