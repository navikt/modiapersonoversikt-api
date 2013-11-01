package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl.HenvendelseMeldingerPortTypeImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelseMeldingerPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.MockPingable;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.HenvendelseMeldingerPortType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.ConfigUtil.isInMockMode;

@Configuration
public class HenvendelseMeldingerEndpointConfig {
    private static final Logger LOG = LoggerFactory.getLogger(HenvendelseMeldingerEndpointConfig.class);
    @Value("${henvendelse.meldinger.endpoint.url}")
    protected String henvendelseEndpoint;
    private boolean useMock;
    private HenvendelseMeldingerPortTypeImpl portType = new HenvendelseMeldingerPortTypeImpl(henvendelseEndpoint);
    private HenvendelseMeldingerPortTypeMock portTypeMock = new HenvendelseMeldingerPortTypeMock();

    public HenvendelseMeldingerEndpointConfig() {
        useMock = isInMockMode("start.henvendelsemeldinger.withintegration");
    }

    @Bean
    public HenvendelseMeldingerPortType henvendelsePortType() {
        if (useMock) {
            return portTypeMock.henvendelseMeldingerPortType();
        }
        return portType.henvendelsePortType();
    }

    @Bean
    public Pingable henvendelsePing() {
        if (useMock) {
            return new MockPingable("HenvendelseMeldingerEndpointConfig");
        }
        return portType.henvendelsePing();
    }

}
