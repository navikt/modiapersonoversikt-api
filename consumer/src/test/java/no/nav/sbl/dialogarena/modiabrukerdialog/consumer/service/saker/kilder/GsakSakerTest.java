package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.psak.PsakService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.SakerServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.virksomhet.behandlesak.v1.BehandleSakV1;
import no.nav.tjeneste.virksomhet.behandlesak.v1.OpprettSakSakEksistererAllerede;
import no.nav.tjeneste.virksomhet.behandlesak.v1.OpprettSakUgyldigInput;
import no.nav.tjeneste.virksomhet.behandlesak.v1.meldinger.WSOpprettSakRequest;
import no.nav.tjeneste.virksomhet.behandlesak.v1.meldinger.WSOpprettSakResponse;
import no.nav.tjeneste.virksomhet.sak.v1.FinnSakForMangeForekomster;
import no.nav.tjeneste.virksomhet.sak.v1.FinnSakUgyldigInput;
import no.nav.tjeneste.virksomhet.sak.v1.SakV1;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSFagomraader;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSFagsystemer;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSak;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSakstyper;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSFinnSakRequest;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSFinnSakResponse;
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeRequest;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeResponse;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class GsakSakerTest {
    private static final String VEDTAKSLOSNINGEN = "FS36";
    private static final DateTime FIRE_DAGER_SIDEN = now().minusDays(4);
    private static final String FNR = "fnr";
    private static final String BEHANDLINGSKJEDEID = "behandlingsKjedeId";
    public static final String SAKS_ID = "123";
    public static final String SakId_1 = "1";
    public static final String FagsystemSakId_1 = "11";
    public static final String SakId_2 = "2";
    public static final String FagsystemSakId_2 = "22";
    public static final String SakId_3 = "3";
    public static final String FagsystemSakId_3 = "33";
    public static final String SakId_4 = "4";
    public static final String FagsystemSakId_4 = "44";

    @Mock
    private SakV1 sakV1;
    @Mock
    private BehandleSakV1 behandleSak;
    @Mock
    private GsakKodeverk gsakKodeverk;
    @Mock
    private StandardKodeverk standardKodeverk;
    @Mock
    private ArbeidOgAktivitet arbeidOgAktivitet;
    @Mock
    private BehandleHenvendelsePortType behandleHenvendelsePortType;
    @Mock
    private UnleashService unleashService;
    @Mock
    private PsakService psakService;
    @InjectMocks
    private SakerServiceImpl sakerService;

    private List<WSSak> sakerListe;

    @BeforeEach
    void setUp() throws FinnSakUgyldigInput, FinnSakForMangeForekomster {
        initMocks(this);
        sakerService.setup(); // Kaller @PostConstruct manuelt siden vi kjører testen uten spring

        sakerListe = createSaksliste();

        when(sakV1.finnSak(any(WSFinnSakRequest.class))).thenReturn(new WSFinnSakResponse().withSakListe(sakerListe));

        when(arbeidOgAktivitet.hentSakListe(any(WSHentSakListeRequest.class))).thenReturn(new WSHentSakListeResponse());
  //      when(unleashService.isEnabled(anyString())).thenReturn(true);
  //      when(unleashService.isEnabled(any(Feature.class))).thenReturn(true);
    }

    @Test
    void transformasjonenGenerererRelevanteFelter() {
        Sak sak = GsakSaker.TIL_SAK.invoke(sakerListe.get(0));

        assertThat(sak.saksId, is(SakId_1));
        assertThat(sak.fagsystemSaksId, is(FagsystemSakId_1));
        assertThat(sak.temaKode, is(GODKJENTE_TEMA_FOR_GENERELL_SAK.get(0)));
        assertThat(sak.sakstype, is(SAKSTYPE_GENERELL));
        assertThat(sak.fagsystemKode, is(FAGSYSTEM_FOR_OPPRETTELSE_AV_GENERELL_SAK));
        assertThat(sak.opprettetDato, is(FIRE_DAGER_SIDEN));
        assertThat(sak.finnesIGsak, is(true));
    }

    @Test
    void transformasjonenBrukerSaksIdForFagsystemIdOgMFSSomSakstypeOmFagsystemErVedtakslosningen() {
        WSSak wsSak = sakerListe.get(0);
        wsSak.withFagsystem(new WSFagsystemer().withValue(VEDTAKSLOSNINGEN));
        Sak sak = GsakSaker.TIL_SAK.invoke(wsSak);

        assertThat(sak.saksId, is(SakId_1));
        assertThat(sak.fagsystemSaksId, is(SakId_1));
        assertThat(sak.temaKode, is(GODKJENTE_TEMA_FOR_GENERELL_SAK.get(0)));
        assertThat(sak.sakstype, is(SAKSTYPE_MED_FAGSAK));
        assertThat(sak.fagsystemKode, is(VEDTAKSLOSNINGEN));
        assertThat(sak.opprettetDato, is(FIRE_DAGER_SIDEN));
        assertThat(sak.finnesIGsak, is(true));
    }


    @Test
    void knytterBehandlingsKjedeTilSakUavhengigOmDenFinnesIGsak() throws Exception {
        Sak sak = lagSak();
        String valgtNavEnhet = "0219";

        WSOpprettSakResponse opprettSakResponse = new WSOpprettSakResponse();
        opprettSakResponse.setSakId(SAKS_ID);

        when(behandleSak.opprettSak(any(WSOpprettSakRequest.class))).thenReturn(opprettSakResponse);

        sakerService.knyttBehandlingskjedeTilSak(FNR, BEHANDLINGSKJEDEID, sak, valgtNavEnhet);

        verify(behandleHenvendelsePortType, times(1)).knyttBehandlingskjedeTilSak(BEHANDLINGSKJEDEID, SAKS_ID, sak.temaKode, valgtNavEnhet);
    }

    @Test
    void knytterBehandlingsKjedeTilSakUavhengigOmDenFinnesIGsakUtenFagsystemId() throws Exception {
        Sak sak = lagSakUtenFagsystemId();
        String valgtNavEnhet = "0219";

        WSOpprettSakResponse opprettSakResponse = new WSOpprettSakResponse();
        opprettSakResponse.setSakId(SAKS_ID);

        when(behandleSak.opprettSak(any(WSOpprettSakRequest.class))).thenReturn(opprettSakResponse);

        sakerService.knyttBehandlingskjedeTilSak(FNR, BEHANDLINGSKJEDEID, sak, valgtNavEnhet);

        verify(behandleHenvendelsePortType, times(1)).knyttBehandlingskjedeTilSak(BEHANDLINGSKJEDEID, SAKS_ID, sak.temaKode, valgtNavEnhet);
    }

    @Test
    void knyttBehandlingskjedeTilSakKallerAlternativMetodeOmBidragsHackSakenErValgt() throws Exception {
        String valgtNavEnhet = "0219";
        Sak sak = new Sak();
        sak.syntetisk = true;
        sak.fagsystemKode = BIDRAG_MARKOR;

        sakerService.knyttBehandlingskjedeTilSak(FNR, BEHANDLINGSKJEDEID, sak, valgtNavEnhet);

        verify(behandleSak, never()).opprettSak(any(WSOpprettSakRequest.class));
        verify(behandleHenvendelsePortType, never()).knyttBehandlingskjedeTilSak(anyString(), anyString(), anyString(), anyString());
        verify(behandleHenvendelsePortType, times(1)).knyttBehandlingskjedeTilTema(BEHANDLINGSKJEDEID, "BID");
    }

    @Test
    void knyttBehandlingskjedeTilSakKasterFeilHvisEnhetIkkeErSatt() throws OpprettSakUgyldigInput, OpprettSakSakEksistererAllerede {
        when(behandleSak.opprettSak(any(WSOpprettSakRequest.class))).thenReturn(new WSOpprettSakResponse().withSakId(SAKS_ID));

        assertThrows(IllegalArgumentException.class, () -> sakerService.knyttBehandlingskjedeTilSak(FNR, BEHANDLINGSKJEDEID, lagSak(), ""));
    }

    @Test
    void knyttBehandlingskjedeTilSakKasterFeilHvisBehandlingskjedeIkkeErSatt() throws OpprettSakUgyldigInput, OpprettSakSakEksistererAllerede {
        when(behandleSak.opprettSak(any(WSOpprettSakRequest.class))).thenReturn(new WSOpprettSakResponse().withSakId(SAKS_ID));

        assertThrows(IllegalArgumentException.class, () -> sakerService.knyttBehandlingskjedeTilSak(FNR, null, lagSak(), "1337"));
    }

    @Test
    void knyttBehandlingskjedeTilSakKasterFeilHvisFnrIkkeErSatt() throws OpprettSakUgyldigInput, OpprettSakSakEksistererAllerede {
        when(behandleSak.opprettSak(any(WSOpprettSakRequest.class))).thenReturn(new WSOpprettSakResponse().withSakId(SAKS_ID));

        assertThrows(IllegalArgumentException.class, () -> sakerService.knyttBehandlingskjedeTilSak("", BEHANDLINGSKJEDEID, lagSak(), "1337"));
    }

    private ArrayList<WSSak> createSaksliste() {
        return new ArrayList<>(asList(
                new WSSak()
                        .withSakId(SakId_1)
                        .withFagsystemSakId(FagsystemSakId_1)
                        .withFagomraade(new WSFagomraader().withValue(GODKJENTE_TEMA_FOR_GENERELL_SAK.get(0)))
                        .withOpprettelsetidspunkt(FIRE_DAGER_SIDEN)
                        .withSakstype(new WSSakstyper().withValue(SAKSTYPE_GENERELL))
                        .withFagsystem(new WSFagsystemer().withValue(FAGSYSTEM_FOR_OPPRETTELSE_AV_GENERELL_SAK)),
                new WSSak()
                        .withSakId(SakId_2)
                        .withFagsystemSakId(FagsystemSakId_2)
                        .withFagomraade(new WSFagomraader().withValue(GODKJENTE_TEMA_FOR_GENERELL_SAK.get(1)))
                        .withOpprettelsetidspunkt(now().minusDays(3))
                        .withSakstype(new WSSakstyper().withValue(SAKSTYPE_GENERELL))
                        .withFagsystem(new WSFagsystemer().withValue(FAGSYSTEM_FOR_OPPRETTELSE_AV_GENERELL_SAK)),
                new WSSak()
                        .withSakId(SakId_3)
                        .withFagsystemSakId(FagsystemSakId_3)
                        .withFagomraade(new WSFagomraader().withValue("AAP"))
                        .withOpprettelsetidspunkt(now().minusDays(5))
                        .withSakstype(new WSSakstyper().withValue("Fag"))
                        .withFagsystem(new WSFagsystemer().withValue(GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.get(0))),
                new WSSak()
                        .withSakId(SakId_4)
                        .withFagsystemSakId(FagsystemSakId_4)
                        .withFagomraade(new WSFagomraader().withValue("STO"))
                        .withOpprettelsetidspunkt(now().minusDays(5))
                        .withSakstype(new WSSakstyper().withValue(SAKSTYPE_GENERELL))
                        .withFagsystem(new WSFagsystemer().withValue(""))
        ));
    }

    private Sak lagSak() {
        Sak sak = new Sak();
        sak.temaKode = "GEN";
        sak.finnesIGsak = false;
        sak.fagsystemKode = FAGSYSTEM_FOR_OPPRETTELSE_AV_GENERELL_SAK;
        sak.sakstype = SAKSTYPE_GENERELL;
        sak.opprettetDato = now();
        return sak;
    }

    private Sak lagSakUtenFagsystemId() {
        Sak sak = new Sak();
        sak.temaKode = "STO";
        sak.finnesIGsak = false;
        sak.fagsystemKode = "";
        sak.sakstype = SAKSTYPE_GENERELL;
        sak.opprettetDato = now();
        return sak;
    }

}
