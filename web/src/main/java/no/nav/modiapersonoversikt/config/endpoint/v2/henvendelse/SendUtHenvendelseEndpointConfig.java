package no.nav.modiapersonoversikt.config.endpoint.v2.henvendelse;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.modiapersonoversikt.infrastructure.ping.PingableWebService;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.modiapersonoversikt.infrastructure.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class SendUtHenvendelseEndpointConfig {
    @Autowired
    private StsConfig stsConfig;

    @Bean
    public SendUtHenvendelsePortType sendUtHenvendelsePortType() {
        SendUtHenvendelsePortType prod = createSendUtHenvendelsePortType().configureStsForSubject(stsConfig).build();

        return createTimerProxyForWebService("SendUtHenvendelse", prod, SendUtHenvendelsePortType.class);
    }

    @Bean
    public Pingable sendUtHenvendelsePing() {
        final SendUtHenvendelsePortType ws = createSendUtHenvendelsePortType().configureStsForSystemUser(stsConfig).build();
        return new PingableWebService("Send ut henvendelse", ws);
    }

    private static CXFClient<SendUtHenvendelsePortType> createSendUtHenvendelsePortType() {
        return new CXFClient<>(SendUtHenvendelsePortType.class)
                .wsdl("classpath:wsdl/SendUtHenvendelse.wsdl")
                .address(EnvironmentUtils.getRequiredProperty("DOMENE_BRUKERDIALOG_SENDUTHENVENDELSE_V1_ENDPOINTURL"))
                .withProperty("jaxb.additionalContextClasses", new Class[]{
                        XMLHenvendelse.class,
                        XMLMetadataListe.class,
                        XMLMeldingFraBruker.class,
                        XMLMeldingTilBruker.class});
    }

}
