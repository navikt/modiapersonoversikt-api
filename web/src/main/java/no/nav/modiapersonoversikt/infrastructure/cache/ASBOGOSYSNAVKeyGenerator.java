package no.nav.modiapersonoversikt.infrastructure.cache;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.*;
import no.nav.modiapersonoversikt.legacy.api.utils.cache.AutentisertBrukerKeyGenerator;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Klasse for å lage cachekey som også tar høyde for
 * brukers ident, slik at ikke tilgang overstyres av
 * cachede entries.
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
        Function transformer = asbogosysCachekeyMapper.get(param0.getClass());
        if (transformer != null) {
            return transformer.apply(param0);
        }
        return super.generate(target, method, params);
    }

    private static Map<Class, Function> asbogosysCachekeyMapper = new HashMap<Class, Function>() {{
        put(ASBOGOSYSNavEnhet.class, new Function<ASBOGOSYSNavEnhet, Object>() {
            @Override
            public Object apply(ASBOGOSYSNavEnhet obj) {
                return obj.getEnhetsId();
            }
        });

        put(ASBOGOSYSHentSpesialEnhetTilPersonRequest.class, new Function<ASBOGOSYSHentSpesialEnhetTilPersonRequest, Object>() {
            @Override
            public Object apply(ASBOGOSYSHentSpesialEnhetTilPersonRequest obj) {
                return obj.getFnr();
            }
        });

        put(ASBOGOSYSHentNAVEnhetListeRequest.class, new Function<ASBOGOSYSHentNAVEnhetListeRequest, Object>() {
            @Override
            public Object apply(ASBOGOSYSHentNAVEnhetListeRequest obj) {
                return obj.getTypeOrganiserer() + obj.getTypeOrganisertUnder() + obj.getNAVEnhet();
            }
        });

        put(ASBOGOSYSFinnNAVEnhetRequest.class, new Function<ASBOGOSYSFinnNAVEnhetRequest, Object>() {
            @Override
            public Object apply(ASBOGOSYSFinnNAVEnhetRequest obj) {
                return obj.getTypeEnhet() + obj.getFagomradeKode();
            }
        });

        put(ASBOGOSYSFinnArenaNAVEnhetListeRequest.class, new Function<ASBOGOSYSFinnArenaNAVEnhetListeRequest, Object>() {
            @Override
            public Object apply(ASBOGOSYSFinnArenaNAVEnhetListeRequest obj) {
                return obj.getEnhetIdSokeStreng() + obj.getEnhetNavnSokeStreng() + obj.getMaxantall();
            }
        });

    }};
}
