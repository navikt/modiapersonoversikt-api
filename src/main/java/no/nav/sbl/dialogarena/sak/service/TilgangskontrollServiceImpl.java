package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.sak.service.interfaces.TilgangskontrollService;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.TjenesteResultatWrapper;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.*;
import static no.nav.modig.security.tilgangskontroll.utils.RequestUtils.forRequest;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet.ENHET_ID;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils.hentValgtEnhet;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Feilmelding.*;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.slf4j.LoggerFactory.getLogger;

public class TilgangskontrollServiceImpl implements TilgangskontrollService {

    @Inject
    @Named("pep")
    private EnforcementPoint pep;
    @Inject
    private AnsattService ansattService;

    public final static String TEMAKODE_BIDRAG = "BID";
    private static final Logger logger = getLogger(TilgangskontrollService.class);


    public TjenesteResultatWrapper harSaksbehandlerTilgangTilDokument(HttpServletRequest request, DokumentMetadata journalpostMetadata) {
        String valgtEnhet = hentValgtEnhet(request);
        String temakode = journalpostMetadata.getTemakode();

        if (!harGodkjentEnhet(request) || !harEnhetTilgangTilTema(temakode, valgtEnhet)) {
            return new TjenesteResultatWrapper(SAKSBEHANDLER_IKKE_TILGANG);
        } else if (temakodeErBidrag(temakode)) {
            return new TjenesteResultatWrapper(TEMAKODE_ER_BIDRAG);
        } else if (erJournalfortPaAnnetTema(temakode, journalpostMetadata)) {
            return new TjenesteResultatWrapper(JOURNALFORT_ANNET_TEMA, journalfortAnnetTemaEktraFeilInfo(journalpostMetadata.getTemakode()));
        } else if (journalpostMetadata.getFeilWrapper().getInneholderFeil()) {
            return new TjenesteResultatWrapper(journalpostMetadata.getFeilWrapper().getFeilmelding());
        }

        return new TjenesteResultatWrapper(TRUE);
    }

    public boolean harGodkjentEnhet(HttpServletRequest request) {
        String valgtEnhet = hentValgtEnhet(request);
        List<String> enhetsListe = on(ansattService.hentEnhetsliste()).map(ENHET_ID).collect();

        if (!enhetsListe.contains(valgtEnhet)) {
            logger.warn("{} har ikke tilgang til enhet {}.", getSubjectHandler().getUid(), valgtEnhet);
            return false;
        }
        return true;
    }

    public boolean harEnhetTilgangTilTema(String temakode, String valgtEnhet) {
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

    private boolean temakodeErBidrag(String temakode) {
        return TEMAKODE_BIDRAG.equals(temakode);
    }

    private boolean erJournalfortPaAnnetTema(String temakode, DokumentMetadata dokumentMetadata) {
        return temakode != null && !dokumentMetadata.getTemakode().equals(temakode);
    }

    private Map journalfortAnnetTemaEktraFeilInfo(String temanavn) {
        Map map = new HashMap<>();
        map.put("temanavn", temanavn);
        return map;
    }
}
