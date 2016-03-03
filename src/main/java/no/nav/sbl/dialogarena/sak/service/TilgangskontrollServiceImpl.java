package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.sbl.dialogarena.sak.service.interfaces.TilgangskontrollService;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.ModiaSakstema;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.TjenesteResultatWrapper;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.*;
import static no.nav.modig.security.tilgangskontroll.utils.RequestUtils.forRequest;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.slf4j.LoggerFactory.getLogger;

public class TilgangskontrollServiceImpl implements TilgangskontrollService {

    @Inject
    @Named("pep")
    private EnforcementPoint pep;

    private static final Logger logger = getLogger(TilgangskontrollService.class);

    public TjenesteResultatWrapper harSaksbehandlerTilgangTilDokument(String journalpostId, String fnr, String sakstemakode) {
        return new TjenesteResultatWrapper(true);
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

}
