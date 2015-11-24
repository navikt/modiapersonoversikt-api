package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse.HenvendelseEndpointConfig.HENVENDELSE_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.TimingMetricsProxy.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.SendUtHenvendelsePortTypeMock.createSendUtHenvendelsePortTypeMock;

@Configuration
public class SendUtHenvendelseEndpointConfig {

    @Bean
    public SendUtHenvendelsePortType sendUtHenvendelsePortType() {
        SendUtHenvendelsePortType prod = createSendUtHenvendelsePortType(new UserSAMLOutInterceptor());
        SendUtHenvendelsePortType mock = createSendUtHenvendelsePortTypeMock();

        return createMetricsProxyWithInstanceSwitcher(prod, mock, HENVENDELSE_KEY, SendUtHenvendelsePortType.class);
    }

    @Bean
    public Pingable sendUtHenvendelsePing() {
        final SendUtHenvendelsePortType ws = createSendUtHenvendelsePortType(new SystemSAMLOutInterceptor());
        return new PingableWebService("SEND_UT_HENVENDELSE", ws);}

    private static SendUtHenvendelsePortType createSendUtHenvendelsePortType(AbstractSAMLOutInterceptor interceptor) {
        return new CXFClient<>(SendUtHenvendelsePortType.class)
                .wsdl("classpath:SendUtHenvendelse.wsdl")
                .address(System.getProperty("send.ut.henvendelse.url"))
                .withOutInterceptor(interceptor)
                .setProperty("jaxb.additionalContextClasses", new Class[]{
                        XMLHenvendelse.class,
                        XMLMetadataListe.class,
                        XMLMeldingFraBruker.class,
                        XMLMeldingTilBruker.class})
                .build();
    }

}
