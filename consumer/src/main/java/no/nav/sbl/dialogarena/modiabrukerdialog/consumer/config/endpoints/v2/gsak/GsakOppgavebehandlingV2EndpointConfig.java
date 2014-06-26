package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.gsak;

import no.nav.virksomhet.tjenester.oppgavebehandling.v2.Oppgavebehandling;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.gsak.GsakOppgaveV2EndpointConfig.GSAK_V2_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.gsak.GsakTjenesteSikkerhet.STANDARD_BRUKERNAVN;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.gsak.GsakTjenesteSikkerhet.STANDARD_PASSORD;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.gsak.GsakTjenesteSikkerhet.leggPaaAutentisering;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakOppgavebehandlingV2PortTypeMock.createOppgavebehandlingPortTypeMock;

@Configuration
public class GsakOppgavebehandlingV2EndpointConfig {

    @Bean
    public Oppgavebehandling gsakOppgavebehandlingPortType() {
        return createSwitcher(createOppgavebehandlingPortType(), createOppgavebehandlingPortTypeMock(), GSAK_V2_KEY, Oppgavebehandling.class);
    }

    private static Oppgavebehandling createOppgavebehandlingPortType() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setWsdlLocation("classpath:oppgavebehandling/no/nav/virksomhet/tjenester/oppgavebehandling/oppgavebehandling.wsdl");
        proxyFactoryBean.setAddress(System.getProperty("gsak.oppgavebehandling.v2.url"));
        proxyFactoryBean.setServiceClass(Oppgavebehandling.class);
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        proxyFactoryBean.setProperties(new HashMap<String, Object>());
        proxyFactoryBean.getProperties().put("schema-validation-enabled", true);

        leggPaaAutentisering(proxyFactoryBean, STANDARD_BRUKERNAVN, STANDARD_PASSORD);

        return proxyFactoryBean.create(Oppgavebehandling.class);
    }
}
