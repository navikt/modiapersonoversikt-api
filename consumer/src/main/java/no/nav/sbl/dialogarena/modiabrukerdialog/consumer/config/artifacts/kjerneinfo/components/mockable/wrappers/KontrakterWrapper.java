package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.wrappers;

import no.nav.kjerneinfo.kontrakter.config.KontrakterPanelConfig;
import no.nav.kontrakter.consumer.fim.config.OppfolgingskontraktConsumerConfig;
import no.nav.kontrakter.consumer.fim.config.YtelseskontraktConsumerConfig;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.YtelseskontraktServiceBi;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v1.YtelseskontraktPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import javax.inject.Named;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.mockableimpl.KontrakterConsumerConfigImpl.createOppfolgingskontraktService;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.mockableimpl.KontrakterConsumerConfigImpl.createYtelseskontraktService;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.OppfolgingskontraktServiceBiMock.getOppfolgingskontraktServiceBiMock;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.YtelseskontraktServiceBiMock.getYtelseskontraktServiceBiMock;

@Configuration
@Import({
        KontrakterPanelConfig.class,
        OppfolgingskontraktConsumerConfig.class,
        YtelseskontraktConsumerConfig.class})
public class KontrakterWrapper {

    @Inject
    @Named("ytelseskontraktPortType")
    private YtelseskontraktPortType ytelseskontraktPortType;

    @Inject
    @Named("selftestYtelseskontraktPortType")
    private YtelseskontraktPortType selftestYtelseskontraktPortType;

    @Inject
    @Named("oppfolgingPortType")
    private OppfoelgingPortType oppfoelgingPortType;

    @Inject
    @Named("selftestOppfolgingPortType")
    private OppfoelgingPortType selftestOppfoelgingPortType;

    @Bean
    public YtelseskontraktServiceBi ytelseskontraktService() {
        return createYtelseskontraktService(ytelseskontraktPortType, selftestYtelseskontraktPortType);
    }

    @Bean
    public YtelseskontraktServiceBi ytelseskontraktMock() {
        return getYtelseskontraktServiceBiMock();
    }

    @Bean
    public OppfolgingskontraktServiceBi oppfolgingskontraktService() {
        return createOppfolgingskontraktService(oppfoelgingPortType, selftestOppfoelgingPortType);
    }

    @Bean
    public OppfolgingskontraktServiceBi oppfolgingskontraktMock() {
        return getOppfolgingskontraktServiceBiMock();
    }

}
