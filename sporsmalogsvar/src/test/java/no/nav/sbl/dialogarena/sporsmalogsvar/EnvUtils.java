package no.nav.sbl.dialogarena.sporsmalogsvar;

import no.nav.brukerdialog.security.context.SubjectHandlerUtils;
import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.brukerdialog.tools.SecurityConstants;

import java.util.HashMap;
import java.util.Map;

public class EnvUtils {
    public static void withSubject(String ident, Runnable fn) {
        System.setProperty(SecurityConstants.SYSTEMUSER_USERNAME, "srvModiabrukerdialog");
        SubjectHandlerUtils.setSubject(new SubjectHandlerUtils.SubjectBuilder(ident, IdentType.InternBruker).getSubject());

        withEnv(new HashMap<String, String>() {{
            put(SecurityConstants.SYSTEMUSER_USERNAME, "srvModiabrukerdialog");
        }}, fn);
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
