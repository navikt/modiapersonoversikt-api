package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Bruker;
import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.EndringsInfo;
import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Fagomradekode;
import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sakstypekode;
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeRequest;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeResponse;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.ArenaService.ARENA_FAGSYSTEMKODE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.ArenaService.BRUKERKODE_PERSON;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.ArenaService.OPPFOLGINGSSAK_TEMA_IDENTIFIKATOR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArenaServiceTest {

    public static final String SAKSID = "ID 1";
    public static final LocalDate OPPRETTET_DATO = new LocalDate().minusDays(1);
    public static final String SAKSTYPEKODE = "arenasak";

    @Captor
    private ArgumentCaptor<WSHentSakListeRequest> wsHentSakListeRequestArgumentCaptor;

    @Mock
    private ArbeidOgAktivitet arbeidOgAktivitet;

    @InjectMocks
    private ArenaService arenaService;

    @Before
    public void setUp() {
        when(arbeidOgAktivitet.hentSakListe(any(WSHentSakListeRequest.class))).thenReturn(createResponseMedSak(OPPFOLGINGSSAK_TEMA_IDENTIFIKATOR));
    }

    @Test
    public void henterSakerFraArena() {
        String fnr = "fnr";

        arenaService.hentOppfolgingssak(fnr);
        Mockito.verify(arbeidOgAktivitet).hentSakListe(wsHentSakListeRequestArgumentCaptor.capture());
        WSHentSakListeRequest request = wsHentSakListeRequestArgumentCaptor.getValue();

        assertThat(request.getBruker().getBruker(), is(fnr));
        assertThat(request.getBruker().getBrukertypeKode(), is(BRUKERKODE_PERSON));
    }

    @Test
    public void oversetterFraArenaSakTilSak() {
        Optional<Sak> optionalSak = arenaService.hentOppfolgingssak("fnr");

        assertTrue(optionalSak.isSome());
        Sak sak = optionalSak.get();
        assertThat(sak.saksId, is(SAKSID));
        assertThat(sak.fagsystemKode, is(ARENA_FAGSYSTEMKODE));
        assertThat(sak.sakstype, is(SAKSTYPEKODE));
        assertThat(sak.tema, is(OPPFOLGINGSSAK_TEMA_IDENTIFIKATOR));
        assertThat(sak.opprettetDato, is(new DateTime(OPPRETTET_DATO.toDate())));
    }

    @Test
    public void haandtererTomListeFraArena() {
        when(arbeidOgAktivitet.hentSakListe(any(WSHentSakListeRequest.class))).thenReturn(new WSHentSakListeResponse().withSakListe(Collections.EMPTY_LIST));

        Optional<Sak> optionalSak = arenaService.hentOppfolgingssak("fnr");

        assertFalse(optionalSak.isSome());
    }

    @Test
    public void haandtererEnListeUtenOppfolgingssaker() {
        when(arbeidOgAktivitet.hentSakListe(any(WSHentSakListeRequest.class))).thenReturn(createResponseMedSak("sakstype som ikke er oppfølgingsidentifikator"));

        Optional<Sak> optionalSak = arenaService.hentOppfolgingssak("fnr");

        assertFalse(optionalSak.isSome());

    }

    private WSHentSakListeResponse createResponseMedSak(String tema) {
        return new WSHentSakListeResponse().withSakListe(
                new no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak()
                        .withAnsvarligEnhetId("ansvarlig enhet")
                        .withAr("2014")
                        .withBruker(new Bruker().withBruker("bruker").withBrukertypeKode("type"))
                        .withEndringsInfo(new EndringsInfo().withOpprettetDato(OPPRETTET_DATO))
                        .withFagomradeKode(new Fagomradekode().withKode(tema))
                        .withSakstypeKode(new Sakstypekode().withKode(SAKSTYPEKODE))
                        .withLopenr("løpenr")
                        .withSaksId(SAKSID));
    }

}
