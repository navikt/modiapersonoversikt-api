import no.nav.dialogarena.config.fasit.FasitUtils;
import no.nav.dialogarena.config.fasit.LdapConfig;
import no.nav.dialogarena.config.fasit.ServiceUser;
import no.nav.modig.testcertificates.TestCertificates;
import no.nav.sbl.dialogarena.test.SystemProperties;
import no.nav.testconfig.ApiAppTest;

import static no.nav.sbl.util.EnvironmentUtils.Type.PUBLIC;
import static no.nav.sbl.util.EnvironmentUtils.Type.SECRET;
import static no.nav.sbl.util.EnvironmentUtils.setProperty;

public class MainTest {

    public static final String APPLICATION_NAME = "modiabrukerdialog";

    public static void main(String[] args) {
        TestCertificates.setupKeyAndTrustStore();
        ApiAppTest.setupTestContext();
        SystemProperties.setFrom("jetty-environment.properties");

        ServiceUser srvModiabrukerdialog = FasitUtils.getServiceUser("srvModiabrukerdialog", APPLICATION_NAME);
        setProperty("SRVMODIABRUKERDIALOG_USERNAME", srvModiabrukerdialog.getUsername(), PUBLIC);
        setProperty("SRVMODIABRUKERDIALOG_PASSWORD", srvModiabrukerdialog.getPassword(), SECRET);

        LdapConfig ldapConfig = FasitUtils.getLdapConfig("ldap", APPLICATION_NAME, FasitUtils.getDefaultEnvironment());
        setProperty("ldap.password", ldapConfig.getPassword(), SECRET);

        ServiceUser issoUser = FasitUtils.getServiceUser("isso-rp-user", APPLICATION_NAME);
        setProperty("isso-rp-user.password", issoUser.getPassword(), SECRET);

        ServiceUser srvKjerneinfoTjenestebuss = FasitUtils.getServiceUser("srv_kjerneinfo_tjenestebuss", APPLICATION_NAME);
        setProperty("ctjenestebuss.username", srvKjerneinfoTjenestebuss.getUsername(), PUBLIC);
        setProperty("ctjenestebuss.password", srvKjerneinfoTjenestebuss.getPassword(), SECRET);

        Main.main("8083", "8084");
    }
}
