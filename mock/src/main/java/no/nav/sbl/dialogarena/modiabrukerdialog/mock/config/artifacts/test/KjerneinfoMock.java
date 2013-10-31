package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.test;

import no.nav.behandlebrukerprofil.consumer.BehandleBrukerprofilServiceBi;
import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import no.nav.sykmeldingsperioder.widget.SykepengerWidgetService;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.test.BehandleBrukerprofilServiceBiMock.getBehandleBrukerprofilServiceBiMock;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.test.BrukerprofilServiceBiMock.getBrukerprofilServiceBiMock;

@Configuration
public class KjerneinfoMock {

    @Bean
    public PersonKjerneinfoServiceBi personKjerneinfoServiceBi() {
        return PersonKjerneinfoServiceBiMock.getPersonKjerneinfoServiceBiMock();
    }

    @Bean
    public BrukerprofilServiceBi brukerprofilServiceBi() throws HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning, HentKontaktinformasjonOgPreferanserPersonIkkeFunnet {
        return getBrukerprofilServiceBiMock();
    }

    @Bean
    public BehandleBrukerprofilServiceBi behandleBrukerprofilServiceBi() {
        return getBehandleBrukerprofilServiceBiMock();
    }

    @Bean
    public SykepengerWidgetService sykepengerWidgetService() {
        return SykepengerWidgetServiceMock.getSykepengerWidgetServiceMock();
    }

    @Bean
    public KodeverkmanagerBi kodeverkmanagerBi() {
        return KodeverkmanagerBiMock.getKodeverkmanagerBiMock();
    }

}
