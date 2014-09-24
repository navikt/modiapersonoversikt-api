package no.nav.sbl.dialogarena.sak.transformers;

import no.nav.sbl.dialogarena.sak.service.SakOgBehandlingFilter;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstemaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSSakstemaer;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static no.nav.sbl.dialogarena.sak.mock.SakOgBehandlingMocks.createWSBehandlingskjede;
import static no.nav.sbl.dialogarena.sak.mock.SakOgBehandlingMocks.createWSSak;
import static no.nav.sbl.dialogarena.sak.transformers.SakOgBehandlingTransformers.BEHANDLINGSIDER_FRA_SAK;
import static no.nav.sbl.dialogarena.sak.transformers.SakOgBehandlingTransformers.BEHANDLINGSKJEDE_TIL_BEHANDLING;
import static no.nav.sbl.dialogarena.sak.transformers.SakOgBehandlingTransformers.temaVMTransformer;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsStatus.AVSLUTTET;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SakOgBehandlingTransformersTest {

    @Test
    public void skalHenteBehandlingsIDfraSak() {
        WSSak sak = createWSSak()
                .withBehandlingskjede(
                        createWSBehandlingskjede().withBehandlingsListeRef("1", "2", "3"),
                        createWSBehandlingskjede().withBehandlingsListeRef("4", "5", "6", "7"),
                        createWSBehandlingskjede().withBehandlingsListeRef("8", "9"));
        assertThat(BEHANDLINGSIDER_FRA_SAK.transform(sak).size(), equalTo(9));
    }

    @Test
    public void behandlingTransformerKomplettObjektMapping() {
        DateTime startTid = new DateTime().minusDays(2);
        String behandlingstema = "typeForTest";
        DateTime avsluttetDato = new DateTime();
        WSBehandlingskjede wsBehandlingskjede = createWSBehandlingskjede()
                .withSlutt(avsluttetDato)
                .withStart(startTid)
                .withBehandlingstema(new WSBehandlingstemaer().withValue(behandlingstema));
        GenerellBehandling behandling = BEHANDLINGSKJEDE_TIL_BEHANDLING.transform(wsBehandlingskjede);

        assertThat(behandling.behandlingDato, equalTo(avsluttetDato));
        assertThat(behandling.behandlingsStatus, equalTo(AVSLUTTET));
        assertThat(behandling.opprettetDato, equalTo(startTid));
        assertThat(behandling.behandlingstema, equalTo(behandlingstema));
    }

    @Test
    public void temaVMtransformerKomplettObjektMapping() {
        SakOgBehandlingFilter filter = mock(SakOgBehandlingFilter.class);
        when(filter.filtrerSaker(anyListOf(WSSak.class))).thenAnswer(new Answer<Object>() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0]; // Filtrerer ingenting og returnerer argumentet
            }
        });
        when(filter.filtrerBehandlinger(anyList())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0]; // Filtrerer ingenting og returnerer argumentet
            }
        });
        String temakode = "temakodeForTest";
        DateTime behandlingsdato = new DateTime();
        WSSak sak = new WSSak()
                .withSakstema(new WSSakstemaer().withValue(temakode))
                .withBehandlingskjede(createWSBehandlingskjede().withStart(behandlingsdato));

        assertThat(temaVMTransformer(filter).transform(sak).temakode, equalTo(temakode));
        assertThat(temaVMTransformer(filter).transform(sak).sistoppdaterteBehandling.behandlingDato, equalTo(behandlingsdato));
    }

}
