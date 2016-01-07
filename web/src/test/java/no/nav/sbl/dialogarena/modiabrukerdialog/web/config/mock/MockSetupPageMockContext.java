package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.CmsSkrivestotte;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.SkrivestotteSok;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class MockSetupPageMockContext {

    @Bean
    public CmsSkrivestotte cmsSkrivestotte() {
        return mock(CmsSkrivestotte.class);
    }

    @Bean
    public SkrivestotteSok skrivestotteSok() {
        return mock(SkrivestotteSok.class);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return mock(ObjectMapper.class);
    }

}
