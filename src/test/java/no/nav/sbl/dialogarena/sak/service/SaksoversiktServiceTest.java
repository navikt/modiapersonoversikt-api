package no.nav.sbl.dialogarena.sak.service;

import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.sak.mock.SakOgBehandlingMocks.createWSSak;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SaksoversiktServiceTest {

    @Mock
    SakOgBehandlingPortType sakOgBehandling;

    @Mock
    HenvendelseSoknaderPortType henvendelse;

    @Mock
    AktoerPortType aktoer;

    @InjectMocks
    private SaksoversiktService service;

    private FinnSakOgBehandlingskjedeListeResponse saker = new FinnSakOgBehandlingskjedeListeResponse();
    List<WSSoknad> henvendelseSoknader = new ArrayList<>();

    @Before
    public void setup() {
        HentAktoerIdForIdentResponse aktoerResponse = new HentAktoerIdForIdentResponse();
        aktoerResponse.setAktoerId("aktor");
        when(sakOgBehandling.finnSakOgBehandlingskjedeListe(any(FinnSakOgBehandlingskjedeListeRequest.class)))
                .thenReturn(saker);
        when(henvendelse.hentSoknadListe(any(String.class)))
                .thenReturn(henvendelseSoknader);
        try {
            when(aktoer.hentAktoerIdForIdent(any(HentAktoerIdForIdentRequest.class)))
                    .thenReturn(aktoerResponse);
        } catch (Exception e) {
            //whatever
        }
    }

    @Test
    public void hentTemaer_skalGi_likeMangeTemaSomSaker() {
        saker.withSak(createWSSak(), createWSSak(), createWSSak());
        assertThat(service.hentTemaer("abc").size(), equalTo(saker.getSak().size()));
    }

    @Test
    public void hentTemaer_skalSortere_omvendtKronologisk() {
        WSSak sak2013 = createWSSak();
        WSSak sak2014 = createWSSak();
        saker.withSak(sak2013, sak2014);
        assertThat(service.hentTemaer("abc").size(), equalTo(saker.getSak().size()));
    }

}
