package no.nav.sbl.dialogarena.saksoversikt.service.mock;

import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService.ARKIVTEMA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class KodeverkServiceConfigMock {

    public static final String TEMA_1 = "tema01";
    public static final String TEMA_2 = "tema02";
    public static final String TEMA_3 = "tema03";

    @Bean
    public KodeverkClient kodeverkClient() {
        KodeverkClient kodeverkClient = mock(KodeverkClient.class);
        when(kodeverkClient.hentFoersteTermnavnForKode(TEMA_1, ARKIVTEMA)).thenReturn("Dagpenger");
        when(kodeverkClient.hentFoersteTermnavnForKode(TEMA_2, ARKIVTEMA)).thenReturn("Foreldrepenger");
        when(kodeverkClient.hentFoersteTermnavnForKode(TEMA_3, ARKIVTEMA)).thenReturn("Arbeidsavklaringspenger");
        return kodeverkClient;
    }

    @Bean
    public BulletproofKodeverkService kodeverkWrapper() {
        return new BulletproofKodeverkService();
    }
}
