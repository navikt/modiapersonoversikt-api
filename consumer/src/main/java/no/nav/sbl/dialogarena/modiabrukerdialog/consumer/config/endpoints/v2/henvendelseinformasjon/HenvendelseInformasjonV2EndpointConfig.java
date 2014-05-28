package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.henvendelseinformasjon;

import no.nav.tjeneste.domene.brukerdialog.henvendelse.informasjon.v2.HenvendelseInformasjonV2PortType;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelseInformasjonV2PortTypeMock.createHenvendelseInformasjonV2PortTypeMock;

@Configuration
public class HenvendelseInformasjonV2EndpointConfig {

    public static final String HENVENDELSEINFORMASJONV2_KEY = "start.henvendelseinformasjonv2.withmock";

    @Bean
    public HenvendelseInformasjonV2PortType henvendelseInformasjonV2PortType() {
        return createSwitcher(createHenvendelseInformasjonV2PortType(), createHenvendelseInformasjonV2PortTypeMock(), HENVENDELSEINFORMASJONV2_KEY, HenvendelseInformasjonV2PortType.class);
    }

    private static HenvendelseInformasjonV2PortType createHenvendelseInformasjonV2PortType() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        return proxyFactoryBean.create(HenvendelseInformasjonV2PortType.class);
    }
}
