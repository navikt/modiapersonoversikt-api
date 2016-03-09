package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.sak.service.interfaces.TilgangskontrollService;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.ModiaSakstema;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.*;
import static no.nav.modig.security.tilgangskontroll.utils.RequestUtils.forRequest;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet.ENHET_ID;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.slf4j.LoggerFactory.getLogger;

public class TilgangskontrollServiceImpl implements TilgangskontrollService {

    @Inject
    @Named("pep")
    private EnforcementPoint pep;

    @Inject
    private AnsattService ansattService;

    private static final Logger logger = getLogger(TilgangskontrollService.class);

    public boolean harSaksbehandlerTilgangTilDokument(String sakstemakode, String enhet) {
        return harEnhetTilgangTilTema(sakstemakode, enhet);
    }

    @Override
    public List<ModiaSakstema> harSaksbehandlerTilgangTilSakstema(List<Sakstema> sakstemaList, String valgtEnhet) {
        return sakstemaList.stream()
                .map(sakstema -> createModiaSakstema(sakstema, valgtEnhet))
                .collect(Collectors.toList());
    }

    private ModiaSakstema createModiaSakstema(Sakstema sakstema, String valgtEnhet) {
        return new ModiaSakstema(sakstema)
                .withTilgang(harEnhetTilgangTilTema(sakstema.temakode, valgtEnhet));
    }

    private boolean harEnhetTilgangTilTema(String temakode, String valgtEnhet) {
        PolicyRequest temagruppePolicyRequest = forRequest(
                actionId("temagruppe"),
                resourceId(""),
                subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(valgtEnhet)),
                resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:tema", defaultString(temakode))
        );
        if (isNotBlank(temakode) && !pep.hasAccess(temagruppePolicyRequest)) {
            logger.warn("Saksbehandler med ident '{}' og valgt enhet '{}' har ikke tilgang til tema '{}'",
                    getSubjectHandler().getUid(),
                    valgtEnhet,
                    temakode);
            return false;
        }
        return true;
    }
    public Optional<Response> harGodkjentEnhet(String valgtEnhet, HttpServletRequest request) {
        List<String> enhetsListe = on(ansattService.hentEnhetsliste()).map(ENHET_ID).collect();

        if (!enhetsListe.contains(valgtEnhet)) {
            logger.warn("{} har ikke tilgang til enhet {}.", getSubjectHandler().getUid(), valgtEnhet);
            return of(Response.status(Response.Status.UNAUTHORIZED).build());
        }
        return empty();
    }

}
