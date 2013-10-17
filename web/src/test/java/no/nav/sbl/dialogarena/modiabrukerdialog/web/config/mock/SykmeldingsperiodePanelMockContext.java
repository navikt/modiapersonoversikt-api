package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.modig.modia.ping.PingResult;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerRequest;
import no.nav.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerResponse;
import no.nav.sykmeldingsperioder.loader.SykmeldingsperiodeLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SykmeldingsperiodePanelMockContext {

    @Bean
    public SykmeldingsperiodeLoader sykmeldingsperiodeLoader() {
        return new SykmeldingsperiodeLoader();
    }

    @Bean
    public SykepengerServiceBi sykepengerServiceBi() {
        return new SykepengerServiceBi() {
            @Override
            public SykepengerResponse hentSykmeldingsperioder(SykepengerRequest request) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public PingResult ping() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

}
