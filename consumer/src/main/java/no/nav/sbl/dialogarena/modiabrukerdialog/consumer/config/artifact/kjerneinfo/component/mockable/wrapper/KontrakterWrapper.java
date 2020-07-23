package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.wrapper;

import no.nav.kontrakter.consumer.fim.config.OppfolgingskontraktConsumerConfig;
import no.nav.kontrakter.consumer.fim.config.YtelseskontraktConsumerConfig;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.support.DefaultOppfolgingskontraktService;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.support.DefaultYtelseskontraktService;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.YtelseskontraktV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.mockableimpl.KontrakterConsumerConfigImpl.createOppfolgingskontraktService;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.mockableimpl.KontrakterConsumerConfigImpl.createYtelseskontraktService;

@Configuration
@Import({
        OppfolgingskontraktConsumerConfig.class,
        YtelseskontraktConsumerConfig.class})
public class KontrakterWrapper {

    @Inject
    private YtelseskontraktV3 ytelseskontraktPortType;


    @Inject
    private OppfoelgingPortType oppfoelgingPortType;

    @Bean
    public DefaultYtelseskontraktService ytelseskontraktService() {
        return createYtelseskontraktService(ytelseskontraktPortType);
    }

    @Bean
    public DefaultOppfolgingskontraktService oppfolgingskontraktService() {
        return createOppfolgingskontraktService(oppfoelgingPortType);
    }
}
