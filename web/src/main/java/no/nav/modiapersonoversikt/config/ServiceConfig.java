package no.nav.modiapersonoversikt.config;

import no.nav.common.client.axsys.AxsysClient;
import no.nav.common.client.nom.NomClient;
import no.nav.common.sts.SystemUserTokenProvider;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.consumer.dkif.Dkif;
import no.nav.modiapersonoversikt.consumer.dkif.DkifServiceImpl;
import no.nav.modiapersonoversikt.consumer.dkif.DkifServiceRestImpl;
import no.nav.modiapersonoversikt.legacy.api.domain.bidragsak.generated.apis.BidragSakControllerApi;
import no.nav.modiapersonoversikt.legacy.api.service.psak.PsakService;
import no.nav.modiapersonoversikt.legacy.api.service.saker.SakerService;
import no.nav.modiapersonoversikt.service.PsakServiceImpl;
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService;
import no.nav.modiapersonoversikt.service.ansattservice.AnsattServiceImpl;
import no.nav.modiapersonoversikt.service.saker.SakerServiceImpl;
import no.nav.modiapersonoversikt.service.saker.mediation.BidragApiClient;
import no.nav.modiapersonoversikt.service.saker.mediation.SakApiGatewayImpl;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.DigitalKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.PensjonSakV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * MODIA ønsker å selv wire inn sine komponenters kontekster for å ha full kontroll over springoppsettet.
 */
@Configuration
@EnableScheduling
public class ServiceConfig {
    @Bean
    public SakerService sakerService() {
        return new SakerServiceImpl();
    }

    @Bean
    public SakApiGatewayImpl sakApiGateway(SystemUserTokenProvider stsService) {
        return new SakApiGatewayImpl(
                EnvironmentUtils.getRequiredProperty("SAK_ENDPOINTURL"),
                stsService
        );
    }

    @Bean
    public BidragSakControllerApi bidragSakControllerApi() {
        return new BidragSakControllerApi(
                EnvironmentUtils.getRequiredProperty("BISYS_BASEURL"),
                BidragApiClient.INSTANCE.getClient()
        );
    }

    @Bean
    public PsakService psakService(PensjonSakV1 pensjonSakV1) {
        return new PsakServiceImpl(pensjonSakV1);
    }

    @Bean(name = "DkifSoap")
    public Dkif.Service defaultDkifService(DigitalKontaktinformasjonV1 dkifV1) {
        return new DkifServiceImpl(dkifV1);
    }

    @Bean(name = "DkifRest")
    public Dkif.Service restDkifService() {
        return new DkifServiceRestImpl(
                EnvironmentUtils.getRequiredProperty("DKIF_REST_URL")
        );
    }
}
