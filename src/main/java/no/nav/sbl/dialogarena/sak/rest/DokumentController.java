package no.nav.sbl.dialogarena.sak.rest;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.sak.service.InnsynImpl;
import no.nav.sbl.dialogarena.sak.service.interfaces.TilgangskontrollService;
import no.nav.sbl.dialogarena.sak.viewdomain.dokumentvisning.DokumentFeilmelding;
import no.nav.sbl.dialogarena.sak.viewdomain.dokumentvisning.JournalpostResultat;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Feilmelding;
import no.nav.sbl.dialogarena.saksoversikt.service.service.DokumentMetadataService;
import no.nav.sbl.dialogarena.saksoversikt.service.service.SaksService;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.TjenesteResultatWrapper;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

import static java.lang.String.format;
import static java.lang.System.getProperty;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet.ENHET_ID;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils.hentValgtEnhet;
import static no.nav.sbl.dialogarena.sak.rest.mock.DokumentControllerMock.mockDokumentResponse;
import static no.nav.sbl.dialogarena.sak.rest.mock.DokumentControllerMock.mockJournalpost;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Feilmelding.*;
import static org.slf4j.LoggerFactory.getLogger;

@Path("/saksoversikt/{fnr}")
@Produces("application/json")
public class DokumentController {

    @Inject
    private InnsynImpl innsyn;

    @Inject
    private SaksService saksService;

    @Inject
    private AnsattService ansattService;

    @Inject
    private DokumentMetadataService dokumentMetadataService;

    @Inject
    private TilgangskontrollService tilgangskontrollService;

    private static final Logger logger = getLogger(DokumentController.class);

    public final static String TEMAKODE_BIDRAG = "BID";
    public final static String BLURRED_DOKUMENT = getProperty("modapp.url") + "/modiabrukerdialog/img/saksoversikt/Dummy_dokument.jpg";

    @GET
    @Path("/dokument/{journalpostId}/{dokumentreferanse}")
    public Response hentDokument(@PathParam("fnr") String fnr, @PathParam("journalpostId") String journalpostId,
                                 @PathParam("dokumentreferanse") String dokumentreferanse,
                                 @Context HttpServletRequest request) throws IOException {
        if (getProperty("dokumentressurs.withmock", "false").equalsIgnoreCase("true")) {
            return mockDokumentResponse();
        }

        String valgtEnhet = hentValgtEnhet(request);
        List<String> enhetsListe = on(ansattService.hentEnhetsliste()).map(ENHET_ID).collect();

        if (!enhetsListe.contains(valgtEnhet)) {
            logger.warn("{} har ikke tilgang til enhet {}.", getSubjectHandler().getUid(), valgtEnhet);
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        DokumentMetadata journalpostMetadata = hentDokumentMetadata(journalpostId, fnr);
        String temakode = journalpostMetadata.getTemakode();

        if (finnesIkkeIJoarkPaBruker(journalpostMetadata) || temakodeErBidrag(temakode)) {
            return status(403).build();
        }

        boolean harSaksbehandlerTilgang = tilgangskontrollService.harSaksbehandlerTilgangTilDokument(temakode, valgtEnhet);
        if (!harSaksbehandlerTilgang) {
            return status(403).build();
        }

        TjenesteResultatWrapper hentDokumentResultat = innsyn.hentDokument(dokumentreferanse, journalpostId);
        return hentDokumentResultat.result
                .map(res -> ok(res).type("application/pdf").build())
                .orElse(status(404).build());
    }

    @GET
    @Path("/journalpostmetadata/{journalpostId}")
    public Response hentJournalpostMetadata(@PathParam("fnr") String fnr, @PathParam("journalpostId") String journalpostId,
                                            @QueryParam("temakode") String temakode, @Context HttpServletRequest request) {
        if (getProperty("dokumentressurs.withmock", "false").equalsIgnoreCase("true")) {
            return ok(mockJournalpost().withDokumentFeilmelding(blurretDokumentReferanseResponse(DOKUMENT_IKKE_FUNNET, "Dokument 1"))).build();
        }

        String valgtEnhet = hentValgtEnhet(request);
        List<String> enhetsListe = on(ansattService.hentEnhetsliste()).map(ENHET_ID).collect();

        if (!enhetsListe.contains(valgtEnhet)) {
            logger.warn("{} har ikke tilgang til enhet {}.", getSubjectHandler().getUid(), valgtEnhet);
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        DokumentMetadata journalpostMetadata = hentDokumentMetadata(journalpostId, fnr);
        JournalpostResultat resultat = new JournalpostResultat()
                .withTittel(journalpostMetadata.getHoveddokument().getTittel());

        if (temakodeErBidrag(journalpostMetadata.getTemakode())) {
            resultat.withDokumentFeilmelding(blurretDokumentReferanseResponse(TEMAKODE_ER_BIDRAG, journalpostMetadata.getHoveddokument().getTittel()));
            return ok(resultat).build();
        }

        if (erJournalfortPaAnnetTema(temakode, journalpostMetadata)) {
            resultat.withDokumentFeilmelding(blurretDokumentReferanseResponse(JOURNALFORT_ANNET_TEMA, journalpostMetadata.getHoveddokument().getTittel(), journalfortAnnetTemaEktraFeilInfo(journalpostId, journalpostMetadata.getTemakodeVisning())));
            return ok(resultat).build();
        }

        boolean harSaksbehandlerTilgang = tilgangskontrollService.harSaksbehandlerTilgangTilDokument(temakode, valgtEnhet);
        if (!harSaksbehandlerTilgang) {
            resultat.withDokumentFeilmelding(blurretDokumentReferanseResponse(SAKSBEHANDLER_IKKE_TILGANG, journalpostMetadata.getHoveddokument().getTittel()));
            return ok(resultat).build();
        }

        //Dette betyr at den enten ikke er journalfort eller er journalfort pa en annen bruker
        if (finnesIkkeIJoarkPaBruker(journalpostMetadata)) {
            resultat.withDokumentFeilmelding(blurretDokumentReferanseResponse(IKKE_JOURNALFORT_ELLER_ANNEN_BRUKER, journalpostMetadata.getHoveddokument().getTittel()));
            return ok(resultat).build();
        }

        Set<String> dokumentreferanser = new HashSet<>();
        dokumentreferanser.add(journalpostMetadata.getHoveddokument().getDokumentreferanse());

        journalpostMetadata.getVedlegg()
                .stream()
                .forEach(dokument -> dokumentreferanser.add(dokument.getDokumentreferanse()));

        //TODO
        //1. Gå igjennom alle dokumentreferanser. Gjør kall til innsyn.hentDokument(dokumenreferenase, journalpostId).
        //2. Dersom feilmelding: Legg til i resultat.withDokumentFeilmelding med riktig feilmelding.
        //3. Ellers: Finn antall sider ved å bruke en ny dependency (muligens vet Nicklas / Joanna / Torstein hvilken). Legg til i resultat.withDokument

        resultat.withDokumentFeilmelding(blurretDokumentReferanseResponse(DOKUMENT_IKKE_FUNNET, journalpostMetadata.getHoveddokument().getTittel()));
        return ok(resultat).build();
    }

    private boolean temakodeErBidrag(String temakode) {
        return TEMAKODE_BIDRAG.equals(temakode);
    }

    private boolean finnesIkkeIJoarkPaBruker(DokumentMetadata journalpostMetadata) {
        return !journalpostMetadata.isErJournalfort();
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
}
