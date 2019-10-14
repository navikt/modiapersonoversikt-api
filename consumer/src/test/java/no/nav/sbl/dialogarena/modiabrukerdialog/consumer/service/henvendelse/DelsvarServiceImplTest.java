package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.DelsvarRequest.DelsvarRequestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DelsvarServiceImplTest {

    public static final String BRUKERS_FNR = "10108000398";
    public static final String TRAAD_ID = "TRAAD_ID";
    public static final String SVAR = "SVAR";
    public static final String SAKSBEHANDLERS_IDENT = "z666777";
    public static final String VALGT_ENHET = "0300";
    public static final String BEHANDLINGSID = "ABC123";

    private OppgaveBehandlingService oppgaveBehandlingServiceMock;
    private HenvendelseUtsendingService henvendelseMock;
    private DelsvarService delsvarService;

    @BeforeEach
    void before() {
        henvendelseMock = mock(HenvendelseUtsendingService.class);
        oppgaveBehandlingServiceMock = mock(OppgaveBehandlingService.class);
        when(henvendelseMock.hentTraad(BRUKERS_FNR, TRAAD_ID, VALGT_ENHET)).thenReturn(Collections.singletonList(new Melding()));
        delsvarService = new DelsvarServiceImpl(henvendelseMock, oppgaveBehandlingServiceMock);
    }

    @Test
    @DisplayName("Ferdigstiller henvendelse med delsvar")
    void ferdigstillerHenvendelse() throws Exception {
        delsvarService.svarDelvis(lagRequest());
        ArgumentCaptor<Melding> argumentCaptor = ArgumentCaptor.forClass(Melding.class);
        verify(henvendelseMock).ferdigstillHenvendelse(argumentCaptor.capture(), any(), any(), anyString(), anyString());

        assertAll("Delsvar",
                () -> assertEquals(SVAR, argumentCaptor.getValue().getFritekst()),
                () -> assertEquals(Kanal.TEKST.name(), argumentCaptor.getValue().kanal),
                () -> assertEquals(SAKSBEHANDLERS_IDENT, argumentCaptor.getValue().navIdent),
                () -> assertEquals(SAKSBEHANDLERS_IDENT, argumentCaptor.getValue().eksternAktor),
                () -> assertEquals(VALGT_ENHET, argumentCaptor.getValue().tilknyttetEnhet)
        );

    }

    @Test
    @DisplayName("Fanger feil fra service og konverterer til RuntimeException")
    void fangerExceptionsOgKasterRuntimeException() throws Exception {
        String opprinneligFeil = "Opprinnelig feil";
        doThrow(new IllegalArgumentException(opprinneligFeil)).when(henvendelseMock).ferdigstillHenvendelse(any(), any(), any(), any(), any());

        Throwable exception = assertThrows(RuntimeException.class, () -> delsvarService.svarDelvis(lagRequest()));

        assertEquals("Opprinnelig feil", exception.getCause().getMessage());
    }

    private DelsvarRequest lagRequest() {
        return new DelsvarRequestBuilder()
                .withNavIdent(SAKSBEHANDLERS_IDENT)
                .withBehandlingsId(BEHANDLINGSID)
                .withTraadId(TRAAD_ID)
                .withSvar(SVAR)
                .withFodselsnummer(BRUKERS_FNR)
                .withValgtEnhet(VALGT_ENHET)
                .withTemagruppe(Temagruppe.ARBD.name())
                .withOppgaveId("123")
                .build();
    }

}