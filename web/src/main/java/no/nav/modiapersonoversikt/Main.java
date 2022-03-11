package no.nav.modiapersonoversikt;

import no.nav.common.utils.Credentials;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.common.utils.NaisUtils;

import no.nav.common.utils.SslUtils;
import no.nav.modiapersonoversikt.consumer.ldap.LDAP;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static no.nav.common.utils.EnvironmentUtils.Type.PUBLIC;
import static no.nav.common.utils.EnvironmentUtils.Type.SECRET;
import static no.nav.modiapersonoversikt.config.service.ServiceConfig.SYSTEMUSER_PASSWORD;
import static no.nav.modiapersonoversikt.config.service.ServiceConfig.SYSTEMUSER_USERNAME;
import static no.nav.modiapersonoversikt.consumer.arena.ArbeidOgAktivitetEndpointConfig.KJERNEINFO_TJENESTEBUSS_PASSWORD;
import static no.nav.modiapersonoversikt.consumer.arena.ArbeidOgAktivitetEndpointConfig.KJERNEINFO_TJENESTEBUSS_USERNAME;

@SpringBootApplication
public class Main {
    static {
        System.setProperty("javax.xml.soap.SAAJMetaFactory", "com.sun.xml.messaging.saaj.soap.SAAJMetaFactoryImpl");
    }

    public static void main(String... args) {
        loadVaultSecrets();
        SslUtils.setupTruststore();
        // Overstyrer appnavn slik at vi er sikre på at vi later som vi er modiabrukerdialog. ;)
        EnvironmentUtils.setProperty("NAIS_APP_NAME", "modiabrukerdialog", PUBLIC);

        SpringApplication.run(Main.class, args);
    }

    private static void loadVaultSecrets() {
        Credentials serviceUser = NaisUtils.getCredentials("service_user");
        EnvironmentUtils.setProperty(SYSTEMUSER_USERNAME, serviceUser.username, PUBLIC);
        EnvironmentUtils.setProperty(SYSTEMUSER_PASSWORD, serviceUser.password, SECRET);

        Credentials ldapUser = NaisUtils.getCredentials("srvssolinux");
        EnvironmentUtils.setProperty(LDAP.USERNAME, ldapUser.username, PUBLIC);
        EnvironmentUtils.setProperty(LDAP.PASSWORD, ldapUser.password, SECRET);

        Credentials gosysUser = NaisUtils.getCredentials("gosys_user");
        EnvironmentUtils.setProperty(KJERNEINFO_TJENESTEBUSS_USERNAME, gosysUser.username, PUBLIC);
        EnvironmentUtils.setProperty(KJERNEINFO_TJENESTEBUSS_PASSWORD, gosysUser.password, SECRET);
    }
}
