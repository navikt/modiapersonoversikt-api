package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import kotlin.Pair;
import no.nav.common.auth.SubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Person;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.CookieUtil;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll;
import no.nav.sbl.dialogarena.naudit.AuditResources.Saksbehandler;
import no.nav.sbl.dialogarena.naudit.Audit;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.List;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils.hentValgtEnhet;
import static no.nav.sbl.dialogarena.naudit.Audit.Action.*;

@Path("/hode")
@Produces(APPLICATION_JSON)
public class HodeController {

    @Inject
    private LDAPService ldapService;

    @Inject
    private AnsattService ansattService;

    @Inject
    private OrganisasjonEnhetV2Service organisasjonEnhetService;

    @Inject
    Tilgangskontroll tilgangskontroll;

    class Me {
        public final String ident, navn, fornavn, etternavn, enhetId, enhetNavn;

        public Me(String ident, String fornavn, String etternavn, String enhetId, String enhetNavn) {
            this.ident = ident;
            this.fornavn = fornavn;
            this.etternavn = etternavn;
            this.navn = fornavn + " " + etternavn;
            this.enhetId = enhetId;
            this.enhetNavn = enhetNavn;
        }
    }

    class Enhet {
        public final String enhetId, navn;

        public Enhet(String enhetId, String navn) {
            this.enhetId = enhetId;
            this.navn = navn;
        }
    }

    class Enheter {
        public final String ident;
        public final List<Enhet> enhetliste;

        public Enheter(String ident, List<Enhet> enhetliste) {
            this.ident = ident;
            this.enhetliste = enhetliste;
        }
    }

    @GET
    @Path("/me")
    public Me hentSaksbehandler(@Context HttpServletRequest request) {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.describe(READ, Saksbehandler.NavnOgEnheter), () -> {
                    String ident = SubjectHandler.getIdent().orElseThrow(() -> new RuntimeException("Fant ikke ident"));
                    Pair<String, String> saksbehandler = hentSaksbehandlerNavn();
                    String enhetId = hentValgtEnhet(request);
                    String enhetNavn = organisasjonEnhetService.hentEnhetGittEnhetId(enhetId, OrganisasjonEnhetV2Service.WSOppgavebehandlerfilter.UFILTRERT)
                            .map((enhet) -> enhet.enhetNavn)
                            .orElse("[Ukjent enhetId: " + enhetId + "]");
                    return new Me(ident, saksbehandler.getFirst(), saksbehandler.getSecond(), enhetId, enhetNavn);
                });
    }

    @GET
    @Path("/enheter")
    public Enheter hentEnheter() {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.describe(READ, Saksbehandler.Enheter), () -> {
                    String ident = SubjectHandler.getIdent().orElseThrow(() -> new RuntimeException("Fant ikke ident"));
                    List<Enhet> enheter = ansattService.hentEnhetsliste()
                            .stream()
                            .map((ansattEnhet) -> new Enhet(ansattEnhet.enhetId, ansattEnhet.enhetNavn))
                            .collect(Collectors.toList());

                    return new Enheter(ident, enheter);
                });
    }

    @POST
    @Path("/velgenhet")
    public String settValgtEnhet(@Context HttpServletResponse response, String enhetId) {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.describe(UPDATE, Saksbehandler.ValgtEnhet, new Pair<>("enhetId", enhetId)), () -> {
                    CookieUtil.setSaksbehandlersValgteEnhet(response, enhetId);
                    return enhetId;
                });
    }

    private Pair<String, String> hentSaksbehandlerNavn() {
        Person saksbehandler = SubjectHandler.getIdent()
                .map(ldapService::hentSaksbehandler)
                .orElseThrow(() -> new RuntimeException("Fant ikke ident til saksbehandler"));
        return new Pair<>(saksbehandler.fornavn, saksbehandler.etternavn);
    }
}
