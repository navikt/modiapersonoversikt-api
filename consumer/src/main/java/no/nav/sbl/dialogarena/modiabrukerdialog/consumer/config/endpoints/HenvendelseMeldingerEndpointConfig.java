package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl.HenvendelseMeldingerPortTypeImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelseMeldingerPortTypeMock;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.HenvendelseMeldingerPortType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.InstanceSwitcher.createSwitcher;

@Configuration
public class HenvendelseMeldingerEndpointConfig {
    @Value("${henvendelse.meldinger.endpoint.url}")
    protected String henvendelseEndpoint;

    private String key = "start.henvendelsemeldinger.withmock";

    @Bean
    public HenvendelseMeldingerPortType henvendelsePortType() {
        HenvendelseMeldingerPortType portType = new HenvendelseMeldingerPortTypeImpl(henvendelseEndpoint).henvendelsePortType();
        HenvendelseMeldingerPortType portTypeMock = new HenvendelseMeldingerPortTypeMock().henvendelseMeldingerPortType();
        return createSwitcher(portType, portTypeMock, key, HenvendelseMeldingerPortType.class);
    }

    @Bean
    public Pingable henvendelsePing() {
        Pingable pingable = new HenvendelseMeldingerPortTypeImpl(henvendelseEndpoint).henvendelsePing();
        Pingable pingableMock = new HenvendelseMeldingerPortTypeMock().henvendelsePing();
        return createSwitcher(pingable, pingableMock, key, Pingable.class);
    }

}
