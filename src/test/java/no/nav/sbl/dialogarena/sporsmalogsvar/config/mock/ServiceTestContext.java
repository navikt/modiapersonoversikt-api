package no.nav.sbl.dialogarena.sporsmalogsvar.config.mock;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.JoarkJournalforingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.ValgtEnhetService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.binding.BehandleJournalV2;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.virksomhet.tjenester.sak.v1.Sak;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.TestUtils.createMockSaksliste;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.TestUtils.opprettMeldingEksempel;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class ServiceTestContext {

    @Bean
    public GsakService gsakService() {
        GsakService gsakService = mock(GsakService.class);
        when(gsakService.hentSakerForBruker(anyString())).thenReturn(createMockSaksliste());
        return gsakService;
    }

    @Bean
    public HenvendelseService henvendelseService() {
        HenvendelseService henvendelseService = mock(HenvendelseService.class);
        when(henvendelseService.hentMeldinger(anyString())).thenReturn(asList(opprettMeldingEksempel()));
        return henvendelseService;
    }

    @Bean
    public JoarkJournalforingService joarkService() {
        return mock(JoarkJournalforingService.class);
    }

    @Bean
    public HenvendelsePortType henvendelsePortType() {
        return mock(HenvendelsePortType.class);
    }

    @Bean
    public BehandleHenvendelsePortType behandleHenvendelsePortType() {
        return mock(BehandleHenvendelsePortType.class);
    }

    @Bean
    public BehandleJournalV2 behandleJournalV2() {
        return mock(BehandleJournalV2.class);
    }

    @Bean
    public OppgavebehandlingV3 oppgavebehandling() {
        return mock(OppgavebehandlingV3.class);
    }

    @Bean
    public Sak sakWs() {
        return mock(Sak.class);
    }

    @Bean
    public GOSYSNAVansatt GOSYSNAVansatt() {
        return mock(GOSYSNAVansatt.class);
    }

    @Bean
    public ValgtEnhetService valgtEnhetService() {
        return mock(ValgtEnhetService.class);
    }

}
