package no.nav.modiapersonoversikt.service;

import no.nav.modiapersonoversikt.service.saker.Sak;
import no.nav.modiapersonoversikt.service.pensjonsak.PsakServiceImpl;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.PensjonSakV1;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.informasjon.WSArkivtema;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.informasjon.WSPeriode;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.informasjon.WSSakSammendrag;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.meldinger.WSHentSakSammendragListeRequest;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.meldinger.WSHentSakSammendragListeResponse;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static no.nav.modiapersonoversikt.service.saker.Sak.FAGSYSTEMKODE_PSAK;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PsakServiceImplTest {

    PensjonSakV1 ws = mock(PensjonSakV1.class);
    PsakServiceImpl psakService = new PsakServiceImpl(ws);

    @Test
    public void henterSakerFraPsakOgMapperRiktig() throws Exception {
        WSSakSammendrag wsSakSammendrag = lagWSSakSammendrag();
        when(ws.hentSakSammendragListe(any(WSHentSakSammendragListeRequest.class))).thenReturn(new WSHentSakSammendragListeResponse().withSakSammendragListe(wsSakSammendrag));

        Collection<? extends Sak> saker = psakService.hentSakerFor("11111111111");

        assertThat(saker, hasSize(1));
        Sak sak = saker.iterator().next();
        assertThat(sak.fagsystemSaksId, is(wsSakSammendrag.getSakId()));
        assertThat(sak.saksId, is(wsSakSammendrag.getSakId()));
        assertThat(sak.temaKode, is(wsSakSammendrag.getArkivtema().getValue()));
        assertThat(sak.temaNavn, is(wsSakSammendrag.getArkivtema().getValue()));
        assertThat(sak.fagsystemKode, is(FAGSYSTEMKODE_PSAK));
        assertThat(sak.finnesIPsak, is(true));
        assertThat(sak.opprettetDato, is(wsSakSammendrag.getSaksperiode().getFom().toDateTimeAtStartOfDay()));
    }

    @Test
    public void handtererNullPeriode() throws Exception {
        WSSakSammendrag wsSakSammendrag = lagWSSakSammendrag().withSaksperiode(null);
        when(ws.hentSakSammendragListe(any(WSHentSakSammendragListeRequest.class))).thenReturn(new WSHentSakSammendragListeResponse().withSakSammendragListe(wsSakSammendrag));

        Collection<? extends Sak> saker = psakService.hentSakerFor("11111111111");
        Sak sak = saker.iterator().next();

        assertThat(sak.opprettetDato, nullValue());
    }

    @Test
    public void handtererNullFom() throws Exception {
        WSSakSammendrag wsSakSammendrag = lagWSSakSammendrag().withSaksperiode(new WSPeriode().withFom(null));
        when(ws.hentSakSammendragListe(any(WSHentSakSammendragListeRequest.class))).thenReturn(new WSHentSakSammendragListeResponse().withSakSammendragListe(wsSakSammendrag));

        Collection<? extends Sak> saker = psakService.hentSakerFor("11111111111");
        Sak sak = saker.iterator().next();

        assertThat(sak.opprettetDato, nullValue());
    }

    private static WSSakSammendrag lagWSSakSammendrag() {
        return new WSSakSammendrag()
                .withSakId("156842")
                .withArkivtema(new WSArkivtema().withValue("PEN"))
                .withSaksperiode(new WSPeriode().withFom(LocalDate.now().minusDays(5)));
    }
}
