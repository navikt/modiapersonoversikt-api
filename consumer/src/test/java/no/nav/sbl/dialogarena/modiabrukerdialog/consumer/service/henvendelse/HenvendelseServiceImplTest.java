package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.FeatureToggle;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.FerdigstillHenvendelseRequest.FerdigstillHenvendelseRequestBuilder;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class HenvendelseServiceImplTest {

    public static final String BRUKERS_FNR = "10108000398";
    public static final String TRAAD_ID = "TRAAD_ID";
    public static final String SVAR = "SVAR";

    private HenvendelseUtsendingService henvendelseMock;
    private HenvendelseService henvendelseService;

    @BeforeEach
    void before() {
        setupSubjectHandler();
        henvendelseMock = mock(HenvendelseUtsendingService.class);
        when(henvendelseMock.hentTraad(BRUKERS_FNR, TRAAD_ID)).thenReturn(Collections.singletonList(new Melding()));
        henvendelseService = new HenvendelseServiceImpl(henvendelseMock);
    }

    private void setupSubjectHandler() {
        System.setProperty(SubjectHandler.SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getCanonicalName());
    }

    @Test
    @DisplayName("Setter kanal på melding før den ferdigstilles")
    void setterKanalPaMelding() throws Exception {
        henvendelseService.ferdigstill(lagRequest());
        ArgumentCaptor<Melding> argumentCaptor = ArgumentCaptor.forClass(Melding.class);
        verify(henvendelseMock).ferdigstillHenvendelse(argumentCaptor.capture(), any(), any(), anyString());

        assertEquals(Kanal.TEKST.name(), argumentCaptor.getValue().kanal);
    }

    @Test
    @DisplayName("Legger til svar på melding før henvendelsen ferdigstilles")
    void setterSvarPaMelding() throws Exception {
        henvendelseService.ferdigstill(lagRequest());
        ArgumentCaptor<Melding> argumentCaptor = ArgumentCaptor.forClass(Melding.class);
        verify(henvendelseMock).ferdigstillHenvendelse(argumentCaptor.capture(), any(), any(), anyString());

        assertEquals(SVAR, argumentCaptor.getValue().getFritekst());
    }

    @Test
    @DisplayName("Fanger feil fra service og konverterer til RuntimeException")
    void fangerExceptionsOgKasterRuntimeException() throws Exception {
        String opprinneligFeil = "Opprinnelig feil";
        doThrow(new IllegalArgumentException(opprinneligFeil)).when(henvendelseMock).ferdigstillHenvendelse(any(), any(), any(), any());

        Throwable exception = assertThrows(RuntimeException.class, () -> henvendelseService.ferdigstill(lagRequest()));

        assertEquals("Opprinnelig feil", exception.getCause().getMessage());
    }

    private FerdigstillHenvendelseRequest lagRequest() {
        return new FerdigstillHenvendelseRequestBuilder()
                .withTraadId(TRAAD_ID)
                .withSvar(SVAR)
                .withFodselsnummer(BRUKERS_FNR)
                .build();
    }

}