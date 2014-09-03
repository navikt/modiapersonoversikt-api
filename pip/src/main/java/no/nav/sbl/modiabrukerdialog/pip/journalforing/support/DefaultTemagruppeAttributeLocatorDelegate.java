package no.nav.sbl.modiabrukerdialog.pip.journalforing.support;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.ASBOGOSYSFagomrade;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.HentNAVEnhetFaultGOSYSGeneriskMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.HentNAVEnhetFaultGOSYSNAVEnhetIkkeFunnetaMsg;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SaksbehandlerInnstillingerService;
import no.nav.sbl.modiabrukerdialog.pip.journalforing.TemagruppeAttributeLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.emptySet;

public class DefaultTemagruppeAttributeLocatorDelegate implements TemagruppeAttributeLocatorDelegate {

    private static Logger logger = LoggerFactory.getLogger(TemagruppeAttributeLocator.class);
    private GOSYSNAVOrgEnhet enhetService;
    private SaksbehandlerInnstillingerService saksbehandlerService;

    public DefaultTemagruppeAttributeLocatorDelegate(GOSYSNAVOrgEnhet enhetService, SaksbehandlerInnstillingerService saksbehandlerService) {
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

            Set<String> temagruppeSet = new HashSet<>();

            for (ASBOGOSYSFagomrade fagomrade : navEnhet.getFagomrader()) {
                temagruppeSet.add(fagomrade.getFagomradeKode());
            }

            return temagruppeSet;
        } catch (HentNAVEnhetFaultGOSYSNAVEnhetIkkeFunnetaMsg e) {
            logger.warn("Fant ikke enhet med enhetsId {}.", saksbehandlerValgtEnhet, e);
            return emptySet();
        } catch (HentNAVEnhetFaultGOSYSGeneriskMsg e) {
            logger.warn("Feil oppsto under henting av enhet med enhetsId {}.", saksbehandlerValgtEnhet, e);
            return emptySet();
        }
    }
}
