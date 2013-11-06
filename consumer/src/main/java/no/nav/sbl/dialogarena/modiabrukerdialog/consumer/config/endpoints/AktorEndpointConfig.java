package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl.AktorPortTypeImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.AktorPortTypeMock;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.InstanceSwitcher.createSwitcher;

@Configuration
public class AktorEndpointConfig {

    String key = "start.aktor.withmock";
    @Value("${aktor.url}")
    private String aktorEndpoint;

    @Bean
    public AktoerPortType aktorPortType() {
        AktoerPortType portType = new AktorPortTypeImpl(aktorEndpoint).aktorPortType();
        AktoerPortType portTypeMock = new AktorPortTypeMock().aktorPortType();
        return createSwitcher(portType, portTypeMock, key, AktoerPortType.class);
    }

    @Bean
    public Pingable aktorIdPing() {
        Pingable pingable = new AktorPortTypeImpl(aktorEndpoint).aktorIdPing();
        Pingable pingableMock = new AktorPortTypeMock().aktorIdPing();
        return createSwitcher(pingable, pingableMock, key, Pingable.class);
    }
}
