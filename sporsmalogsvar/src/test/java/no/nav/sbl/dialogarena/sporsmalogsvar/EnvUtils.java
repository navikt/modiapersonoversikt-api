package no.nav.sbl.dialogarena.sporsmalogsvar;

import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.brukerdialog.tools.SecurityConstants;
import no.nav.common.auth.SsoToken;
import no.nav.common.auth.Subject;
import no.nav.common.auth.SubjectHandler;
import no.nav.sbl.util.fn.UnsafeRunnable;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;

public class EnvUtils {
    public static void withSubject(String ident, UnsafeRunnable fn) {
        withEnv(new HashMap<String, String>() {{
            put(SecurityConstants.SYSTEMUSER_USERNAME, "srvModiabrukerdialog");
        }}, () -> SubjectHandler.withSubject(
                new Subject(ident, IdentType.InternBruker, SsoToken.oidcToken("", emptyMap())),
                fn
        ));
    }

    public static void withEnv(Map<String, String> env, Runnable fn) {
        Map<String, String> originalValues = env
                .keySet()
                .stream()
                .collect(HashMap::new, (m, v) -> m.put(v, System.getProperty(v)), HashMap::putAll);
        // Kjent bug JDK-8148463, null-values kan ikke sendes til Hashmap::merge
        // I en fremtidig verden kan man sikkert bruke den pene syntaksen nedenfor.
        // .collect(Collectors.toMap(Function.identity(), System::getProperty));

        setEnv(env);
        fn.run();
        setEnv(originalValues);

    }

    private static void setEnv(Map<String, String> env) {
        env.forEach((key, value) -> {
            if (value == null) {
                System.clearProperty(key);
            } else {
                System.setProperty(key, value);
            }
        });
    }
}
