package no.nav.modiapersonoversikt.service.arbeidsfordeling;

import no.nav.common.types.identer.Fnr;
import no.nav.modiapersonoversikt.consumer.norg.NorgApi;
import no.nav.modiapersonoversikt.consumer.pdl.generated.HentAdressebeskyttelse;
import no.nav.modiapersonoversikt.consumer.pdl.generated.enums.AdressebeskyttelseGradering;
import no.nav.modiapersonoversikt.consumer.pdl.generated.hentadressebeskyttelse.Adressebeskyttelse;
import no.nav.modiapersonoversikt.consumer.skjermedePersoner.SkjermedePersonerApi;
import no.nav.modiapersonoversikt.service.arbeidsfordeling.ArbeidsfordelingService.ArbeidsfordelingException;
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService;
import no.nav.modiapersonoversikt.utils.PropertyRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;

import static no.nav.modiapersonoversikt.service.unleash.strategier.StrategyUtils.ENVIRONMENT_PROPERTY;
import static no.nav.modiapersonoversikt.utils.TestUtils.sneaky;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArbeidsfordelingServiceTest {
    @RegisterExtension
    static PropertyRule environment = new PropertyRule(ENVIRONMENT_PROPERTY, "n/a");

    private static final String FAGOMRADE = "BAR";
    private static final String OPPGAVETYPE = "SPM_OG_SVAR";
    private static final String UNDERKATEGORI = "ae0106:";
    private static final String GEOGRAFISK_TILKNYTNING = "0219";
    private static final Fnr PERSON = Fnr.of("11111111111");

    private final PdlOppslagService pdlOppslagService = mock(PdlOppslagService.class);
    private final NorgApi norgApi = mock(NorgApi.class);
    private final SkjermedePersonerApi egenAnsattService = mock(SkjermedePersonerApi.class);
    private ArbeidsfordelingService arbeidsfordelingService;

    @BeforeEach
    void setupMocks() {
        Mockito.reset(
                pdlOppslagService,
                norgApi,
                egenAnsattService
        );
        arbeidsfordelingService = new ArbeidsfordelingServiceImpl(norgApi, pdlOppslagService, egenAnsattService);
    }

    @Test
    @DisplayName("Kaster exception hvis kall mot PersondataService feiler")
    void kasterExceptionHvisKallMotPersondataServiceFeiler() {
        gitt_at_alt_fungerer();
        gitt_feil_ved_henting_av_geografisk_tilknytning();

        assertThrows(ArbeidsfordelingException.class, () -> arbeidsfordelingService.hentBehandlendeEnheter(FAGOMRADE, OPPGAVETYPE, PERSON, UNDERKATEGORI));
    }

    @Test
    @DisplayName("Kaster exception hvis kall mot Arbeidsfordeling feiler")
    void kasterExceptionHvisKallMotArbeidsfordelingFeiler() {
        gitt_at_alt_fungerer();
        gitt_feil_ved_henting_av_enheter();

        assertThrows(ArbeidsfordelingException.class, () -> arbeidsfordelingService.hentBehandlendeEnheter(FAGOMRADE, OPPGAVETYPE, PERSON, UNDERKATEGORI));
    }

    @Test
    @DisplayName("Arbeidsfordeling kalles med riktige argumenter og bruker REST")
    void kallerArbeidsfordelingMedRiktigeArgumenterMotNyTjeneste() {
        gitt_at_alt_fungerer();
        gitt_er_egen_ansatt();

        arbeidsfordelingService.hentBehandlendeEnheter(FAGOMRADE, OPPGAVETYPE, PERSON, UNDERKATEGORI);

        ArgumentCaptor<Boolean> erEgenAnsattCaptor = ArgumentCaptor.forClass(Boolean.class);
        verify(norgApi, times(1)).hentBehandlendeEnheter(any(), any(), any(), any(), erEgenAnsattCaptor.capture(), any());

        assertTrue(erEgenAnsattCaptor.getValue());
    }

    private void gitt_at_alt_fungerer() {
        ArrayList<Adressebeskyttelse> adressebeskyttelseMock = new ArrayList<>();
        adressebeskyttelseMock.add(new Adressebeskyttelse(
                AdressebeskyttelseGradering.UGRADERT
        ));
        sneaky(() -> {
            when(pdlOppslagService.hentGeografiskTilknyttning(anyString())).thenReturn(GEOGRAFISK_TILKNYTNING);
            when(pdlOppslagService.hentAdressebeskyttelse(anyString())).thenReturn(adressebeskyttelseMock);

            when(egenAnsattService.erSkjermetPerson(any())).thenReturn(false);
        });
    }

    private void gitt_er_egen_ansatt() {
        when(egenAnsattService.erSkjermetPerson(any())).thenReturn(true);
    }

    private void gitt_feil_ved_henting_av_geografisk_tilknytning() {
        when(pdlOppslagService.hentGeografiskTilknyttning(PERSON.get())).thenThrow(new RuntimeException());
    }

    private void gitt_feil_ved_henting_av_enheter() {
        sneaky(() ->
                when(norgApi.hentBehandlendeEnheter(any(), any(), any(), any(), anyBoolean(), any())).thenThrow(new IllegalStateException())
        );
    }
}
