package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattEnhetListeFaultGOSYSGeneriskMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg;
import org.apache.wicket.util.cookies.CookieUtils;

import javax.inject.Inject;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

public class ValgtEnhetService {

    @Inject
    private GOSYSNAVansatt ansattWS;

    private static CookieUtils cookieUtils = new CookieUtils();

    private String hentDefaultEnhetsId() {
        ASBOGOSYSNAVAnsatt hentNAVAnsattEnhetListeRequest = new ASBOGOSYSNAVAnsatt();
        hentNAVAnsattEnhetListeRequest.setAnsattId(getSubjectHandler().getUid());
        try {
            return (ansattWS.hentNAVAnsattEnhetListe(hentNAVAnsattEnhetListeRequest).getNAVEnheter().get(0).getEnhetsId());
        } catch (HentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg | HentNAVAnsattEnhetListeFaultGOSYSGeneriskMsg hentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg) {
            hentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg.printStackTrace();
            return null;
        }
    }

    public String getEnhetId() {
        if (cookieUtils.load(brukerSpesifikCookieId()) == null) {
            return hentDefaultEnhetsId();
        } else {
            return cookieUtils.load(brukerSpesifikCookieId());
        }
    }

    private String brukerSpesifikCookieId() {
        return "saksbehandlerinstillinger-" + getSubjectHandler().getUid();
    }
}
