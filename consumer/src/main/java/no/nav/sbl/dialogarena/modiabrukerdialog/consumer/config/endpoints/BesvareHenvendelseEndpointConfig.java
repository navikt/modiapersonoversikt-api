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

    private static final String KEY = "start.besvarehenvendelse.withmock";

    @Bean
    public BesvareHenvendelsePortType besvareHenvendelsePortType() {
        BesvareHenvendelsePortType portType = new BesvareHenvendelsePortTypeImpl(besvareHenvendelseEndpoint).besvareHenvendelsePortType();
        BesvareHenvendelsePortType portTypeMock = new BesvareHenvendelsePortTypeMock().besvareHenvendelsePortType();

        return createSwitcher(portType, portTypeMock, KEY, BesvareHenvendelsePortType.class);
    }

    @Bean
    public Pingable besvareHenvendelsePing() {
        Pingable pingable = new BesvareHenvendelsePortTypeImpl(besvareHenvendelseEndpoint).besvareHenvendelsePing();
        Pingable pingableMock = new BesvareHenvendelsePortTypeMock().besvareHenvendelsePing();

        return createSwitcher(pingable, pingableMock, KEY, Pingable.class);
    }
}
