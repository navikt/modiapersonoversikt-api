package no.nav.sbl.modiabrukerdialog.pip.journalforing.support;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.ASBOGOSYSFagomrade;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.HentNAVEnhetFaultGOSYSGeneriskMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.HentNAVEnhetFaultGOSYSNAVEnhetIkkeFunnetaMsg;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SaksbehandlerInnstillingerService;
import no.nav.sbl.modiabrukerdialog.pip.journalforing.JournalfortTemaAttributeLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.emptySet;

public class DefaultJournalfortTemaAttributeLocatorDelegate implements JournalfortTemaAttributeLocatorDelegate {

    private static Logger logger = LoggerFactory.getLogger(JournalfortTemaAttributeLocator.class);
    private GOSYSNAVOrgEnhet enhetService;
    private SaksbehandlerInnstillingerService saksbehandlerService;

    public DefaultJournalfortTemaAttributeLocatorDelegate(GOSYSNAVOrgEnhet enhetService, SaksbehandlerInnstillingerService saksbehandlerService) {
        this.enhetService = enhetService;
        this.saksbehandlerService = saksbehandlerService;
    }

    @Override
    public Set<String> getTemagrupperForAnsattesValgteEnhet() {
        String saksbehandlerValgtEnhet = saksbehandlerService.getSaksbehandlerValgtEnhet();

        try {
            ASBOGOSYSNavEnhet navEnhetRequest = new ASBOGOSYSNavEnhet();
            navEnhetRequest.setEnhetsId(saksbehandlerValgtEnhet);

            ASBOGOSYSNavEnhet navEnhet = enhetService.hentNAVEnhet(navEnhetRequest);

            Set<String> temaSet = new HashSet<>();

            for (ASBOGOSYSFagomrade fagomrade : navEnhet.getFagomrader()) {
                temaSet.add(fagomrade.getFagomradeKode());
            }

            return temaSet;
        } catch (HentNAVEnhetFaultGOSYSNAVEnhetIkkeFunnetaMsg e) {
            logger.warn("Fant ikke enhet med enhetsId {}.", saksbehandlerValgtEnhet, e);
            return emptySet();
        } catch (HentNAVEnhetFaultGOSYSGeneriskMsg e) {
            logger.warn("Feil oppsto under henting av enhet med enhetsId {}.", saksbehandlerValgtEnhet, e);
            return emptySet();
        }
    }
}
