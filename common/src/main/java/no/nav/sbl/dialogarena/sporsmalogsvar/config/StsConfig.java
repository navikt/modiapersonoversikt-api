package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import no.nav.modig.security.sts.utility.STSConfigurationUtility;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;

import org.apache.cxf.frontend.ClientProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class StsConfig {

	@Autowired
    SporsmalOgSvarPortType sosPT;
	
	@Autowired
    HenvendelsePortType hPT;

    @PostConstruct
    public void setupSts() {
        STSConfigurationUtility.configureStsForSystemUser(ClientProxy.getClient(sosPT));
//        STSConfigurationUtility.configureStsForSystemUser(ClientProxy.getClient(hPT));
    }
    
}
