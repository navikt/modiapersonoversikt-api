package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import no.nav.modig.security.sts.utility.STSConfigurationUtility;
import org.apache.cxf.frontend.ClientProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class StsConfig {

    @Autowired
    ServicesConfig services;

    @PostConstruct
    public void setupSts() {
        STSConfigurationUtility.configureStsForSystemUser(ClientProxy.getClient(services.sporsmalOgSvarPortType()));
    }
}
