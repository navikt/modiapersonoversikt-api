package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.henvendelseaktivitet;

import no.nav.tjeneste.domene.brukerdialog.henvendelse.aktivitet.v2.HenvendelseAktivitetV2PortType;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelseAktivitetV2PortTypeMock.createHenvendelseAktivitetV2PortTypeMock;

@Configuration
public class HenvendelseAktivitetV2EndpointConfig {

    public static final String HENVENDELSE_AKTIVITET_V2_KEY = "start.henvendelseaktivitetv2.withmock";

    @Bean
    public HenvendelseAktivitetV2PortType henvendelseAktivitetV2PortType() {
        return createSwitcher(createHenvendelseAktivitetV2PortType(), createHenvendelseAktivitetV2PortTypeMock(), HENVENDELSE_AKTIVITET_V2_KEY, HenvendelseAktivitetV2PortType.class);
    }

    private static HenvendelseAktivitetV2PortType createHenvendelseAktivitetV2PortType() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        return proxyFactoryBean.create(HenvendelseAktivitetV2PortType.class);
    }
}
