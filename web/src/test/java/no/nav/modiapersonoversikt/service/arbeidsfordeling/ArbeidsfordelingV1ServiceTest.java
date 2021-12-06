package no.nav.modiapersonoversikt.service.arbeidsfordeling;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.egenansatt.EgenAnsattService;
import no.nav.modiapersonoversikt.legacy.api.service.arbeidsfordeling.ArbeidsfordelingV1Service;
import no.nav.modiapersonoversikt.legacy.api.service.arbeidsfordeling.FinnBehandlendeEnhetException;
import no.nav.modiapersonoversikt.rest.persondata.PersondataFletter;
import no.nav.modiapersonoversikt.rest.persondata.PersondataResult;
import no.nav.modiapersonoversikt.rest.persondata.PersondataService;
import no.nav.modiapersonoversikt.rest.persondata.PersondataTestdataKt;
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk;
import no.nav.modiapersonoversikt.service.kodeverksmapper.KodeverksmapperService;
import no.nav.modiapersonoversikt.service.kodeverksmapper.domain.Behandling;
import no.nav.modiapersonoversikt.utils.PropertyRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Optional;

import static no.nav.modiapersonoversikt.service.unleash.strategier.ByEnvironmentStrategy.ENVIRONMENT_PROPERTY;
import static no.nav.modiapersonoversikt.utils.TestUtils.sneaky;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArbeidsfordelingV1ServiceTest {
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
    private static final String PERSON = "11111111111";
    private static final String STRENGT_FORTROLIG_ADRESSE = "SPSF";

    private PersondataService persondataService = mock(PersondataService.class);
    private KodeverksmapperService kodeverksmapper = mock(KodeverksmapperService.class);
    private ArbeidsfordelingClient arbeidsfordelingClient = mock(ArbeidsfordelingClient.class);
    private EgenAnsattService egenAnsattService = mock(EgenAnsattService.class);
    private ArbeidsfordelingV1Service arbeidsfordelingService;

    @BeforeEach
    void setupMocks() {
        Mockito.reset(
                persondataService,
                kodeverksmapper,
                arbeidsfordelingClient,
                egenAnsattService
        );
        arbeidsfordelingService = new ArbeidsfordelingV1ServiceImpl(arbeidsfordelingClient, egenAnsattService, persondataService, kodeverksmapper);
    }

    @Test
    @DisplayName("Kaster exception hvis kall mot Kodeverksmapperen feiler")
    void kasterExceptionHvisKallMotKodeverksmapperFeiler() {
        gitt_at_alt_fungerer();
        gitt_feil_ved_henting_av_kodeverk();

        assertThrows(FinnBehandlendeEnhetException.class, () -> arbeidsfordelingService.finnBehandlendeEnhetListe(PERSON, FAGOMRADE, OPPGAVETYPE, UNDERKATEGORI));
    }

    @Test
    @DisplayName("Kaster exception hvis kall mot PersondataService feiler")
    void kasterExceptionHvisKallMotPersondataServiceFeiler() {
        gitt_at_alt_fungerer();
        gitt_feil_ved_henting_av_geografisk_tilknytning();

        assertThrows(FinnBehandlendeEnhetException.class, () -> arbeidsfordelingService.finnBehandlendeEnhetListe(PERSON, FAGOMRADE, OPPGAVETYPE, UNDERKATEGORI));
    }

    @Test
    @DisplayName("Kaster exception hvis kall mot Arbeidsfordeling feiler")
    void kasterExceptionHvisKallMotArbeidsfordelingFeiler() {
        gitt_at_alt_fungerer();
        gitt_feil_ved_henting_av_enheter();

        assertThrows(FinnBehandlendeEnhetException.class, () -> arbeidsfordelingService.finnBehandlendeEnhetListe(PERSON, FAGOMRADE, OPPGAVETYPE, UNDERKATEGORI));
    }

    @Test
    @DisplayName("Arbeidsfordeling kalles med riktige argumenter og bruker REST")
    void kallerArbeidsfordelingMedRiktigeArgumenterMotNyTjeneste() {
        gitt_at_alt_fungerer();
        gitt_er_egen_ansatt();

        arbeidsfordelingService.finnBehandlendeEnhetListe(PERSON, FAGOMRADE, OPPGAVETYPE, UNDERKATEGORI);

        ArgumentCaptor<Boolean> erEgenAnsattCaptor = ArgumentCaptor.forClass(Boolean.class);
        verify(arbeidsfordelingClient, times(1)).hentArbeidsfordeling(any(), any(), any(), any(), erEgenAnsattCaptor.capture(), any());

        assertTrue(erEgenAnsattCaptor.getValue());
    }

    private void gitt_at_alt_fungerer() {
        sneaky(() -> {
            when(persondataService.hentGeografiskTilknytning(anyString())).thenReturn(GEOGRAFISK_TILKNYTNING);
            when(kodeverksmapper.mapOppgavetype(anyString())).thenReturn(MAPPET_OPPGAVETYPE);
            when(kodeverksmapper.mapUnderkategori(anyString())).thenReturn(Optional.of(new Behandling().withBehandlingstema(BEHANDLINGSTEMA).withBehandlingstype(BEHANDLINGSTYPE)));

            when(egenAnsattService.erEgenAnsatt(anyString())).thenReturn(false);
        });
    }

    private void gitt_er_egen_ansatt() {
        when(egenAnsattService.erEgenAnsatt(anyString())).thenReturn(true);
    }

    private void gitt_feil_ved_henting_av_kodeverk() {
        sneaky(() -> {
            when(kodeverksmapper.mapOppgavetype(anyString())).thenThrow(new IOException());
            when(kodeverksmapper.mapUnderkategori(anyString())).thenThrow(new IOException());
        });
    }

    private void gitt_feil_ved_henting_av_geografisk_tilknytning() {
        when(persondataService.hentGeografiskTilknytning(PERSON)).thenThrow(new RuntimeException());
    }

    private void gitt_feil_ved_henting_av_enheter() {
        sneaky(() ->
                when(arbeidsfordelingClient.hentArbeidsfordeling(any(), any(), any(), any(), anyBoolean(), any())).thenThrow(new IllegalStateException())
        );
    }
}
