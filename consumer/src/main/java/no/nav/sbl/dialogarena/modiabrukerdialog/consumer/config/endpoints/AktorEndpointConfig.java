package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl.AktorPortTypeImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.AktorPortTypeMock;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.InstanceSwitcher.createSwitcher;

@Configuration
public class AktorEndpointConfig {

    String key = "start.aktor.withintegration";
    @Value("${aktor.url}")
    private URL aktorEndpoint;
    private AktoerPortType portType = new AktorPortTypeImpl(aktorEndpoint).aktorPortType();
    private AktoerPortType portTypeMock = new AktorPortTypeMock().aktorPortType();
    private Pingable pingable = new AktorPortTypeImpl(aktorEndpoint).aktorIdPing();
    private Pingable pingableMock = new AktorPortTypeImpl(aktorEndpoint).aktorIdPing();

    @Bean
    public AktoerPortType aktorPortType() {
        return createSwitcher(portType, portTypeMock, key, AktoerPortType.class);
    }

    @Bean
    public Pingable aktorIdPing() {
        return createSwitcher(pingable, pingableMock, key, Pingable.class);
    }
}
