package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable;

import no.nav.kjerneinfo.kontrakter.config.KontrakterPanelConfig;
import no.nav.kontrakter.consumer.fim.config.OppfolgingskontraktConsumerConfig;
import no.nav.kontrakter.consumer.fim.config.YtelseskontraktConsumerConfig;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktRequest;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktResponse;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.YtelseskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktRequest;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktResponse;
import no.nav.modig.modia.ping.PingResult;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v1.YtelseskontraktPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import javax.inject.Named;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.mockableimpl.KontrakterConsumerConfigImpl.createOppfolgingskontraktService;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.mockableimpl.KontrakterConsumerConfigImpl.createYtelseskontraktService;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockUtil.mockErSlaattPaaForKey;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockUtil.mockSetupErTillatt;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.OppfolgingskontraktServiceBiMock.getOppfolgingskontraktServiceBiMock;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.YtelseskontraktServiceBiMock.getYtelseskontraktServiceBiMock;

@Configuration
@Import({
        KontrakterPanelConfig.class,
        OppfolgingskontraktConsumerConfig.class,
        YtelseskontraktConsumerConfig.class})
public class KontrakterConsumerConfigResolver {

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
    public YtelseskontraktServiceBi ytelseskontraktServiceBi() {
        final YtelseskontraktServiceBi ytelseskontraktService = createYtelseskontraktService(ytelseskontraktPortType, selftestYtelseskontraktPortType);
        final YtelseskontraktServiceBi ytelseskontraktServiceBiMock = getYtelseskontraktServiceBiMock();
        return new YtelseskontraktServiceBi() {
            @Override
            public YtelseskontraktResponse hentYtelseskontrakter(YtelseskontraktRequest request) {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return ytelseskontraktServiceBiMock.hentYtelseskontrakter(request);
                }
                return ytelseskontraktService.hentYtelseskontrakter(request);
            }

            @Override
            public PingResult ping() {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return ytelseskontraktServiceBiMock.ping();
                }
                return ytelseskontraktService.ping();
            }
        };
    }

    @Bean
    public OppfolgingskontraktServiceBi oppfolgingskontraktServiceBi() {
        final OppfolgingskontraktServiceBi oppfolgingskontraktService = createOppfolgingskontraktService(oppfoelgingPortType, selftestOppfoelgingPortType);
        final OppfolgingskontraktServiceBi oppfolgingskontraktServiceBiMock = getOppfolgingskontraktServiceBiMock();
        return new OppfolgingskontraktServiceBi() {
            @Override
            public OppfolgingskontraktResponse hentOppfolgingskontrakter(OppfolgingskontraktRequest request) {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return oppfolgingskontraktServiceBiMock.hentOppfolgingskontrakter(request);
                }
                return oppfolgingskontraktService.hentOppfolgingskontrakter(request);
            }

            @Override
            public PingResult ping() {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return oppfolgingskontraktServiceBiMock.ping();
                }
                return oppfolgingskontraktService.ping();
            }
        };
    }

}
