package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.brukerdialog.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Sakstema;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.TjenesteResultatWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.TilgangskontrollService;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static java.util.stream.Collectors.toList;
import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;
import static no.nav.brukerdialog.security.tilgangskontroll.utils.AttributeUtils.*;
import static no.nav.brukerdialog.security.tilgangskontroll.utils.RequestUtils.forRequest;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils.hentValgtEnhet;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Feilmelding.*;
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

    public TjenesteResultatWrapper harSaksbehandlerTilgangTilDokument(HttpServletRequest request, DokumentMetadata journalpostMetadata, String fnr, String urlTemakode) {
        String temakode = journalpostMetadata.getTemakode();

        if (temakodeErBidrag(temakode)) {
            return new TjenesteResultatWrapper(TEMAKODE_ER_BIDRAG);
        } else if (erJournalfortPaAnnetTema(urlTemakode, journalpostMetadata)) {
            return new TjenesteResultatWrapper(JOURNALFORT_ANNET_TEMA, journalfortAnnetTemaEktraFeilInfo(journalpostMetadata.getTemakodeVisning(), fnr));
        } else if (!journalpostMetadata.isErJournalfort()) {
            return new TjenesteResultatWrapper(IKKE_JOURNALFORT, ikkeJournalfortEkstraFeilInfo(fnr));
        } else if (journalpostMetadata.getFeilWrapper().getInneholderFeil()) {
            return new TjenesteResultatWrapper(journalpostMetadata.getFeilWrapper().getFeilmelding());
        }

        return new TjenesteResultatWrapper(TRUE);
    }

    public boolean harGodkjentEnhet(HttpServletRequest request) {
        String valgtEnhet = hentValgtEnhet(request);
        List<String> enhetsListe = ansattService.hentEnhetsliste().stream().map(ansattEnhet -> ansattEnhet.enhetId).collect(toList());

        valgtEnhet = settEnhetDersomCookieIkkeErSatt(valgtEnhet, enhetsListe);

        if (!enhetsListe.contains(valgtEnhet)) {
            logger.warn("{} har ikke tilgang til enhet {}.", getSubjectHandler().getUid(), valgtEnhet);
            return false;
        }
        return true;
    }

    private String settEnhetDersomCookieIkkeErSatt(String valgtEnhet, List<String> enhetsListe) {
        if ("".equals(valgtEnhet)) {
            valgtEnhet = enhetsListe.get(0);
        }
        return valgtEnhet;
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

    public void markerIkkeJournalforte(List<Sakstema> sakstemaList) {
        sakstemaList
                .forEach(sakstema -> sakstema.dokumentMetadata
                        .stream()
                        .filter(dokumentMetadata -> !dokumentMetadata.isErJournalfort())
                        .map(dokumentMetadata -> dokumentMetadata.withFeilWrapper(IKKE_JOURNALFORT))
                        .collect(toList()));
    }

    private boolean temakodeErBidrag(String temakode) {
        return TEMAKODE_BIDRAG.equals(temakode);
    }

    private boolean erJournalfortPaAnnetTema(String temakode, DokumentMetadata dokumentMetadata) {
        return temakode != null && !dokumentMetadata.getTemakode().equals(temakode);
    }

    private Map journalfortAnnetTemaEktraFeilInfo(String temanavn, String fnr) {
        Map map = new HashMap<>();
        map.put("temanavn", temanavn);
        map.put("fnr", fnr);
        return map;
    }

    private Map ikkeJournalfortEkstraFeilInfo(String fnr) {
        Map map = new HashMap<>();
        map.put("fnr", fnr);
        return map;
    }
}
