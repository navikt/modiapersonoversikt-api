package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import no.nav.modig.security.sts.utility.STSConfigurationUtility;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import org.apache.cxf.frontend.ClientProxy;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

@Configuration
public class StsConfig {

	@Inject
    @Named("sporsmalOgSvarPortType")
    SporsmalOgSvarPortType sporsmalOgSvarPortType;
	
	@Inject
    @Named("henvendelsePortType")
    HenvendelsePortType henvendelsePortType;

	@Inject
    @Named("selftestSporsmalOgSvarPortType")
    SporsmalOgSvarPortType selftestSporsmalOgSvarPortType;

	@Inject
    @Named("selftestHenvendelsePortType")
    HenvendelsePortType selftestHenvendelsePortType;

    @PostConstruct
    public void setupSts() {
        STSConfigurationUtility.configureStsForExternalSSO(ClientProxy.getClient(sporsmalOgSvarPortType));
        STSConfigurationUtility.configureStsForExternalSSO(ClientProxy.getClient(henvendelsePortType));
        STSConfigurationUtility.configureStsForSystemUser(ClientProxy.getClient(selftestSporsmalOgSvarPortType));
        STSConfigurationUtility.configureStsForSystemUser(ClientProxy.getClient(selftestHenvendelsePortType));
    }
    
}
