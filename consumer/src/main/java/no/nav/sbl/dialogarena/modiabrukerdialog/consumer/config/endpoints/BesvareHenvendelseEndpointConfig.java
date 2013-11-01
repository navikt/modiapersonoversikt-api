package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.integrasjon.features.TimeoutFeature;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl.BesvareHenvendelsePortTypeImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.BesvareHenvendelsePortTypeMock;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.EndpointsConfig.MODIA_CONNECTION_TIMEOUT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.EndpointsConfig.MODIA_RECEIVE_TIMEOUT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.Utils.konfigurerMedHttps;

@Configuration
public class BesvareHenvendelseEndpointConfig {

    @Value("${besvarehenvendelseendpoint.url}")
    protected String besvareHenvendelseEndpoint;

    private BesvareHenvendelsePortType portType = new BesvareHenvendelsePortTypeImpl(besvareHenvendelseEndpoint).besvareHenvendelsePortType();
    private BesvareHenvendelsePortType portTypeMock = new BesvareHenvendelsePortTypeMock().besvareHenvendelsePortType();
    private Pingable pingable = new BesvareHenvendelsePortTypeImpl(besvareHenvendelseEndpoint).besvareHenvendelsePing();
    private Pingable pingableMock = new BesvareHenvendelsePortTypeMock().besvareHenvendelsePing();
    private String key = "start.";

    @Bean
    public BesvareHenvendelsePortType besvareHenvendelsePortType() {
        return createSwitcher(portType, portTypeMock, key, BesvareHenvendelsePortType.class);
    }

    @Bean
    public Pingable besvareHenvendelsePing() {
        return createSwitcher(pingable, pingableMock, key, Pingable.class);
    }
}
