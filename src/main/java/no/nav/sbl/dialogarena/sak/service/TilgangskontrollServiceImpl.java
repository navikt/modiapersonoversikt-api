package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sak.service.interfaces.TilgangskontrollService;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.TjenesteResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.service.GsakSakerService;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;

import static org.slf4j.LoggerFactory.getLogger;

public class TilgangskontrollServiceImpl implements TilgangskontrollService {

    @Inject
    private GsakSakerService gSakService;

    @Inject
    @Named("pep")
    private EnforcementPoint pep;

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    private static final Logger logger = getLogger(TilgangskontrollService.class);

    private static final String JOURNALPOST_STATUS_JOURNALFORT = "J";
    private static final String JOURNALPOST_STATUS_UTGAAR = "U";
    private static final String JOURNALPOST_STATUS_UKJENT_BRUKER = "UB";
    private static final String SAKSTEMAKODE_PENSJON = "PEN";
    private static final String SAKSTEMAKODE_UFORETRYD = "UFO";

    public TjenesteResultatWrapper harSaksbehandlerTilgangTilDokument(String journalpostId, String fnr, String sakstemakode) {
        return sjekkTilgang(journalpostId, fnr, sakstemakode);
    }

    private TjenesteResultatWrapper sjekkTilgang(String journalpostId, String fnr, String sakstemakode) {
//        if (!harJournalpostId(journalpostId)) {
//            return new HentDokumentResultat(false, IKKE_JOURNALFORT);
//        }
//
//        if (erSaksTemaKodeSomIkkeHarVedleggIJoark(sakstemakode)) {
//            return new HentDokumentResultat(false, UGYLDIG_SAKSTEMA);
//        }
//
//        //TODO Her må det gjøres større endringer
////        WSJournalpost journalpost = null;//hentJournalpost(journalpostId);
//        if (erFeilregistrert(journalpost)) {
//            return new HentDokumentResultat(false, FEILREGISTRERT);
//        } else if (!erJournalfort(journalpost)) {
//            if (harStatusUtgaar(journalpost)) {
//                return new HentDokumentResultat(false, STATUS_UTGAAR);
//            } else if (harUkjentBruker(journalpost)) {
//                return new HentDokumentResultat(false, UKJENT_BRUKER);
//            } else {
//                return new HentDokumentResultat(false, IKKE_JOURNALFORT);
//            }
//        } else if (!erInnsenderSakspart(journalpost, fnr)) {
//            return new HentDokumentResultat(false, IKKE_SAKSPART, hentSakspart(journalpost));
//        } else if (!harEnhetTilgangTilTema(journalpost)) {
//            return new HentDokumentResultat(false, INGEN_TILGANG);
//        }
        return new TjenesteResultatWrapper(true);
    }

    private boolean erSaksTemaKodeSomIkkeHarVedleggIJoark(String sakstemakode) {
        return SAKSTEMAKODE_PENSJON.equalsIgnoreCase(sakstemakode) || SAKSTEMAKODE_UFORETRYD.equalsIgnoreCase(sakstemakode);
    }

