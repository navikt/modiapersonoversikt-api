package no.nav.sbl.dialogarena.modiabrukerdialog.sak.mock;

import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.BulletproofKodeverkService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        when(kodeverkClient.hentFoersteTermnavnForKode(TEMA_1, BulletproofKodeverkService.ARKIVTEMA)).thenReturn("Dagpenger");
        when(kodeverkClient.hentFoersteTermnavnForKode(TEMA_2, BulletproofKodeverkService.ARKIVTEMA)).thenReturn("Foreldrepenger");
        when(kodeverkClient.hentFoersteTermnavnForKode(TEMA_3, BulletproofKodeverkService.ARKIVTEMA)).thenReturn("Arbeidsavklaringspenger");
        return kodeverkClient;
    }

    @Bean
    public BulletproofKodeverkService kodeverkWrapper() {
        return new BulletproofKodeverkService();
    }
}
