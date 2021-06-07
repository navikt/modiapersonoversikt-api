package no.nav.modiapersonoversikt.legacy.sak.service;

import no.nav.modiapersonoversikt.legacy.api.domain.saker.Sak;
import no.nav.modiapersonoversikt.legacy.api.service.saker.SakerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SaksServiceTest {

    @Mock
    private PesysService pesysService;

    @Mock
    private SakerService sakerService;

    @InjectMocks
    private SaksService saksService;

    public static final String DAG = "DAG";
    public static final String AAP = "AAP";
    public static final String GSAK_SYSTEM = "FS22";


    @Test
    public void hentSakstema() {
        Sak sakDAG = new Sak();
        sakDAG.temaKode = DAG;
        sakDAG.fagsystemKode = GSAK_SYSTEM;

        Sak sakAAP = new Sak();
        sakAAP.temaKode = AAP;
        sakAAP.fagsystemKode = GSAK_SYSTEM;

        List<Sak> lstSaker = new ArrayList();
        lstSaker.add(sakDAG);
        lstSaker.add(sakAAP);

        when(sakerService.hentSakSaker(any()))
                .thenReturn(new SakerService.Resultat(lstSaker, new ArrayList<>()));

        List<no.nav.modiapersonoversikt.legacy.sak.providerdomain.Sak> saker = saksService.hentAlleSaker("11111111111").resultat;

        assertThat(saker.get(0).getTemakode(), equalTo(DAG));
        assertThat(saker.get(1).getTemakode(), equalTo(AAP));
    }

    @Test
    public void hentSakstemaGirTomOptionalHvisTjenesteGirException() {

        when(sakerService.hentSakSaker(any())).thenThrow(new RuntimeException());
        List<no.nav.modiapersonoversikt.legacy.sak.providerdomain.Sak> saker = saksService.hentAlleSaker("11111111111").resultat;

        assertTrue(saker.isEmpty());
    }

}
