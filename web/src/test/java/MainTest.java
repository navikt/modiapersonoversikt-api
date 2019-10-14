import no.nav.apiapp.ApiApp;
import no.nav.fasit.FasitUtils;
import no.nav.fasit.LdapConfig;
import no.nav.fasit.ServiceUser;
import no.nav.modig.testcertificates.TestCertificates;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ModiaApplicationContext;
import no.nav.sbl.dialogarena.test.SystemProperties;
import no.nav.testconfig.ApiAppTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static no.nav.sbl.util.EnvironmentUtils.Type.PUBLIC;
import static no.nav.sbl.util.EnvironmentUtils.Type.SECRET;
import static no.nav.sbl.util.EnvironmentUtils.setProperty;

public class MainTest {

    private static final Logger logger = LoggerFactory.getLogger(MainTest.class);
    public static final String APPLICATION_NAME = "modiabrukerdialog";

    public static void main(String[] args) {
        TestCertificates.setupKeyAndTrustStore();
        ApiAppTest.setupTestContext(ApiAppTest.Config.builder().build());
        SystemProperties.setFrom("jetty-environment.properties");
        logger.info("Env= " + FasitUtils.getDefaultEnvironment());

        ServiceUser srvModiabrukerdialog = FasitUtils.getServiceUser("srvModiabrukerdialog", APPLICATION_NAME, "q1", "preprod.local");
        setProperty("SRVMODIABRUKERDIALOG_USERNAME", srvModiabrukerdialog.getUsername(), PUBLIC);
        setProperty("SRVMODIABRUKERDIALOG_PASSWORD", srvModiabrukerdialog.getPassword(), SECRET);


        ServiceUser issoUser = FasitUtils.getServiceUser("isso-rp-user", APPLICATION_NAME, "q1", "preprod.local");
        setProperty("isso-rp-user.password", issoUser.getPassword(), SECRET);

        ServiceUser srvKjerneinfoTjenestebuss = FasitUtils.getServiceUser("srv_kjerneinfo_tjenestebuss", APPLICATION_NAME, "q1", "preprod.local");
        setProperty("ctjenestebuss.username", srvKjerneinfoTjenestebuss.getUsername(), PUBLIC);
        setProperty("ctjenestebuss.password", srvKjerneinfoTjenestebuss.getPassword(), SECRET);

        LdapConfig ldapConfig = FasitUtils.getLdapConfig("q");
        setProperty("ldap.password", ldapConfig.getPassword(), SECRET);

        ApiApp.startApp(ModiaApplicationContext.class, new String[]{"8083", "8084"});
    }
}
