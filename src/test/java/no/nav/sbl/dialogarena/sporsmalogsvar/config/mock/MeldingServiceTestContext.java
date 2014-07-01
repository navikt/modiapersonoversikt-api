package no.nav.sbl.dialogarena.sporsmalogsvar.config.mock;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.virksomhet.tjenester.sak.v1.Sak;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;

import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.TestUtils.createMockSaksliste;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.TestUtils.opprettMockMelding;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class MeldingServiceTestContext {

    @Bean
    public MeldingService meldingService() {
        MeldingService meldingService = mock(MeldingService.class);
        when(meldingService.hentSakerForBruker(anyString())).thenReturn(createMockSaksliste());
        when(meldingService.hentMeldinger(anyString())).thenReturn(new ArrayList<>(Arrays.asList(opprettMockMelding())));
        return meldingService;
    }

    @Bean
    public HenvendelsePortType henvendelsePortType(){
        return mock(HenvendelsePortType.class);
    }

    @Bean
    public Sak sakWs(){
        return mock(Sak.class);
    }

}
