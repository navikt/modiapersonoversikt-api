package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseLesService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.ConsumerContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.cache.CacheConfiguration;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.plukkoppgave.PlukkOppgaveService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.plukkoppgave.PlukkOppgaveServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@Import({
        ConsumerContext.class,
        CacheConfiguration.class
})
public class ApplicationContextBeans {

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public PlukkOppgaveService plukkOppgaveService(OppgaveBehandlingService oppgaveBehandlingService, PersonKjerneinfoServiceBi personKjerneinfoServiceBi, Tilgangskontroll tilgangskontroll) {
        return new PlukkOppgaveServiceImpl(
                oppgaveBehandlingService,
                personKjerneinfoServiceBi,
                tilgangskontroll
        );
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new JacksonConfig().getContext(null);
    }

    @Bean
    public Tilgangskontroll tilgangskontroll(
            LDAPService ldapService,
            GOSYSNAVansatt ansattService, // TODO undersøk om denne kan erstattes med axsys
            HenvendelseLesService henvendelseLesService
    ) {
        TilgangskontrollContext context = new TilgangskontrollContextImpl(
                null, // TODO send inn AbacClient her
                ldapService,
                ansattService,
                henvendelseLesService
        );
        return new Tilgangskontroll(context);
    }
}
