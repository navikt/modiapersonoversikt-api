package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.cache;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSHentNAVAnsattFagomradeListeRequest;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.*;
import org.apache.commons.collections15.Transformer;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Klasse for å lage cachekey som IKKE tar høyde for
 * brukers ident.
 */
public class ASBOGOSYSNAVAnsattListeKeyGenerator extends SimpleKeyGenerator {

    public ASBOGOSYSNAVAnsattListeKeyGenerator() {
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
        return super.generate(target, method, params); // TODO: burde ikke vare denne returnere params?
    }

    private static Map<Class, Transformer> asbogosysCachekeyMapper = new HashMap<Class, Transformer>() {{
        put(ASBOGOSYSNavEnhet.class, new Transformer<ASBOGOSYSNavEnhet, Object>() {
            @Override
            public Object transform(ASBOGOSYSNavEnhet obj) {
                return obj.getEnhetsId();
            }
        });

    }};
}
