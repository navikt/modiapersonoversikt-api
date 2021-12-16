package no.nav.modiapersonoversikt.legacy.sak.service;

import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService;
import no.nav.modiapersonoversikt.legacy.sak.mock.MockCreationUtil;
import no.nav.modiapersonoversikt.legacy.sak.service.filter.Filter;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.binding.SakOgBehandlingV1;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SakOgBehandlingServiceTest {

    @Mock
    private SakOgBehandlingV1 sakOgBehandling_v1PortType;

    @Mock
    private PdlOppslagService pdlOppslagService;

    @Mock
    private Filter filter;

    @InjectMocks
    private SakOgBehandlingService sakOgBehandlingService;


    @Test
    public void treWSsaker_skalGi_listeMedTreElementer() throws Exception {
        List<Sak> saker = Arrays.asList(
                MockCreationUtil.createWSSak(),
                MockCreationUtil.createWSSak(),
                MockCreationUtil.createWSSak()
        );

        FinnSakOgBehandlingskjedeListeResponse response = new FinnSakOgBehandlingskjedeListeResponse();
        response.getSak().addAll(saker);
        when(sakOgBehandling_v1PortType.finnSakOgBehandlingskjedeListe(any(FinnSakOgBehandlingskjedeListeRequest.class))).thenReturn(response);
        when(filter.filtrerSaker(any())).thenReturn(saker);

        assertThat(sakOgBehandlingService.hentAlleSaker("11111111111").size(), equalTo(3));
    }

}
