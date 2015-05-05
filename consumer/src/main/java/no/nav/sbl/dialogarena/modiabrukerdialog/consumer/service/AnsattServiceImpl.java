package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.*;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Ansatt;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.AnsattService;
import org.apache.commons.collections15.Transformer;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;

public class AnsattServiceImpl implements AnsattService {

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

    public String hentAnsattNavn(String ident) {
        try {
            ASBOGOSYSNAVAnsatt ansattRequest = new ASBOGOSYSNAVAnsatt();
            ansattRequest.setAnsattId(ident);
            return ansattWS.hentNAVAnsatt(ansattRequest).getAnsattNavn();
        } catch (HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg | HentNAVAnsattFaultGOSYSGeneriskfMsg e) {
            throw new RuntimeException("Noe gikk galt ved henting av ansatt med ident " + ident, e);
        }
    }

    @Override
    public List<Ansatt> ansatteForEnhet(AnsattEnhet enhet) {
        ASBOGOSYSNavEnhet request = new ASBOGOSYSNavEnhet();
        request.setEnhetsId(enhet.enhetId);
        request.setEnhetsNavn(enhet.enhetNavn);
        try {
            return on(ansattWS.hentNAVAnsattListe(request).getNAVAnsatte()).map(new Transformer<ASBOGOSYSNAVAnsatt, Ansatt>() {
                @Override
                public Ansatt transform(ASBOGOSYSNAVAnsatt ansatt) {
                    return new Ansatt(ansatt.getFornavn(), ansatt.getEtternavn(), ansatt.getAnsattId());
                }
            }).collect();
        } catch (Exception e) {
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
