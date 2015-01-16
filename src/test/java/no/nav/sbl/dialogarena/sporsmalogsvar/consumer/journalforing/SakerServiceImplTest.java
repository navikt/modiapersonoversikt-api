package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing;


import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Saker;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.GsakKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.LokaltKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.StandardKodeverk;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.SakerServiceImpl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;

import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Sak.GODKJENT_FAGSYSTEM_FOR_GENERELLE;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Sak.SAKSTYPE_GENERELL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createSak;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SakerServiceImplTest {

    @Mock
    private GsakService gsakService;
    @Mock
    private GsakKodeverk gsakKodeverk;
    @Mock
    private StandardKodeverk standardKodeverk;
    @Mock
    private LokaltKodeverk lokaltKodeverk;

    @InjectMocks
    private SakerServiceImpl sakerService;

    @Before
    public void setUp() {
        ArrayList<Sak> saksliste = createSakslisteBasertPaTemaMap();
        when(gsakService.hentSakerForBruker(anyString())).thenReturn(saksliste);
    }

    @Test
    public void hentSakerReturnererSakerObject() {
        ArgumentCaptor<String> fnrCaptor = ArgumentCaptor.forClass(String.class);

        Saker saker = sakerService.hentSaker("fnr");

        verify(gsakService, times(1)).hentSakerForBruker(fnrCaptor.capture());
        assertThat(fnrCaptor.getValue(), is("fnr"));
        assertThat(saker.getSakerListeGenerelle().size(), is(2));
        assertThat(saker.getSakerListeFagsak().size(), is(1));
    }

    private ArrayList<Sak> createSakslisteBasertPaTemaMap() {
        ArrayList<String> temaerForGenerelle = new ArrayList<>(Arrays.asList("FUL", "SER", "SIK", "VEN"));

        return new ArrayList<>(Arrays.asList(
                createSak("11111111", temaerForGenerelle.get(0), GODKJENT_FAGSYSTEM_FOR_GENERELLE, SAKSTYPE_GENERELL, DateTime.now().minusDays(4)),
                createSak("22222222", temaerForGenerelle.get(1), GODKJENT_FAGSYSTEM_FOR_GENERELLE, SAKSTYPE_GENERELL, DateTime.now().minusDays(3)),
                createSak("55555555", "AAP", "AO01", "Fag", DateTime.now().minusDays(5))
        ));
    }
}