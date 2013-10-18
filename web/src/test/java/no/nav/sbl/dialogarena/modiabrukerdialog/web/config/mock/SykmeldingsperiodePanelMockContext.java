package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import no.nav.sykmeldingsperioder.loader.SykmeldingsperiodeLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class SykmeldingsperiodePanelMockContext {

    @Bean
    public SykmeldingsperiodeLoader sykmeldingsperiodeLoader() {
        return new SykmeldingsperiodeLoader();
    }

    @Bean
    public SykepengerServiceBi sykepengerServiceBi() {
        return mock(SykepengerServiceBi.class);
    }

}
