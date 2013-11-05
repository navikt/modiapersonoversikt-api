package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl.BesvareHenvendelsePortTypeImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.BesvareHenvendelsePortTypeMock;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.InstanceSwitcher.createSwitcher;

@Configuration
public class BesvareHenvendelseEndpointConfig {

    @Value("${besvarehenvendelseendpoint.url}")
    protected String besvareHenvendelseEndpoint;

    private BesvareHenvendelsePortType portType;
    private BesvareHenvendelsePortType portTypeMock;
    private Pingable pingable;
    private Pingable pingableMock;
    private String key = "start.besvarehenvendelse.withmock";

    @Bean
    public BesvareHenvendelsePortType besvareHenvendelsePortType() {
        portType = new BesvareHenvendelsePortTypeImpl(besvareHenvendelseEndpoint).besvareHenvendelsePortType();
        portTypeMock = new BesvareHenvendelsePortTypeMock().besvareHenvendelsePortType();

        return createSwitcher(portType, portTypeMock, key, BesvareHenvendelsePortType.class);
    }

    @Bean
    public Pingable besvareHenvendelsePing() {
        pingable = new BesvareHenvendelsePortTypeImpl(besvareHenvendelseEndpoint).besvareHenvendelsePing();
        pingableMock = new BesvareHenvendelsePortTypeMock().besvareHenvendelsePing();

        return createSwitcher(pingable, pingableMock, key, Pingable.class);
    }
}
