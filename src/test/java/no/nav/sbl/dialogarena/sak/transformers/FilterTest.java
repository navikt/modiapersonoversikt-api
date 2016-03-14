package no.nav.sbl.dialogarena.sak.transformers;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.sbl.dialogarena.sak.domain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.util.KvitteringstypeUtils;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FilterTest {

    @Mock
    private CmsContentRetriever cms;

    @InjectMocks
    private Filter filter;

    @Before
    public void setup() {
        when(cms.hentTekst("filter.lovligebehandlingstyper")).thenReturn("ae0047,ae0034,ae0014,ae0020,ae0019,ae0011,ae0045");
        when(cms.hentTekst("filter.ulovligesakstema")).thenReturn("FEI,SAK,SAP,OPP,YRA,GEN,AAR,KLA,HEL");
    }
    @Test
    public void filtrerBehandlingerOk() {
        List<GenerellBehandling> generellBehandling = filter.filtrerBehandlinger(lovligBehandling());
        assertThat(generellBehandling.size(), is(1));
    }

    @Test
    public void filtrerBehandlingerUlovligPrefix() {
        List<GenerellBehandling> generellBehandling = filter.filtrerBehandlinger(ulovligPrefix());
        assertThat(generellBehandling.size(), is(0));
    }

    @Test
    public void filtrerBehandlingerUlovligBehandlingsstatus() {
        List<GenerellBehandling> generellBehandling = filter.filtrerBehandlinger(ulovligBehandlingsstatus());
        assertThat(generellBehandling.size(), is(0));
    }

//    @Test
//    public void filtrerSakerOk() {
//        filter.filtrerSaker(lovligSaker());
//    }
//
//    private List<WSSak> lovligSaker() {
//        return asList(
//            new WSSak()
//        );
//    }

    private List<GenerellBehandling> lovligBehandling() {
        return asList(
                new GenerellBehandling()
                        .withBehandlingsType(KvitteringstypeUtils.SEND_SOKNAD_KVITTERINGSTYPE)
                        .withBehandlingStatus(GenerellBehandling.BehandlingsStatus.AVSLUTTET)
                .withPrefix("11")

        );
    }

    private List<GenerellBehandling> ulovligPrefix() {
        return asList(
                new GenerellBehandling()
                        .withBehandlingsType(KvitteringstypeUtils.SEND_SOKNAD_KVITTERINGSTYPE)
                        .withBehandlingStatus(GenerellBehandling.BehandlingsStatus.AVSLUTTET)
                        .withPrefix("17")

        );
    }

    private List<GenerellBehandling> ulovligBehandlingsstatus() {
        return asList(
                new GenerellBehandling()
                        .withBehandlingsType(KvitteringstypeUtils.SEND_SOKNAD_KVITTERINGSTYPE)
                        .withBehandlingStatus(GenerellBehandling.BehandlingsStatus.OPPRETTET)
                        .withPrefix("11")

        );
    }
}
