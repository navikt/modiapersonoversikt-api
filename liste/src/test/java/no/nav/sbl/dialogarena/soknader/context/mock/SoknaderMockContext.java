package no.nav.sbl.dialogarena.soknader.context.mock;

import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import no.nav.sbl.dialogarena.soknader.service.SoknaderService;
import no.nav.sbl.dialogarena.soknader.util.SoknadBuilder;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.GAMMEL_FERDIG;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.MOTTATT;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.NYLIG_FERDIG;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.UNDER_BEHANDLING;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class SoknaderMockContext {

    @Bean
    public SoknaderService soknaderService(){
        SoknaderService service = mock(SoknaderService.class);
        List<Soknad> soknadList = soknaderDataSetMocked();
        when(service.getSoknader(anyString())).thenReturn(soknadList);
        return service;
    }

    private List<Soknad> soknaderDataSetMocked() {
        return asList(
                createMottattSoknad(),
                createUnderBehandlingSoknad(),
                createNyligFerdigSoknad(),
                createGammelFerdigSoknad(),
                createMottattSoknadUtenBehandlingsTid(),
                createMottattSoknadUtenInnsendtDato()
        );
    }

    private Soknad createGammelFerdigSoknad() {
        return new SoknadBuilder()
                .withTittel("Dagpenger")
                .withInnsendtDato(new DateTime(2013, 1, 1, 11, 11))
                .underBehandlingStartDato(new DateTime(2013, 2, 2, 11, 12))
                .withFerdigDato(now().minusDays(AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED + 1))
                .withSoknadStatus(GAMMEL_FERDIG)
                .withNormertBehandlingsTid("14 dager")
                .build();
    }

    private Soknad createNyligFerdigSoknad() {
        return new SoknadBuilder()
                .withTittel("Sykepenger")
                .withInnsendtDato(new DateTime(2013, 8, 19, 11, 11))
                .underBehandlingStartDato(new DateTime(2013, 9, 10, 11, 12))
                .withFerdigDato(now().minusDays(AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED - 1))
                .withSoknadStatus(NYLIG_FERDIG)
                .withNormertBehandlingsTid("10 dager")
                .build();
    }

    private Soknad createUnderBehandlingSoknad() {
        return new SoknadBuilder()
                .withInnsendtDato(now().minusDays(5))
                .underBehandlingStartDato(now().minusDays(2))
                .withTittel("Uføre")
                .withNormertBehandlingsTid("200 dager")
                .withSoknadStatus(UNDER_BEHANDLING)
                .build();
    }

    private Soknad createMottattSoknad() {
        return new SoknadBuilder()
                .withInnsendtDato(now())
                .withTittel("Dagpenger")
                .withNormertBehandlingsTid("10 dager")
                .withSoknadStatus(MOTTATT)
                .build();
    }

    private Soknad createMottattSoknadUtenBehandlingsTid() {
        return new SoknadBuilder()
                .withInnsendtDato(now())
                .withTittel("Dagpenger uten behandlingstid")
                .withSoknadStatus(MOTTATT)
                .withNormertBehandlingsTid("")
                .build();
    }

    private Soknad createMottattSoknadUtenInnsendtDato() {
        return new SoknadBuilder()
                .withInnsendtDato(null)
                .withTittel("Dagpenger uten innsendt dato")
                .withSoknadStatus(MOTTATT)
                .withNormertBehandlingsTid("")
                .withFerdigDato(now().minusDays(AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED - 1))
                .build();
    }

}
