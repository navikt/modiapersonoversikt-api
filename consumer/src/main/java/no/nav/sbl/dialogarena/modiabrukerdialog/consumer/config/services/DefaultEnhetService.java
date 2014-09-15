package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSHentNAVEnhetListeRequest;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.HentNAVEnhetListeFaultGOSYSGeneriskMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.HentNAVEnhetListeFaultGOSYSNAVEnhetIkkeFunnetMsg;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.EnhetService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SaksbehandlerInnstillingerService;

import javax.inject.Inject;
import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.AnsattService.TIL_ANSATTENHET;

public class DefaultEnhetService implements EnhetService {

    @Inject
    private GOSYSNAVOrgEnhet enhetWS;

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    public List<AnsattEnhet> hentAlleEnheter() {
        ASBOGOSYSHentNAVEnhetListeRequest req = new ASBOGOSYSHentNAVEnhetListeRequest();
        ASBOGOSYSNavEnhet enhet = new ASBOGOSYSNavEnhet();
        enhet.setEnhetsId(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet());
        req.setNAVEnhet(enhet);

        try {
            return on(enhetWS.hentNAVEnhetListe(req).getNAVEnheter())
                    .map(TIL_ANSATTENHET)
                    .collect();
        } catch (HentNAVEnhetListeFaultGOSYSGeneriskMsg | HentNAVEnhetListeFaultGOSYSNAVEnhetIkkeFunnetMsg ex) {
            ex.printStackTrace();
        }
        return emptyList();
    }
}
