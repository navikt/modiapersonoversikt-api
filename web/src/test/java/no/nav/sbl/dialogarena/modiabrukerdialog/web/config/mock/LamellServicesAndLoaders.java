package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;


import no.nav.kjerneinfo.kontrakter.oppfolging.loader.OppfolgingsLoader;
import no.nav.kjerneinfo.kontrakter.ytelser.YtelseskontrakterLoader;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.YtelseskontraktServiceBi;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.PleiepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.utbetalinger.UtbetalingerService;
import no.nav.sykmeldingsperioder.foreldrepenger.loader.ForeldrepengerLoader;
import no.nav.sykmeldingsperioder.loader.SykmeldingsperiodeLoader;
import no.nav.sykmeldingsperioder.pleiepenger.loader.PleiepengerLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class LamellServicesAndLoaders {

    @Bean
    public OppfolgingsLoader modelLoader() {
        return mock(OppfolgingsLoader.class);
    }

    @Bean
    public OppfolgingskontraktServiceBi serviceBi() {
        return mock(OppfolgingskontraktServiceBi.class);
    }

    @Bean
    public YtelseskontraktServiceBi serviceBi2() {
        return mock(YtelseskontraktServiceBi.class);
    }

    @Bean
    public YtelseskontrakterLoader loader2() {
        return mock(YtelseskontrakterLoader.class);
    }

    @Bean
    public ForeldrepengerLoader foreldrepengerLoader() {
        return mock(ForeldrepengerLoader.class);
    }

    @Bean
    public ForeldrepengerServiceBi foreldrepengerServiceBi() {
        return mock(ForeldrepengerServiceBi.class);
    }

    @Bean
    public SykmeldingsperiodeLoader sykmeldingsperiodeLoader() {
        return new SykmeldingsperiodeLoader();
    }

    @Bean
    public SykepengerServiceBi sykepengerServiceBi() {
        return mock(SykepengerServiceBi.class);
    }

    @Bean
    public PleiepengerServiceBi pleiepengerServiceBi() {
        return mock(PleiepengerServiceBi.class);
    }

    @Bean
    public PleiepengerLoader pleiepengerLoader() { return new PleiepengerLoader(); }

    @Bean
    public UtbetalingerService utbetalingerService() {
        return mock(UtbetalingerService.class);
    }

    @Bean
    public HenvendelseBehandlingService henvendelseBehandlingService() {
        return mock(HenvendelseBehandlingService.class);
    }

    @Bean
    public GsakService gsakService() {
        return mock(GsakService.class);
    }

    @Bean
    public SaksbehandlerInnstillingerService saksbehandlerInnstillingerService() {
        return mock(SaksbehandlerInnstillingerService.class);
    }

    @Bean
    public EnforcementPoint pep() {
        return mock(EnforcementPoint.class);
    }
}
