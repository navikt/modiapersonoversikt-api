package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSFinnNAVEnhetRequest;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.FinnNAVEnhetFaultGOSYSGeneriskMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.EnhetService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.AnsattService.TIL_ANSATTENHET;

public class DefaultEnhetService implements EnhetService {

    @Inject
    private GOSYSNAVOrgEnhet enhetWS;

    private enum EnhetType {
        EN, FYLKE, SPESEN, ENGR, GR
    }

    public List<AnsattEnhet> hentAlleEnheter() {
        List<AnsattEnhet> enheter = new ArrayList<>();

        enheter.addAll(finnEnheterMedType(EnhetType.EN));
        enheter.addAll(finnEnheterMedType(EnhetType.FYLKE));
        enheter.addAll(finnEnheterMedType(EnhetType.SPESEN));
        enheter.addAll(finnEnheterMedType(EnhetType.ENGR));
        enheter.addAll(finnEnheterMedType(EnhetType.GR));

        return on(enheter).collect(ENHET_ID_STIGENDE);
    }

    private List<AnsattEnhet> finnEnheterMedType(EnhetType enhetType) {
        try {
            ASBOGOSYSFinnNAVEnhetRequest enhetRequest = new ASBOGOSYSFinnNAVEnhetRequest();
            enhetRequest.setTypeEnhet(enhetType.name());

            return on(enhetWS.finnNAVEnhet(enhetRequest).getNAVEnheter()).map(TIL_ANSATTENHET).collect();
        } catch (FinnNAVEnhetFaultGOSYSGeneriskMsg finnNAVEnhetFaultGOSYSGeneriskMsg) {
            finnNAVEnhetFaultGOSYSGeneriskMsg.printStackTrace();
            return emptyList();
        }
    }

    private static final Comparator<AnsattEnhet> ENHET_ID_STIGENDE = new Comparator<AnsattEnhet>() {
        @Override
        public int compare(AnsattEnhet o1, AnsattEnhet o2) {
            return o1.enhetId.compareTo(o2.enhetId);
        }
    };
}
