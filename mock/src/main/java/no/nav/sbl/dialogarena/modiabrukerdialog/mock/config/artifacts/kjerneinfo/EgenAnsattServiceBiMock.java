package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo;

import no.nav.kjerneinfo.consumer.egenansatt.EgenAnsattService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



public class EgenAnsattServiceBiMock {

    public static EgenAnsattService getEgenAnsattServiceBiMock() {
        EgenAnsattService egenAnsattServiceMock = mock(EgenAnsattService.class);

        boolean mockReturnValue = true;

        when(egenAnsattServiceMock.erEgenAnsatt(any(String.class))).thenReturn(mockReturnValue);

        return egenAnsattServiceMock;
    }

}
