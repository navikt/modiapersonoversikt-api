package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class HenvendelseEndpointConfig {

    @Bean
    public HenvendelsePortType henvendelsePortType() {
        HenvendelsePortType prod = createHenvendelsePortType().configureStsForSubject().build();
        return createTimerProxyForWebService("henvendelseV2", prod, HenvendelsePortType.class);
    }

    @Bean
    public Pingable henvendelsePing() {
        final HenvendelsePortType ws = createHenvendelsePortType().configureStsForSystemUser().build();
        return new PingableWebService("Hent henvendelse", ws);
    }

    private static CXFClient<HenvendelsePortType> createHenvendelsePortType() {
        return new CXFClient<>(HenvendelsePortType.class)
                .wsdl("classpath:Henvendelse.wsdl")
                .address(EnvironmentUtils.getRequiredProperty("DOMENE_BRUKERDIALOG_HENVENDELSE_V2_ENDPOINTURL"))
                .timeout(10000, 60000)
                .withProperty("jaxb.additionalContextClasses", new Class[]{
                        XMLHenvendelse.class,
                        XMLMetadataListe.class,
                        XMLMeldingFraBruker.class,
                        XMLMeldingTilBruker.class});
    }

}
