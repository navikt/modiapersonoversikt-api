package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.SvarDelvisRequest.SvarDelvisRequestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SvarDelvisServiceImplTest {

    public static final String BRUKERS_FNR = "10108000398";
    public static final String TRAAD_ID = "TRAAD_ID";
    public static final String SVAR = "SVAR";
    public static final String SAKSBEHANDLERS_IDENT = "z666777";
    public static final String VALGT_ENHET = "0300";

    private HenvendelseUtsendingService henvendelseMock;
    private SvarDelvisService svarDelvisService;

    @BeforeEach
    void before() {
        henvendelseMock = mock(HenvendelseUtsendingService.class);
        when(henvendelseMock.hentTraad(BRUKERS_FNR, TRAAD_ID, VALGT_ENHET)).thenReturn(Collections.singletonList(new Melding()));
        svarDelvisService = new SvarDelvisServiceImpl(henvendelseMock);
    }

    @Test
    @DisplayName("Ferdigstiller henvendelse med et delvis svar")
    void ferdigstillerHenvendelse() throws Exception {
        svarDelvisService.svarDelvis(lagRequest());
        ArgumentCaptor<Melding> argumentCaptor = ArgumentCaptor.forClass(Melding.class);
        verify(henvendelseMock).ferdigstillHenvendelse(argumentCaptor.capture(), any(), any(), anyString());

        assertAll("Delvis svar",
                () -> assertEquals(SVAR, argumentCaptor.getValue().getFritekst()),
                () -> assertEquals(Kanal.TEKST.name(), argumentCaptor.getValue().kanal),
                () -> assertEquals(SAKSBEHANDLERS_IDENT, argumentCaptor.getValue().navIdent),
                () -> assertEquals(VALGT_ENHET, argumentCaptor.getValue().tilknyttetEnhet)
        );

    }

    @Test
    @DisplayName("Fanger feil fra service og konverterer til RuntimeException")
    void fangerExceptionsOgKasterRuntimeException() throws Exception {
        String opprinneligFeil = "Opprinnelig feil";
        doThrow(new IllegalArgumentException(opprinneligFeil)).when(henvendelseMock).ferdigstillHenvendelse(any(), any(), any(), any());

        Throwable exception = assertThrows(RuntimeException.class, () -> svarDelvisService.svarDelvis(lagRequest()));

        assertEquals("Opprinnelig feil", exception.getCause().getMessage());
    }

    private SvarDelvisRequest lagRequest() {
        return new SvarDelvisRequestBuilder()
                .withNavIdent(SAKSBEHANDLERS_IDENT)
                .withTraadId(TRAAD_ID)
                .withSvar(SVAR)
                .withFodselsnummer(BRUKERS_FNR)
                .withValgtEnhet(VALGT_ENHET)
                .build();
    }

}