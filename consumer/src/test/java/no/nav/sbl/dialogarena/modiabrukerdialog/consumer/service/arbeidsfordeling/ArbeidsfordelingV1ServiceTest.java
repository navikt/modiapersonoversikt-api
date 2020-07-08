package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.arbeidsfordeling;

import no.nav.kjerneinfo.consumer.egenansatt.EgenAnsattService;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.domain.person.GeografiskTilknytning;
import no.nav.kjerneinfo.domain.person.GeografiskTilknytningstyper;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.FinnBehandlendeEnhetException;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.KodeverksmapperService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.domain.Behandling;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.PropertyRule;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.binding.FinnBehandlendeEnhetListeUgyldigInput;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.informasjon.Organisasjonsenhet;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.meldinger.FinnBehandlendeEnhetListeRequest;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.meldinger.FinnBehandlendeEnhetListeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.strategier.ByEnvironmentStrategy.ENVIRONMENT_PROPERTY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.TestUtils.sneaky;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    private PersonKjerneinfoServiceBi personService = mock(PersonKjerneinfoServiceBi.class);
    private KodeverksmapperService kodeverksmapper = mock(KodeverksmapperService.class);
    private ArbeidsfordelingClient arbeidsfordelingClient = ArbeidsfordelingClientMock.get(Collections.emptyList());
    private EgenAnsattService egenAnsattService = mock(EgenAnsattService.class);
    private ArbeidsfordelingV1Service arbeidsfordelingService;

    @BeforeEach
    void setupMocks() {
        Mockito.reset(
                personService,
                kodeverksmapper,
                arbeidsfordelingClient,
                egenAnsattService
        );
        arbeidsfordelingService = new ArbeidsfordelingV1ServiceImpl(arbeidsfordelingClient, egenAnsattService, personService, kodeverksmapper);
    }

    @Test
    @DisplayName("Kaster exception hvis kall mot Kodeverksmapperen feiler")
    void kasterExceptionHvisKallMotKodeverksmapperFeiler() {
        gitt_at_alt_fungerer();
        gitt_feil_ved_henting_av_kodeverk();

        assertThrows(FinnBehandlendeEnhetException.class, () -> arbeidsfordelingService.finnBehandlendeEnhetListe(PERSON, FAGOMRADE, OPPGAVETYPE, UNDERKATEGORI));
    }

    @Test
    @DisplayName("Kaster exception hvis kall mot PersonService feiler")
    void kasterExceptionHvisKallMotPersonServiceFeiler() {
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
    void kallerArbeidsfordelingMedRiktigeArgumenterMotNyTjeneste() throws FinnBehandlendeEnhetListeUgyldigInput {
        gitt_at_alt_fungerer();
        gitt_er_egen_ansatt();

        arbeidsfordelingService.finnBehandlendeEnhetListe(PERSON, FAGOMRADE, OPPGAVETYPE, UNDERKATEGORI);

        ArgumentCaptor<Boolean> erEgenAnsattCaptor = ArgumentCaptor.forClass(Boolean.class);
        verify(arbeidsfordelingClient, times(1)).hentArbeidsfordeling(any(), any(), any(), any(), erEgenAnsattCaptor.capture());

        assertTrue(erEgenAnsattCaptor.getValue());
    }

    private void gitt_at_alt_fungerer() {
        sneaky(() -> {
            when(personService.hentGeografiskTilknytning(anyString())).thenReturn(new GeografiskTilknytning().withType(GeografiskTilknytningstyper.KOMMUNE).withValue(GEOGRAFISK_TILKNYTNING));

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
        when(personService.hentGeografiskTilknytning(anyString())).thenThrow(new RuntimeException());
    }

    private void gitt_feil_ved_henting_av_enheter() {
        sneaky(() -> {
            when(arbeidsfordelingClient.hentArbeidsfordeling(any(), any(), any(), any(), anyBoolean())).thenThrow(new IllegalStateException());
        });
    }
}
