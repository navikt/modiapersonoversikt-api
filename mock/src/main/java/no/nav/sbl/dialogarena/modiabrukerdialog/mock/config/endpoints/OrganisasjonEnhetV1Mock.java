package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.OrganisasjonEnhetV1;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrganisasjonEnhetV1Mock {

    @Bean
    public static OrganisasjonEnhetV1 organisasjonEnhetV1() {
        return Mockito.mock(OrganisasjonEnhetV1.class);
    }

}
