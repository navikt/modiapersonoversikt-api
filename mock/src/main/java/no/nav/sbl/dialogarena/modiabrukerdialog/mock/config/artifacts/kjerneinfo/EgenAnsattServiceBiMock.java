package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo;

import no.nav.kjerneinfo.consumer.fim.person.support.EgenAnsattServiceBi;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



public class EgenAnsattServiceBiMock {

    public static EgenAnsattServiceBi getEgenAnsattServiceBiMock() {
        EgenAnsattServiceBi egenAnsattServiceMock = mock(EgenAnsattServiceBi.class);

        boolean mockReturnValue = true;

        when(egenAnsattServiceMock.erEgenAnsatt(any(String.class))).thenReturn(mockReturnValue);

        return egenAnsattServiceMock;
    }

}
