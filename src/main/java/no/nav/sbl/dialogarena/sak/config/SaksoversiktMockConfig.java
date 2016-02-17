package no.nav.sbl.dialogarena.sak.config;


import no.nav.sbl.dialogarena.saksoversikt.service.mock.MockContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {MockContext.class})
public class SaksoversiktMockConfig {
}
