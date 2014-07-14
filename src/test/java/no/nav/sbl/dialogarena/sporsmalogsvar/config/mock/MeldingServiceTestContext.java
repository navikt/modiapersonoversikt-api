package no.nav.sbl.dialogarena.sporsmalogsvar.config.mock;

import no.nav.sbl.dialogarena.pdf.HandleBarKjoerer;
import no.nav.sbl.dialogarena.pdf.HtmlGenerator;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.PdfUtils;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.virksomhet.tjenester.sak.v1.Sak;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;

import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.TestUtils.createMockSaksliste;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.TestUtils.opprettMeldingEksempel;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class MeldingServiceTestContext {

    @Bean
    public PdfUtils pdfUtils() {
        return new PdfUtils();
    }

    @Bean
    public HtmlGenerator htmlGenerator() {
        return new HandleBarKjoerer();
    }

    @Bean
    public MeldingService meldingService() {
        MeldingService meldingService = mock(MeldingService.class);
        when(meldingService.hentSakerForBruker(anyString())).thenReturn(createMockSaksliste());
        when(meldingService.hentMeldinger(anyString())).thenReturn(new ArrayList<>(Arrays.asList(opprettMeldingEksempel())));
        return meldingService;
    }

    @Bean
    public HenvendelsePortType henvendelsePortType(){
        return mock(HenvendelsePortType.class);
    }

    @Bean
    public BehandleHenvendelsePortType behandleHenvendelsePortType(){
        return mock(BehandleHenvendelsePortType.class);
    }

    @Bean
    public Sak sakWs(){
        return mock(Sak.class);
    }

}
