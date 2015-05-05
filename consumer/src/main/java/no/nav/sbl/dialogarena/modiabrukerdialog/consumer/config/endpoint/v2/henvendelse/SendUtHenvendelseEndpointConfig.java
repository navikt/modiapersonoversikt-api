package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse.HenvendelseEndpointConfig.HENVENDELSE_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.SendUtHenvendelsePortTypeMock.createSendUtHenvendelsePortTypeMock;

@Configuration
public class SendUtHenvendelseEndpointConfig {

    @Bean
    public SendUtHenvendelsePortType sendUtHenvendelsePortType() {
        return createSwitcher(
                createSendUtHenvendelsePortType(new UserSAMLOutInterceptor()),
                createSendUtHenvendelsePortTypeMock(),
                HENVENDELSE_KEY,
                SendUtHenvendelsePortType.class
        );
    }

    @Bean
    public Pingable sendUtHenvendelsePing() {
        final SendUtHenvendelsePortType ws = createSendUtHenvendelsePortType(new SystemSAMLOutInterceptor());
        return new Pingable() {
            @Override
            public List<PingResult> ping() {
                long start = System.currentTimeMillis();
                String name = "SEND_UT_HENVENDELSE";
                try {
                    ws.ping();
                    return asList(new PingResult(name, SERVICE_OK, System.currentTimeMillis() - start));
                } catch (Exception e) {
                    return asList(new PingResult(name, SERVICE_FAIL, System.currentTimeMillis() - start));
                }
            }
        };
    }

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
