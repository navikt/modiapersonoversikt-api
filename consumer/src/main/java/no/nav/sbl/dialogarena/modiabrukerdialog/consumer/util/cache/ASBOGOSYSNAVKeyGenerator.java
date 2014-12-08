package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.cache;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSHentNAVAnsattFagomradeListeRequest;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSFinnArenaNAVEnhetListeRequest;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSFinnNAVEnhetRequest;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSHentNAVEnhetListeRequest;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSHentSpesialEnhetTilPersonRequest;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;

import java.lang.reflect.Method;

/**
 * Klasse for å lage cachekey som også tar høyde for
 * brukers ident, slik at ikke tilgang overstyres av
 * cachede entries. NB! Extender DefaultKeyGenerator
 * som er standard i nåværende versjon av Spring.
 * Denne er deprecated pga potensielle hash-kollisjoner
 * i nyeste versjon av Spring.
 */
public class ASBOGOSYSNAVKeyGenerator extends AutentisertBrukerKeyGenerator {

    public ASBOGOSYSNAVKeyGenerator() {
        super();
    }

    @Override
    public Object generate(Object target, Method method, Object... params) {
        return super.generate(target, method, getCacheKey(target, method, params));
    }

    private Object getCacheKey(Object target, Method method, Object... params) {
        Object param0 = params[0];
        if (param0 instanceof ASBOGOSYSNavEnhet) {
            return ASBOGOSYSNavEnhet.class.cast(param0).getEnhetsId();
        } else if (param0 instanceof ASBOGOSYSHentSpesialEnhetTilPersonRequest) {
            return ASBOGOSYSHentSpesialEnhetTilPersonRequest.class.cast(param0).getFnr();
        } else if (param0 instanceof ASBOGOSYSHentNAVEnhetListeRequest) {
            ASBOGOSYSHentNAVEnhetListeRequest req = ASBOGOSYSHentNAVEnhetListeRequest.class.cast(param0);
            return req.getTypeOrganiserer() + req.getTypeOrganisertUnder() + req.getNAVEnhet();
        } else if (param0 instanceof ASBOGOSYSFinnNAVEnhetRequest) {
            ASBOGOSYSFinnNAVEnhetRequest req = ASBOGOSYSFinnNAVEnhetRequest.class.cast(param0);
            return req.getTypeEnhet() + req.getFagomradeKode();
        } else if (param0 instanceof ASBOGOSYSFinnArenaNAVEnhetListeRequest) {
            ASBOGOSYSFinnArenaNAVEnhetListeRequest req = ASBOGOSYSFinnArenaNAVEnhetListeRequest.class.cast(param0);
            return req.getEnhetIdSokeStreng() + req.getEnhetNavnSokeStreng() + req.getMaxantall();
        } else if (param0 instanceof ASBOGOSYSHentNAVAnsattFagomradeListeRequest) {
            ASBOGOSYSHentNAVAnsattFagomradeListeRequest req = ASBOGOSYSHentNAVAnsattFagomradeListeRequest.class.cast(param0);
            return req.getEnhetsId() + req.getAnsattId();
        } else if (param0 instanceof ASBOGOSYSNAVAnsatt) {
            ASBOGOSYSNAVAnsatt req = ASBOGOSYSNAVAnsatt.class.cast(param0);
            return req.getAnsattId() + req.getAnsattNavn() + req.getEnheter().toString();
        } else {
            return super.generate(target, method, params);
        }
    }
}
