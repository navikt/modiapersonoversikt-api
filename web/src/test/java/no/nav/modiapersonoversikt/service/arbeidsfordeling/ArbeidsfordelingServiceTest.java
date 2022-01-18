package no.nav.modiapersonoversikt.service.arbeidsfordeling;

import no.nav.common.types.identer.Fnr;
import no.nav.modiapersonoversikt.config.endpoint.kodeverksmapper.Kodeverksmapper;
import no.nav.modiapersonoversikt.consumer.norg.NorgApi;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.egenansatt.EgenAnsattService;
import no.nav.modiapersonoversikt.rest.persondata.*;
import no.nav.modiapersonoversikt.service.arbeidsfordeling.ArbeidsfordelingService.ArbeidsfordelingException;
import no.nav.modiapersonoversikt.service.kodeverksmapper.domain.Behandling;
import no.nav.modiapersonoversikt.utils.PropertyRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static no.nav.modiapersonoversikt.service.unleash.strategier.ByEnvironmentStrategy.ENVIRONMENT_PROPERTY;
import static no.nav.modiapersonoversikt.utils.TestUtils.sneaky;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArbeidsfordelingServiceTest {
    @RegisterExtension
    static PropertyRule environment = new PropertyRule(ENVIRONMENT_PROPERTY, "n/a");

    private static final String FAGOMRADE = "BAR";
    private static final String OPPGAVETYPE = "SPM_OG_SVAR";
    private static final String UNDERKATEGORI = "KNA_BAR";
    private static final String ENHETSNUMMER = "4100";
    private static final String ENHETSNAVN = "NAV Styringsenhet Kontaktsenter";
    private static final String BEHANDLINGSTEMA = "ab0311";
    private static final String BEHANDLINGSTYPE = "ae0106";
    private static final String MAPPET_OPPGAVETYPE = "JFR";
    private static final String GEOGRAFISK_TILKNYTNING = "0219";
    private static final Fnr PERSON = Fnr.of("11111111111");
    private static final String STRENGT_FORTROLIG_ADRESSE = "SPSF";

    private PersondataService persondataService = mock(PersondataService.class);
    private Kodeverksmapper kodeverksmapper = mock(Kodeverksmapper.class);
    private NorgApi norgApi = mock(NorgApi.class);
    private EgenAnsattService egenAnsattService = mock(EgenAnsattService.class);
    private ArbeidsfordelingService arbeidsfordelingService;

    @BeforeEach
    void setupMocks() {
        Mockito.reset(
                persondataService,
                kodeverksmapper,
                norgApi,
                egenAnsattService
        );
        arbeidsfordelingService = new ArbeidsfordelingServiceImpl(norgApi, persondataService, kodeverksmapper, egenAnsattService);
    }

    @Test
    @DisplayName("Kaster exception hvis kall mot Kodeverksmapperen feiler")
    void kasterExceptionHvisKallMotKodeverksmapperFeiler() {
        gitt_at_alt_fungerer();
        gitt_feil_ved_henting_av_kodeverk();

        assertThrows(ArbeidsfordelingException.class, () -> arbeidsfordelingService.hentBehandlendeEnheter(FAGOMRADE, OPPGAVETYPE, PERSON, UNDERKATEGORI));
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
        ArrayList<Persondata.KodeBeskrivelse<Persondata.AdresseBeskyttelse>> adressebeskyttelseMock = new ArrayList<>();
        adressebeskyttelseMock.add(new Persondata.KodeBeskrivelse<>(Persondata.AdresseBeskyttelse.UGRADERT, "UGRADERT"));
        sneaky(() -> {
            when(persondataService.hentGeografiskTilknytning(anyString())).thenReturn(GEOGRAFISK_TILKNYTNING);
            when(persondataService.hentAdressebeskyttelse(anyString())).thenReturn(adressebeskyttelseMock);
            when(kodeverksmapper.hentOppgavetype()).thenReturn(new HashMap<>(){{
                put(OPPGAVETYPE, MAPPET_OPPGAVETYPE);
            }});
            when(kodeverksmapper.hentUnderkategori()).thenReturn(new HashMap<>(){{
                put(UNDERKATEGORI, new Behandling().withBehandlingstema(BEHANDLINGSTEMA).withBehandlingstype(BEHANDLINGSTYPE));
            }});

            when(egenAnsattService.erEgenAnsatt(anyString())).thenReturn(false);
        });
    }

    private void gitt_er_egen_ansatt() {
        when(egenAnsattService.erEgenAnsatt(anyString())).thenReturn(true);
    }

    private void gitt_feil_ved_henting_av_kodeverk() {
        sneaky(() -> {
            when(kodeverksmapper.hentOppgavetype()).thenThrow(new IOException());
            when(kodeverksmapper.hentUnderkategori()).thenThrow(new IOException());
        });
    }

    private void gitt_feil_ved_henting_av_geografisk_tilknytning() {
        when(persondataService.hentGeografiskTilknytning(PERSON.get())).thenThrow(new RuntimeException());
    }

    private void gitt_feil_ved_henting_av_enheter() {
        sneaky(() ->
                when(norgApi.hentBehandlendeEnheter(any(), any(), any(), any(), anyBoolean(), any())).thenThrow(new IllegalStateException())
        );
    }
}