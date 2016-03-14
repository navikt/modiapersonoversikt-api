package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.sak.transformers.FilterImpl;
import no.nav.sbl.dialogarena.saksoversikt.service.service.SakOgBehandlingService;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SaksoversiktServiceTest {

    @Mock
    private AktoerPortType fodselnummerAktorService;
    @Mock
    private SakOgBehandlingService sakOgBehandlingService;
    @Mock
    private FilterImpl filter;

    @InjectMocks
    private SaksoversiktServiceImpl saksoversiktService = new SaksoversiktServiceImpl();


    @Test (expected = SystemException.class)
    public void kasterSystemExceptionOmAktorIdIkkeFinnes() throws HentAktoerIdForIdentPersonIkkeFunnet {
        when(fodselnummerAktorService.hentAktoerIdForIdent(any())).thenThrow(new HentAktoerIdForIdentPersonIkkeFunnet());
        saksoversiktService.hentTemaer("12345678901");
    }

    @Test (expected = RuntimeException.class)
    public void kasterRuntimeOmAktoerTjenesteErNede() throws HentAktoerIdForIdentPersonIkkeFunnet {
        when(fodselnummerAktorService.hentAktoerIdForIdent(any())).thenThrow(new RuntimeException());
        saksoversiktService.hentTemaer("12345678901");
    }
}
