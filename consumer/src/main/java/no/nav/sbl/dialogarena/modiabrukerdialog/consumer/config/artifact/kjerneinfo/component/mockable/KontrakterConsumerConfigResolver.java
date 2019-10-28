package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable;

import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.support.DefaultOppfolgingskontraktService;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktRequest;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktResponse;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.YtelseskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.support.DefaultYtelseskontraktService;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktRequest;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktResponse;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

@Configuration
public class KontrakterConsumerConfigResolver {

    @Inject
    private Wrapper<DefaultYtelseskontraktService> ytelseskontraktService;

    @Inject
    private Wrapper<YtelseskontraktServiceBi> ytelseskontraktMock;

    @Inject
    private Wrapper<DefaultOppfolgingskontraktService> oppfolgingskontraktService;

    @Inject
    private Wrapper<OppfolgingskontraktServiceBi> oppfolgingskontraktMock;

    @Bean
    public YtelseskontraktServiceBi ytelseskontraktServiceBi() {
        return new YtelseskontraktServiceBi() {
            @Override
            public YtelseskontraktResponse hentYtelseskontrakter(YtelseskontraktRequest request) {
                return ytelseskontraktService.wrappedObject.hentYtelseskontrakter(request);
            }

        };
    }

    @Bean
    public OppfolgingskontraktServiceBi oppfolgingskontraktServiceBi() {
        return new OppfolgingskontraktServiceBi() {
            @Override
            public OppfolgingskontraktResponse hentOppfolgingskontrakter(OppfolgingskontraktRequest request) {
                return oppfolgingskontraktService.wrappedObject.hentOppfolgingskontrakter(request);
            }
        };
    }

}
