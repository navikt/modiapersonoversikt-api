package no.nav.kjerneinfo.consumer.organisasjon;

import no.nav.common.sts.SystemUserTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrganisasjonConsumerConfig {

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
}
