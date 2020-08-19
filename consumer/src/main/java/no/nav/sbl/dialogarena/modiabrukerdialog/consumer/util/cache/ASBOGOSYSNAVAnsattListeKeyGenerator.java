package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.cache;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Klasse for å lage cachekey som IKKE tar høyde for
 * brukers ident.
 */
public class ASBOGOSYSNAVAnsattListeKeyGenerator extends SimpleKeyGenerator {

    public ASBOGOSYSNAVAnsattListeKeyGenerator() {
    }

    @Override
    public Object generate(Object target, Method method, Object... params) {
        return super.generate(target, method, getCacheKey(params));
    }

    private Object getCacheKey(Object... params) {
        Object param0 = params[0];
        Function transformer = asbogosysCachekeyMapper.get(param0.getClass());
        if (transformer != null) {
            return transformer.apply(param0);
        }
        return params;
    }

    private static Map<Class, Function> asbogosysCachekeyMapper = new HashMap<Class, Function>() {{
        put(ASBOGOSYSNavEnhet.class, new Function<ASBOGOSYSNavEnhet, Object>() {
            @Override
            public Object apply(ASBOGOSYSNavEnhet enhet) {
                return enhet.getEnhetsId();
            }
        });

    }};
}
