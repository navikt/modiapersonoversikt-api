package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattEnhetListeFaultGOSYSGeneriskMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.AnsattEnhet;
import org.apache.commons.collections15.Transformer;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;

public class AnsattService {

    @Inject
    private GOSYSNAVansatt ansattWS;

    public List<AnsattEnhet> hentEnhetsliste() {
        ASBOGOSYSNAVAnsatt hentNAVAnsattEnhetListeRequest = new ASBOGOSYSNAVAnsatt();
        hentNAVAnsattEnhetListeRequest.setAnsattId(getSubjectHandler().getUid());
        try {
            return on(ansattWS.hentNAVAnsattEnhetListe(hentNAVAnsattEnhetListeRequest).getNAVEnheter()).map(TIL_ANSATTENHET).collect();
        } catch (HentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg | HentNAVAnsattEnhetListeFaultGOSYSGeneriskMsg e) {
            throw new RuntimeException(e);
        }
    }

    protected static final Transformer<ASBOGOSYSNavEnhet, AnsattEnhet> TIL_ANSATTENHET = new Transformer<ASBOGOSYSNavEnhet, AnsattEnhet>() {
        @Override
        public AnsattEnhet transform(ASBOGOSYSNavEnhet asbogosysNavEnhet) {
            return new AnsattEnhet(asbogosysNavEnhet.getEnhetsId(), asbogosysNavEnhet.getEnhetsNavn());
        }
    };
}
