package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.HentDokumentResultat;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.WSJournalpost;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.WSJournalstatuser;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSAktoer;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSak;
import org.apache.commons.collections15.Predicate;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.*;
import static no.nav.modig.security.tilgangskontroll.utils.RequestUtils.forRequest;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.HentDokumentResultat.Feilmelding.*;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.slf4j.LoggerFactory.getLogger;

public class TilgangskontrollServiceImpl implements TilgangskontrollService {

    @Inject
    private GSakService gSakService;

    @Inject
    private JoarkService joarkService;

    @Inject
    @Named("pep")
    private EnforcementPoint pep;

    private static final Logger logger = getLogger(TilgangskontrollService.class);

    private final String JOURNALPOST_STATUS_JOURNALFORT = "J";
    private final String JOURNALPOST_STATUS_UTGAAR = "U";
    private final String JOURNALPOST_STATUS_UKJENT_BRUKER = "UB";

    public HentDokumentResultat harSaksbehandlerTilgangTilDokument(String journalpostId, String fnr) {
        return sjekkTilgang(journalpostId, fnr);
    }

    private HentDokumentResultat sjekkTilgang(String journalpostId, String fnr) {
        if (!harJournalpostId(journalpostId)) {
            return new HentDokumentResultat(false, IKKE_JOURNALFORT);
        }

        WSJournalpost journalpost = hentJournalpost(journalpostId);
        if (!erJournalfort(journalpost)) {
            if (harStatusUtgaar(journalpost)) {
                return new HentDokumentResultat(false, STATUS_UTGAAR);
            } else if (harUkjentBruker(journalpost)) {
                return new HentDokumentResultat(false, UKJENT_BRUKER);
            } else {
                return new HentDokumentResultat(false, IKKE_JOURNALFORT);
            }
        } else if (erFeilregistrert(journalpost)) {
            return new HentDokumentResultat(false, FEILREGISTRERT);
        } else if (!erInnsenderSakspart(journalpost, fnr)) {
            return new HentDokumentResultat(false, IKKE_SAKSPART);
        } else if (!harEnhetTilgangTilTema(journalpost)) {
            return new HentDokumentResultat(false, INGEN_TILGANG);
        }
        return new HentDokumentResultat(true);
    }

    private boolean harJournalpostId(String journalpostid) {
        return journalpostid != null;
    }

    private WSJournalpost hentJournalpost(String journalpostId) {
        return joarkService.hentJournalpost(journalpostId);
    }

    private boolean erJournalfort(WSJournalpost journalpost) {
        WSJournalstatuser journalstatus = journalpost.getJournalstatus();
        boolean erJournalfort = journalstatus != null && JOURNALPOST_STATUS_JOURNALFORT.equalsIgnoreCase(journalstatus.getValue());

        if (!erJournalfort) {
            logger.warn("Journalposten med id '{}' er ikke journalført.", journalpost.getJournalpostId());
        }
        return erJournalfort;
    }

    private boolean harStatusUtgaar(WSJournalpost journalpost) {
        WSJournalstatuser journalstatus = journalpost.getJournalstatus();
        boolean harStatusUtgaar = journalstatus != null && JOURNALPOST_STATUS_UTGAAR.equalsIgnoreCase(journalstatus.getValue());

        if (harStatusUtgaar) {
            logger.warn("Journalposten med id '{}' har status utgår (status på journalpost: '').", journalpost.getJournalpostId(), journalstatus);
        }
        return harStatusUtgaar;
    }

    private boolean harUkjentBruker(WSJournalpost journalpost) {
        WSJournalstatuser journalstatus = journalpost.getJournalstatus();
        boolean harUkjentBruker = journalstatus != null && JOURNALPOST_STATUS_UKJENT_BRUKER.equalsIgnoreCase(journalstatus.getValue());

        if (harUkjentBruker) {
            logger.warn("Journalposten med id '{}' har ukjent bruker (status på journalpost: '').", journalpost.getJournalpostId(), journalstatus);
        }
        return harUkjentBruker;
    }

    private boolean erFeilregistrert(WSJournalpost journalpost) {
        if (journalpost.getGjelderSak() == null) {
            logger.warn("Det eksisterer ingen sak knyttet til journalpost med id {}", journalpost.getJournalpostId());
            return false;
        }

        boolean feilregistrert = journalpost.getGjelderSak().isErFeilregistrert();
        if (feilregistrert) {
            logger.warn("Journalposten med id '{}' er feilregistrert.", journalpost.getJournalpostId());
        }
        return feilregistrert;
    }

    private boolean erInnsenderSakspart(WSJournalpost journalpost, String fnr) {
        if (journalpost.getGjelderSak() == null) {
            logger.warn("Det eksisterer ingen sak knyttet til journalpost med id {}", journalpost.getJournalpostId());
            return false;
        }

        String sakId = journalpost.getGjelderSak().getSakId();
        WSSak gSak = hentSak(sakId);
        boolean erInnsenderSakspart = on(gSak.getGjelderBrukerListe()).exists(aktoerMedFnr(fnr));

        if (!erInnsenderSakspart) {
            logger.warn("Innsender med fnr '{}' er ikke sakspart for sak med id '{}'.", fnr, sakId);
        }
        return erInnsenderSakspart;
    }

    private boolean harEnhetTilgangTilTema(WSJournalpost journalpost) {
        PolicyRequest temagruppePolicyRequest = forRequest(
                actionId("temagruppe"),
                resourceId(""),
                resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:tema", defaultString(journalpost.getArkivtema().getValue()))
        );
        if (isNotBlank(journalpost.getArkivtema().getValue()) && !pep.hasAccess(temagruppePolicyRequest)) {
            logger.warn("Saksbehandler med ident '{}' har ikke tilgang til tema '{}'", getSubjectHandler().getUid(), journalpost.getArkivtema().getValue());
            return false;
        }
        return true;
    }

    private WSSak hentSak(String sakId) {
        return gSakService.hentSak(sakId);
    }

    private static Predicate<WSAktoer> aktoerMedFnr(final String fnr) {
        return new Predicate<WSAktoer>() {
            @Override
            public boolean evaluate(WSAktoer wsAktoer) {
                return wsAktoer.getIdent().equals(fnr);
            }
        };
    }
}
