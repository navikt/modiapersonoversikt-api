package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.tjeneste.domene.brukerdialog.oppgavebehandling.v1.OppgavebehandlingPortType;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HentOppgaveConfig {
    protected String oppgavebehandlingEndpoint = "https://localhost:30103/besvarehenvendelse/services/domene.Brukerdialog/GsakOppgavebehandling_v1";

    @Bean
    public OppgavebehandlingPortType oppgavebehandlingPortType() {
        JaxWsProxyFactoryBean jaxwsClient = new JaxWsProxyFactoryBean();
        jaxwsClient.setServiceClass(OppgavebehandlingPortType.class);
        jaxwsClient.setAddress(oppgavebehandlingEndpoint);
        OppgavebehandlingPortType oppgavebehandlingPortType = jaxwsClient.create(OppgavebehandlingPortType.class);
        Client client = ClientProxy.getClient(oppgavebehandlingPortType);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        httpConduit.setTlsClientParameters(new TLSClientParameters());
        return oppgavebehandlingPortType;
    }
}
