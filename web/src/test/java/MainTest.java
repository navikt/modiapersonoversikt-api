import no.nav.apiapp.ApiApp;
import no.nav.fasit.FasitUtils;
import no.nav.fasit.LdapConfig;
import no.nav.fasit.ServiceUser;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ModiaApplicationContext;
import no.nav.sbl.dialogarena.test.SystemProperties;
import no.nav.testconfig.ApiAppTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static no.nav.sbl.dialogarena.test.ssl.SSLTestUtils.setupKeyAndTrustStore;
import static no.nav.sbl.util.EnvironmentUtils.Type.PUBLIC;
import static no.nav.sbl.util.EnvironmentUtils.Type.SECRET;
import static no.nav.sbl.util.EnvironmentUtils.setProperty;
import static no.nav.testconfig.ApiAppTest.setupTestContext;

public class MainTest {

    private static final Logger logger = LoggerFactory.getLogger(MainTest.class);
    public static final String APPLICATION_NAME = "modiabrukerdialog";

    public static void main(String[] args) {
        setupKeyAndTrustStore();
        setupTestContext(ApiAppTest.Config.builder().applicationName(APPLICATION_NAME).build());
        SystemProperties.setFrom("jetty-environment.properties");

        ServiceUser srvModiabrukerdialog = FasitUtils.getServiceUser("srvModiabrukerdialog", APPLICATION_NAME);
        setProperty("SRVMODIABRUKERDIALOG_USERNAME", srvModiabrukerdialog.getUsername(), PUBLIC);
        setProperty("SRVMODIABRUKERDIALOG_PASSWORD", srvModiabrukerdialog.getPassword(), SECRET);

        logger.info("Env= " + FasitUtils.getDefaultEnvironment());
        LdapConfig ldapConfig = FasitUtils.getLdapConfig("q");
        setProperty("ldap.password", ldapConfig.getPassword(), SECRET);

        ServiceUser issoUser = FasitUtils.getServiceUser("isso-rp-user", APPLICATION_NAME);
        setProperty("isso-rp-user.password", issoUser.getPassword(), SECRET);

        ServiceUser srvKjerneinfoTjenestebuss = FasitUtils.getServiceUser("srv_kjerneinfo_tjenestebuss", APPLICATION_NAME);
        setProperty("ctjenestebuss.username", srvKjerneinfoTjenestebuss.getUsername(), PUBLIC);
        setProperty("ctjenestebuss.password", srvKjerneinfoTjenestebuss.getPassword(), SECRET);

        ApiApp.runApp(ModiaApplicationContext.class, new String[]{"8083", "8084"});
    }
}
