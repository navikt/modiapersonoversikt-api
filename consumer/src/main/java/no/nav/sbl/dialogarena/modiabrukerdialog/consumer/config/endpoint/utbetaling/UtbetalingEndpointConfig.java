package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.utbetaling;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.tjeneste.virksomhet.utbetaling.v1.HentUtbetalingsinformasjonPeriodeIkkeGyldig;
import no.nav.tjeneste.virksomhet.utbetaling.v1.UtbetalingV1;
import no.nav.tjeneste.virksomhet.utbetaling.v1.meldinger.WSHentUtbetalingsinformasjonRequest;
import no.nav.tjeneste.virksomhet.utbetaling.v1.meldinger.WSHentUtbetalingsinformasjonResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.mockErTillattOgSlaattPaaForKey;

@Configuration
public class UtbetalingEndpointConfig {

    public static final String UTBETALING_KEY = "start.utbetaling.withmock";

    @Inject
    @Qualifier("utbetalingPortTypeWrapperMock")
    private Wrapper<UtbetalingV1> mockedPortTypeWrapper;

    @Inject
    @Qualifier("utbetalingPortTypeWrapper")
    private Wrapper<UtbetalingV1> portTypeWrapper;

    @Bean(name = "utbetalingPortType")
    public UtbetalingV1 utbetalingPortType() {
        return new UtbetalingV1() {

            @Override
            public void ping() {

            }

            @Cacheable("utbetalingCache")
            @Override
            public WSHentUtbetalingsinformasjonResponse hentUtbetalingsinformasjon(WSHentUtbetalingsinformasjonRequest request) throws HentUtbetalingsinformasjonPeriodeIkkeGyldig {
                if (mockErTillattOgSlaattPaaForKey(UTBETALING_KEY)) {
                    return mockedPortTypeWrapper.wrappedObject.hentUtbetalingsinformasjon(request);
                }
                return portTypeWrapper.wrappedObject.hentUtbetalingsinformasjon(request);
            }
        };
    }

}
