package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl.OppgavebehandlingPortTypeImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.OppgavebehandlingPortTypeMock;
import no.nav.tjeneste.domene.brukerdialog.oppgavebehandling.v1.OppgavebehandlingPortType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.InstanceSwitcher.createSwitcher;

@Configuration
public class OppgavebehandlingEndpointConfig {

    @Value("${oppgavebehandlingendpoint.url}")
    protected String oppgavebehandlingEndpoint;
    private OppgavebehandlingPortType portType = new OppgavebehandlingPortTypeImpl(oppgavebehandlingEndpoint).oppgavebehandlingPortType();
    private OppgavebehandlingPortType portTypeMock = new OppgavebehandlingPortTypeMock().oppgavebehandlingPortType();
    private Pingable pingable = new OppgavebehandlingPortTypeImpl(oppgavebehandlingEndpoint).oppgavebehandlingPing();
    private Pingable pingableMock = new OppgavebehandlingPortTypeMock().oppgavebehandlingPing();
    private String key = "start.oppgavebehandling.withmock";

    @Bean
    public OppgavebehandlingPortType oppgavebehandlingPortType() {
        return createSwitcher(portType, portTypeMock, key, OppgavebehandlingPortType.class);
    }

    @Bean
    public Pingable oppgavebehandlingPing() {
        return createSwitcher(pingable, pingableMock, key, Pingable.class);
    }
}
