package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable;

import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktRequest;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktResponse;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.YtelseskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktRequest;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktResponse;
import no.nav.modig.modia.ping.PingResult;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.mockErTillattOgSlaattPaaForKey;

@Configuration
public class KontrakterConsumerConfigResolver {

    @Inject
    private Wrapper<YtelseskontraktServiceBi> ytelseskontraktService;

    @Inject
    private Wrapper<YtelseskontraktServiceBi> ytelseskontraktMock;

    @Inject
    private Wrapper<OppfolgingskontraktServiceBi> oppfolgingskontraktService;

    @Inject
    private Wrapper<OppfolgingskontraktServiceBi> oppfolgingskontraktMock;

    @Bean
    public YtelseskontraktServiceBi ytelseskontraktServiceBi() {
        return new YtelseskontraktServiceBi() {
            @Override
            public YtelseskontraktResponse hentYtelseskontrakter(YtelseskontraktRequest request) {
                if (mockErTillattOgSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return ytelseskontraktMock.wrappedObject.hentYtelseskontrakter(request);
                }
                return ytelseskontraktService.wrappedObject.hentYtelseskontrakter(request);
            }

            @Override
            public PingResult ping() {
                if (mockErTillattOgSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return ytelseskontraktMock.wrappedObject.ping();
                }
                return ytelseskontraktService.wrappedObject.ping();
            }
        };
    }

    @Bean
    public OppfolgingskontraktServiceBi oppfolgingskontraktServiceBi() {
        return new OppfolgingskontraktServiceBi() {
            @Override
            public OppfolgingskontraktResponse hentOppfolgingskontrakter(OppfolgingskontraktRequest request) {
                if (mockErTillattOgSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return oppfolgingskontraktMock.wrappedObject.hentOppfolgingskontrakter(request);
                }
                return oppfolgingskontraktService.wrappedObject.hentOppfolgingskontrakter(request);
            }

            @Override
            public PingResult ping() {
                if (mockErTillattOgSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return oppfolgingskontraktMock.wrappedObject.ping();
                }
                return oppfolgingskontraktService.wrappedObject.ping();
            }
        };
    }

}
