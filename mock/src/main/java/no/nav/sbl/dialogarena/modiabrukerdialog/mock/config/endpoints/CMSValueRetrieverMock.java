package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.modig.content.ValueRetriever;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class CMSValueRetrieverMock {
    public static final String CMS_KEY = "start.cms.withmock";

    @Bean
    public ValueRetriever getValueRetrieverMock() {
        ValueRetriever mock = mock(ValueRetriever.class);
        when(mock.getValueOf(anyString(), anyString())).thenReturn("Tekst fra cms-mock");
        return mock;
    }

}
