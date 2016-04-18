package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.FeilendeBaksystemException;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sak;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.HentSakSammendragListePersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.HentSakSammendragListeSakManglerEierenhet;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.PensjonSakV1;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.informasjon.WSArkivtema;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.informasjon.WSSakSammendrag;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.meldinger.WSHentSakSammendragListeRequest;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.meldinger.WSHentSakSammendragListeResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertFalse;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem.PESYS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PesysServiceTest {

    @Mock
    private PensjonSakV1 pensjonSakV1;

    @InjectMocks
    private PesysService pesysService;

    @Test
    public void hentSakstemaGirTomtResultatVedHentSakSammendragListePersonIkkeFunnet() throws HentSakSammendragListeSakManglerEierenhet, HentSakSammendragListePersonIkkeFunnet {
        when(pensjonSakV1.hentSakSammendragListe(any(WSHentSakSammendragListeRequest.class))).thenThrow(new HentSakSammendragListePersonIkkeFunnet());

        Optional<Stream<Sak>> sakstemaFraPesys = pesysService.hentSakstemaFraPesys("11111111111");

        assertFalse(sakstemaFraPesys.isPresent());
    }

    @Test
    public void hentSakstemaGirTomtResultatVedHentSakSammendragListeSakManglerEierenhet() throws HentSakSammendragListeSakManglerEierenhet, HentSakSammendragListePersonIkkeFunnet {
        when(pensjonSakV1.hentSakSammendragListe(any(WSHentSakSammendragListeRequest.class))).thenThrow(new HentSakSammendragListeSakManglerEierenhet());

        Optional<Stream<Sak>> sakstemaFraPesys = pesysService.hentSakstemaFraPesys("11111111111");

        assertFalse(sakstemaFraPesys.isPresent());
    }

    @Test
    public void hentSakstemaGirFeilendeBaksystemExceptionVedUventetFeilFraTjeneste() throws HentSakSammendragListeSakManglerEierenhet, HentSakSammendragListePersonIkkeFunnet {
        when(pensjonSakV1.hentSakSammendragListe(any(WSHentSakSammendragListeRequest.class))).thenThrow(new RuntimeException());

        try {
            pesysService.hentSakstemaFraPesys("11111111111");
        } catch (FeilendeBaksystemException e) {
            assertThat(e.getBaksystem(), is(PESYS));
            return;
        }

        fail();
    }

    @Test
    public void mapperTilGenerellSak() throws HentSakSammendragListeSakManglerEierenhet, HentSakSammendragListePersonIkkeFunnet {
        when(pensjonSakV1.hentSakSammendragListe(any(WSHentSakSammendragListeRequest.class))).thenReturn(lagSakSammendrag());

        List<Sak> sakstemaFraPesys = pesysService.hentSakstemaFraPesys("11111111111").get().collect(toList());

        assertThat(sakstemaFraPesys.size(), is(2));

        Sak sak1 = sakstemaFraPesys.get(0);
        assertThat(sak1.getSaksId(), is("en id"));
        assertThat(sak1.getTemakode(), is("DAG"));
        assertThat(sak1.baksystem(), is(PESYS));
        assertThat(sak1.fagsystem(), is(PesysService.PESYS_FAGSYSTEM_ID));

        Sak sak2 = sakstemaFraPesys.get(1);
        assertThat(sak2.getSaksId(), is("en annen id"));
        assertThat(sak2.getTemakode(), is("FOR"));
        assertThat(sak2.baksystem(), is(PESYS));
        assertThat(sak2.fagsystem(), is(PesysService.PESYS_FAGSYSTEM_ID));
    }

    private WSHentSakSammendragListeResponse lagSakSammendrag() {
        return new WSHentSakSammendragListeResponse()
                .withSakSammendragListe(
                        new WSSakSammendrag()
                            .withArkivtema(new WSArkivtema().withValue("DAG"))
                            .withSakId("en id"),
                        new WSSakSammendrag()
                            .withArkivtema(new WSArkivtema().withValue("FOR"))
                            .withSakId("en annen id"));
    }
}