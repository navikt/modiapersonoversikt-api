package no.nav.sbl.dialogarena.saksoversikt.service.mock;


import no.nav.sbl.dialogarena.saksoversikt.service.service.*;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.TemagrupperHenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.mock;

@Configuration
public class MockContext {
    @Bean
    public Filter sakOgBehandlingFilter() {
        return mock(Filter.class);
    }

    @Bean
    public SakOgBehandlingService sakOgBehandlingService() {
        return mock(SakOgBehandlingService.class);
    }

    @Bean
    public HenvendelseService henvendelseService() {
        return mock(HenvendelseService.class);
    }

    @Bean
    public GsakSakerService gSakService() {
        return mock(GsakSakerService.class);
    }

    @Bean
    public InnsynJournalService joarkService() {
        return mock(InnsynJournalService.class);
    }

    @Bean
    public PesysService pesysService() {
        return mock(PesysService.class);
    }

    @Bean
    public SaksService saksService() {
        return mock(SaksService.class);
    }

    @Bean
    public GsakSakerService gsakSakerService() {
        return mock(GsakSakerService.class);
    }

    @Bean
    public FodselnummerAktorService fodselnummerAktorService() {
        return mock(FodselnummerAktorService.class);
    }

    @Bean
    public DataFletter dataFletter() {
        return mock(DataFletter.class);
    }

    @Bean
    public DokumentMetadataService dokumentMetadataService() {
        return mock(DokumentMetadataService.class);
    }

    @Bean
    public TemagrupperHenter temagrupperHenter() {
        return mock(TemagrupperHenter.class);
    }

    @Bean
    public SakstemaGrupperer sakstemaGrupperer() {
        return mock(SakstemaGrupperer.class);
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(50);
    }

    @Bean
    public PDFConverterService pdfConverterService() {
        return mock(PDFConverterService.class);
    }

    @Bean
    public BulletproofKodeverkService kodeverkService() {
        return mock(BulletproofKodeverkService.class);
    }
}
