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

    private static final String KEY = "start.oppgavebehandling.withmock";

    @Bean
    public OppgavebehandlingPortType oppgavebehandlingPortType() {
        OppgavebehandlingPortType portType = new OppgavebehandlingPortTypeImpl(oppgavebehandlingEndpoint).oppgavebehandlingPortType();
        OppgavebehandlingPortType portTypeMock = new OppgavebehandlingPortTypeMock().oppgavebehandlingPortType();
        return createSwitcher(portType, portTypeMock, KEY, OppgavebehandlingPortType.class);
    }

    @Bean
    public Pingable oppgavebehandlingPing() {
        Pingable pingable = new OppgavebehandlingPortTypeImpl(oppgavebehandlingEndpoint).oppgavebehandlingPing();
        Pingable pingableMock = new OppgavebehandlingPortTypeMock().oppgavebehandlingPing();
        return createSwitcher(pingable, pingableMock, KEY, Pingable.class);
    }
}
