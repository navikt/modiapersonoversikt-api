package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.cache;

import org.springframework.cache.interceptor.DefaultKeyGenerator;

import java.lang.reflect.Method;

import static java.lang.String.valueOf;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

/**
 * Klasse for å lage cachekey som også tar høyde for
 * brukers ident, slik at ikke tilgang overstyres av
 * cachede entries. NB! Extender DefaultKeyGenerator
 * som er standard i nåværende versjon av Spring.
 * Denne er deprecated pga potensielle hash-kollisjoner
 * i nyeste versjon av Spring.
 */
public class AutentisertBrukerKeyGenerator extends DefaultKeyGenerator {

    public AutentisertBrukerKeyGenerator() {
        super();
    }

    @Override
    public Object generate(Object target, Method method, Object... params) {
        String cacheKey = valueOf(super.generate(target, method, params));
        return "user: " + getSubjectHandler().getUid() + "cachekey: " + target.getClass().getName() + method.getName() + cacheKey;
    }

}
