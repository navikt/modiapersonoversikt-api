package no.nav.kjerneinfo.consumer.organisasjon;

import no.nav.common.health.HealthCheckResult;
import no.nav.common.health.selftest.SelfTestCheck;
import no.nav.common.sts.SystemUserTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrganisasjonConsumerConfig {

    private static final String ORG_NUMMER_NAV = "990983666";
    private static final String ORG_NAVN_NAV = "NAV IKT";


    @Autowired
    private SystemUserTokenProvider systemUserTokenProvider;

    @Bean
    public OrganisasjonService organisasjonService() {
        return new OrganisasjonServiceImpl(organisasjonV1RestClient());
    }

    @Bean
    public OrganisasjonV1RestClient organisasjonV1RestClient() {
        return new OrganisasjonRestClientImpl(systemUserTokenProvider);
    }

    @Bean
    public SelfTestCheck eregOrganisasjonCheck() {
        return new SelfTestCheck(
                String.format("EREG Organisasjon  %s", ORG_NUMMER_NAV),
                false,
                () -> {
                    try {
                        OrganisasjonResponse organisasjonResponse = organisasjonV1RestClient().hentKjernInfoFraRestClient(ORG_NUMMER_NAV);
                        if (organisasjonResponse.getNavn().getNavnelinje1().equals(ORG_NAVN_NAV))
                            return HealthCheckResult.healthy();
                        else
                            return HealthCheckResult.unhealthy("Feil ved henting av data. Sjekke tjeneste logg");
                    } catch (Exception e) {
                        return HealthCheckResult.unhealthy(e);
                    }
                }
        );
    }
}
