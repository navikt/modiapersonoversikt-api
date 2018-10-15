package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;


import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.kjerneinfo.consumer.organisasjon.OrganisasjonService;
import no.nav.kjerneinfo.kontrakter.oppfolging.loader.OppfolgingsLoader;
import no.nav.kjerneinfo.kontrakter.ytelser.YtelseskontrakterLoader;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.YtelseskontraktServiceBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppfolgingsinfo.Oppfolgingsinfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo.OppfolgingsinfoService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingService;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.PleiepengerService;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.PleiepengerServiceImpl;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.mock.PleiepengerMockFactory;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.utbetalinger.UtbetalingerService;
import no.nav.sykmeldingsperioder.foreldrepenger.loader.ForeldrepengerLoader;
import no.nav.sykmeldingsperioder.loader.SykmeldingsperiodeLoader;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.HentPleiepengerettighetSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.HentPleiepengerettighetUgyldigIdentNr;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.PleiepengerV1;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.meldinger.WSHentPleiepengerettighetRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        return mock(SykmeldingsperiodeLoader.class);
    }

    @Bean
    public SykepengerServiceBi sykepengerServiceBi() {
        return mock(SykepengerServiceBi.class);
    }

    @Bean
    public PleiepengerService pleiepengerServiceBi() throws HentPleiepengerettighetUgyldigIdentNr, HentPleiepengerettighetSikkerhetsbegrensning {
        PleiepengerV1 pleiepengerV1 = mock(PleiepengerV1.class);
        when(pleiepengerV1.hentPleiepengerettighet(any(WSHentPleiepengerettighetRequest.class)))
                .thenReturn(PleiepengerMockFactory.createWsHentPleiepengerListeResponse());
        return new PleiepengerServiceImpl(pleiepengerV1);
    }

    @Bean
    public OrganisasjonService organisasjonService() {
        OrganisasjonService mock = mock(OrganisasjonService.class);
        when(mock.hentNoekkelinfo(anyString())).thenReturn(Optional.empty());
        return mock;
    }

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

    @Bean
    public OppfolgingsinfoService oppfolgingsinfoService() {
        OppfolgingsinfoService mock = mock(OppfolgingsinfoService.class);
        when(mock.hentOppfolgingsinfo(anyString())).thenReturn(new Oppfolgingsinfo(true)
                .withVeileder(new Saksbehandler("Harald", "Hårfagre", "z991028"))
                .withOppfolgingsenhet(new AnsattEnhet("0118", "NAV Aremark")));
        return mock;
    }

    @Bean
    public UnleashService unleashService() {
        return mock(UnleashService.class);
    }
}
