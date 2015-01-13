package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.cache;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSHentNAVAnsattFagomradeListeRequest;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.*;
import org.apache.commons.collections15.Transformer;

import java.lang.reflect.Method;
import java.util.HashMap;

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
        Transformer transformer = asbogosysCachekeyMapper.get(param0.getClass());
        if (transformer != null) {
            return transformer.transform(param0);
        }
        return super.generate(target, method, params);
    }

    private static HashMap<Class, Transformer> asbogosysCachekeyMapper = new HashMap<Class, Transformer>() {{
        put(ASBOGOSYSNavEnhet.class, new Transformer<ASBOGOSYSNavEnhet, Object>() {
            @Override
            public Object transform(ASBOGOSYSNavEnhet obj) {
                return obj.getEnhetsId();
            }
        });

        put(ASBOGOSYSHentSpesialEnhetTilPersonRequest.class, new Transformer<ASBOGOSYSHentSpesialEnhetTilPersonRequest, Object>() {
            @Override
            public Object transform(ASBOGOSYSHentSpesialEnhetTilPersonRequest obj) {
                return obj.getFnr();
            }
        });

        put(ASBOGOSYSHentNAVEnhetListeRequest.class, new Transformer<ASBOGOSYSHentNAVEnhetListeRequest, Object>() {
            @Override
            public Object transform(ASBOGOSYSHentNAVEnhetListeRequest obj) {
                return obj.getTypeOrganiserer() + obj.getTypeOrganisertUnder() + obj.getNAVEnhet();
            }
        });

        put(ASBOGOSYSFinnNAVEnhetRequest.class, new Transformer<ASBOGOSYSFinnNAVEnhetRequest, Object>() {
            @Override
            public Object transform(ASBOGOSYSFinnNAVEnhetRequest obj) {
                return obj.getTypeEnhet() + obj.getFagomradeKode();
            }
        });

        put(ASBOGOSYSFinnArenaNAVEnhetListeRequest.class, new Transformer<ASBOGOSYSFinnArenaNAVEnhetListeRequest, Object>() {
            @Override
            public Object transform(ASBOGOSYSFinnArenaNAVEnhetListeRequest obj) {
                return obj.getEnhetIdSokeStreng() + obj.getEnhetNavnSokeStreng() + obj.getMaxantall();
            }
        });

        put(ASBOGOSYSHentNAVAnsattFagomradeListeRequest.class, new Transformer<ASBOGOSYSHentNAVAnsattFagomradeListeRequest, Object>() {
            @Override
            public Object transform(ASBOGOSYSHentNAVAnsattFagomradeListeRequest obj) {
                return obj.getEnhetsId() + obj.getAnsattId();
            }
        });

        put(ASBOGOSYSNAVAnsatt.class, new Transformer<ASBOGOSYSNAVAnsatt, Object>() {
            @Override
            public Object transform(ASBOGOSYSNAVAnsatt obj) {
                return obj.getAnsattId() + obj.getAnsattNavn() + obj.getEnheter().toString();
            }
        });
    }};
}
