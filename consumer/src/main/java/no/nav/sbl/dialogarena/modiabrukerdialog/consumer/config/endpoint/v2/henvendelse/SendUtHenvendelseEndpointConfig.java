package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class SendUtHenvendelseEndpointConfig {

    @Bean
    public SendUtHenvendelsePortType sendUtHenvendelsePortType() {
        SendUtHenvendelsePortType prod = createSendUtHenvendelsePortType().configureStsForSubject().build();

        return createTimerProxyForWebService("SendUtHenvendelse", prod, SendUtHenvendelsePortType.class);
    }

    @Bean
    public Pingable sendUtHenvendelsePing() {
        final SendUtHenvendelsePortType ws = createSendUtHenvendelsePortType().configureStsForSystemUser().build();
        return new PingableWebService("Send ut henvendelse", ws);
    }

    private static CXFClient<SendUtHenvendelsePortType> createSendUtHenvendelsePortType() {
        return new CXFClient<>(SendUtHenvendelsePortType.class)
                .wsdl("classpath:SendUtHenvendelse.wsdl")
                .address(System.getProperty("send.ut.henvendelse.url"))
                .withProperty("jaxb.additionalContextClasses", new Class[]{
                        XMLHenvendelse.class,
                        XMLMetadataListe.class,
                        XMLMeldingFraBruker.class,
                        XMLMeldingTilBruker.class});
    }

}
