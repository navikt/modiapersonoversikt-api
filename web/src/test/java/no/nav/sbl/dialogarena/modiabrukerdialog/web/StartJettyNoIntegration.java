package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import java.io.File;

import static no.nav.modig.core.test.FilesAndDirs.TEST_RESOURCES;
import static no.nav.modig.core.test.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.StartJetty.createLoginService;
import static no.nav.sbl.dialogarena.test.SystemProperties.setFrom;

/**
 * Fyrer opp Jetty uten noen form for integrasjon.
 *
 * Logg inn med vilkårlig brukernavn og passord (ettersom alt er integrasjonsløst har det ingen betydning hva som settes på kontekst).
 * Uavhengig av hva som søkes på vil samme person returneres.
 * Fødselsnummer er spesifisert i {@link no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.MockContext}
 */
public class StartJettyNoIntegration {

    public static void main(String ... args) {
        setFrom("jetty-mock-environment.properties");
        setupKeyAndTrustStore();

        usingWar(WEBAPP_SOURCE)
                .at("modiabrukerdialog")
                .withLoginService(createLoginService())
                .port(8083)
                .overrideWebXml(new File(TEST_RESOURCES, "mock-web.xml"))
                .buildJetty()
                .start();
    }

}
