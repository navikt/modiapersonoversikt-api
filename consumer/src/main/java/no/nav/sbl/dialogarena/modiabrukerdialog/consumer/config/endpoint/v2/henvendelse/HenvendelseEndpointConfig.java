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
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.TimingMetricsProxy.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelsePortTypeMock.createHenvendelsePortTypeMock;

@Configuration
public class HenvendelseEndpointConfig {

    public static final String HENVENDELSE_KEY = "start.henvendelse.withmock";

    @Bean
    public HenvendelsePortType henvendelsePortType() {
        HenvendelsePortType prod = createHenvendelsePortType(new UserSAMLOutInterceptor());
        HenvendelsePortType mock = createHenvendelsePortTypeMock();
        
        return createMetricsProxyWithInstanceSwitcher(prod, mock, HENVENDELSE_KEY, HenvendelsePortType.class);
    }

    @Bean
    public Pingable henvendelsePing() {
        final HenvendelsePortType ws = createHenvendelsePortType(new SystemSAMLOutInterceptor());
        return new PingableWebService("HENVENDELSE_V2", ws);
    }

    private static HenvendelsePortType createHenvendelsePortType(AbstractSAMLOutInterceptor interceptor) {
        return new CXFClient<>(HenvendelsePortType.class)
                .wsdl("classpath:Henvendelse.wsdl")
                .address(System.getProperty("henvendelse.v2.url"))
                .withOutInterceptor(interceptor)
                .setProperty("jaxb.additionalContextClasses", new Class[]{
                        XMLHenvendelse.class,
                        XMLMetadataListe.class,
                        XMLMeldingFraBruker.class,
                        XMLMeldingTilBruker.class})
                .build();
    }

}