    private boolean harJournalpostId(String journalpostid) {
        return journalpostid != null;
    }

//    private WSJournalpost hentJournalpost(String journalpostId) {
////        return joarkService.hentJournalpost(journalpostId);
//    }

//    private boolean erJournalfort(WSJournalpost journalpost) {
//        WSJournalstatuser journalstatus = journalpost.getJournalstatus();
//        boolean erJournalfort = journalstatus != null && JOURNALPOST_STATUS_JOURNALFORT.equalsIgnoreCase(journalstatus.getValue());
//
//        if (!erJournalfort) {
//            logger.warn("Journalposten med id '{}' er ikke journalført.", journalpost.getJournalpostId());
//        }
//        return erJournalfort;
//    }
//
//    private boolean harStatusUtgaar(WSJournalpost journalpost) {
//        WSJournalstatuser journalstatus = journalpost.getJournalstatus();
//        boolean harStatusUtgaar = journalstatus != null && JOURNALPOST_STATUS_UTGAAR.equalsIgnoreCase(journalstatus.getValue());
//
//        if (harStatusUtgaar) {
//            logger.warn("Journalposten med id '{}' har status utgår (status på journalpost: '').", journalpost.getJournalpostId(), journalstatus);
//        }
//        return harStatusUtgaar;
//    }
//
//    private boolean harUkjentBruker(WSJournalpost journalpost) {
//        WSJournalstatuser journalstatus = journalpost.getJournalstatus();
//        boolean harUkjentBruker = journalstatus != null && JOURNALPOST_STATUS_UKJENT_BRUKER.equalsIgnoreCase(journalstatus.getValue());
//
//        if (harUkjentBruker) {
//            logger.warn("Journalposten med id '{}' har ukjent bruker (status på journalpost: '').", journalpost.getJournalpostId(), journalstatus);
//        }
//        return harUkjentBruker;
//    }
//
//    private boolean erFeilregistrert(WSJournalpost journalpost) {
//        if (journalpost.getGjelderSak() == null) {
//            logger.warn("Det eksisterer ingen sak knyttet til journalpost med id {}", journalpost.getJournalpostId());
//            return false;
//        }
//
//        boolean feilregistrert = journalpost.getGjelderSak().isErFeilregistrert();
//        if (feilregistrert) {
//            logger.warn("Journalposten med id '{}' er feilregistrert.", journalpost.getJournalpostId());
//        }
//        return feilregistrert;
//    }
//
//    private boolean erInnsenderSakspart(WSJournalpost journalpost, String fnr) {
//        if (journalpost.getGjelderSak() == null) {
//            logger.warn("Det eksisterer ingen sak knyttet til journalpost med id {}", journalpost.getJournalpostId());
//            return false;
//        }
//
//        String sakId = journalpost.getGjelderSak().getSakId();
//        WSSak gSak = hentSak(sakId);
//        boolean erInnsenderSakspart = on(gSak.getGjelderBrukerListe()).exists(aktoerMedFnr(fnr));
//
//        if (!erInnsenderSakspart) {
//            logger.warn("Innsender med fnr '{}' er ikke sakspart for sak med id '{}'.", fnr, sakId);
//        }
//        return erInnsenderSakspart;
//    }
//
//    private WSSak hentSak(String sakId) {
//        return gSakService.hentSak(sakId);
//    }
//
//    private static Predicate<WSAktoer> aktoerMedFnr(final String fnr) {
//        return new Predicate<WSAktoer>() {
//            @Override
//            public boolean evaluate(WSAktoer wsAktoer) {
//                return wsAktoer.getIdent().equals(fnr);
//            }
//        };
//    }
//
//    private String hentSakspart(WSJournalpost journalpost) {
//        WSSak sak = hentSak(journalpost.getGjelderSak().getSakId());
//        return on(sak.getGjelderBrukerListe()).map(AKTOER_TIL_IDENT).head().getOrElse("");
//    }
//
//    private static Transformer<WSAktoer, String> AKTOER_TIL_IDENT = new Transformer<WSAktoer, String>() {
//        @Override
//        public String transform(WSAktoer wsAktoer) {
//            return wsAktoer.getIdent();
//        }
//    };
//
//    private boolean harEnhetTilgangTilTema(WSJournalpost journalpost) {
//        PolicyRequest temagruppePolicyRequest = forRequest(
//                actionId("temagruppe"),
//                resourceId(""),
//                subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet())),
//                resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:tema", defaultString(journalpost.getArkivtema().getValue()))
//        );
//        if (isNotBlank(journalpost.getArkivtema().getValue()) && !pep.hasAccess(temagruppePolicyRequest)) {
//            logger.warn("Saksbehandler med ident '{}' og valgt enhet '{}' har ikke tilgang til tema '{}'",
//                    getSubjectHandler().getUid(),
//                    saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet(),
//                    journalpost.getArkivtema().getValue());
//            return false;
//        }
//        return true;
//    }

}
