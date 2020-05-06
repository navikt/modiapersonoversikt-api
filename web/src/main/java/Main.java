import no.nav.apiapp.ApiApp;
import no.nav.common.utils.NaisUtils;
import no.nav.sbl.dialogarena.common.abac.pep.CredentialConstants;
import no.nav.sbl.dialogarena.common.cxf.StsSecurityConstants;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.ldap.LdapContextProvider;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ModiaApplicationContext;
import no.nav.sbl.util.EnvironmentUtils;

import static no.nav.sbl.dialogarena.common.cxf.StsSecurityConstants.SYSTEMUSER_PASSWORD;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg.NorgEndpointFelles.KJERNEINFO_TJENESTEBUSS_PASSWORD;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg.NorgEndpointFelles.KJERNEINFO_TJENESTEBUSS_USERNAME;
import static no.nav.sbl.util.EnvironmentUtils.Type.PUBLIC;
import static no.nav.sbl.util.EnvironmentUtils.Type.SECRET;

public class Main {
    public static void main(String... args) {
        loadVaultSecrets();
        // Overstyrer appnavn slik at vi er sikre på at vi later som vi er modiabrukerdialog. ;)
        EnvironmentUtils.setProperty("NAIS_APP_NAME", "modiabrukerdialog", PUBLIC);

        ApiApp.runApp(ModiaApplicationContext.class, args);
    }

    private static void loadVaultSecrets() {
        NaisUtils.Credentials serviceUser = NaisUtils.getCredentials("service_user");
        EnvironmentUtils.setProperty(CredentialConstants.SYSTEMUSER_USERNAME, serviceUser.username, PUBLIC);
        EnvironmentUtils.setProperty(CredentialConstants.SYSTEMUSER_PASSWORD, serviceUser.password, SECRET);
        EnvironmentUtils.setProperty(StsSecurityConstants.SYSTEMUSER_USERNAME, serviceUser.username, PUBLIC);
        EnvironmentUtils.setProperty(SYSTEMUSER_PASSWORD, serviceUser.password, SECRET);

        NaisUtils.Credentials ldapUser = NaisUtils.getCredentials("srvssolinux");
        EnvironmentUtils.setProperty(LdapContextProvider.LDAP_USERNAME, ldapUser.username, PUBLIC);
        EnvironmentUtils.setProperty(LdapContextProvider.LDAP_PASSWORD, ldapUser.password, SECRET);

        NaisUtils.Credentials gosysUser = NaisUtils.getCredentials("gosys_user");
        EnvironmentUtils.setProperty(KJERNEINFO_TJENESTEBUSS_USERNAME, gosysUser.username, PUBLIC);
        EnvironmentUtils.setProperty(KJERNEINFO_TJENESTEBUSS_PASSWORD, gosysUser.password, SECRET);
    }
}
