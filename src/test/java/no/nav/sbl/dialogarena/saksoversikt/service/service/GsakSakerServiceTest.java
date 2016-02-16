package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Sak;
import no.nav.tjeneste.virksomhet.sak.v1.FinnSakForMangeForekomster;
import no.nav.tjeneste.virksomhet.sak.v1.FinnSakUgyldigInput;
import no.nav.tjeneste.virksomhet.sak.v1.SakV1;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSFagomraader;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSFagsystemer;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSPerson;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSak;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSFinnSakRequest;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSFinnSakResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GsakSakerServiceTest {

    @Mock
    private SakV1 sakV1;

    @Mock
    private BulletproofKodeverkService kodeverkWrapper;

    @InjectMocks
    private GsakSakerService gsakSakerService;

    public static final String DAG = "DAG";
    public static final String AAP = "AAP";

    @Before
    public void setup() {
        when(kodeverkWrapper.getTemanavnForTemakode(anyString(), eq("Tema"))).thenReturn("test");
    }

    @Test
    public void hentSakstema() throws Exception {
        when(sakV1.finnSak(new WSFinnSakRequest().withBruker(new WSPerson().withIdent(anyString()))))
                .thenReturn(new WSFinnSakResponse().withSakListe(fagomraade(DAG), fagomraade(AAP)));

        List<Sak> saker = gsakSakerService.hentSaker("***REMOVED***").get().collect(toList());

        assertThat(saker.get(0).getTemakode(), equalTo(DAG));
        assertThat(saker.get(1).getTemakode(), equalTo(AAP));
    }

    @Test
    public void hentSakstemaGirTomOptionalHvisTjenesteGirException() throws FinnSakUgyldigInput, FinnSakForMangeForekomster {
        when(sakV1.finnSak(new WSFinnSakRequest().withBruker(new WSPerson().withIdent(anyString()))))
                .thenThrow(new FinnSakUgyldigInput());

        assertFalse(gsakSakerService.hentSaker("***REMOVED***").isPresent());
    }

    private WSSak fagomraade(String tema) {
        return new WSSak().withFagomraade(new WSFagomraader().withValue(tema)).withFagsystem(new WSFagsystemer().withValue("FS22"));
    }
}