package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;


import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.JournalforingFeilet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.psak.PsakService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.SakerServiceImpl;
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
import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.EndringsInfo;
import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Fagomradekode;
import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sakstypekode;
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeRequest;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeResponse;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.SakerServiceImpl.VEDTAKSLOSNINGEN;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SakerServiceImplTest {

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
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Mock
    private PsakService psakService;

    @InjectMocks
    private SakerServiceImpl sakerService;

    private List<WSSak> sakerListe;

    @BeforeEach
    void setUp() throws FinnSakUgyldigInput, FinnSakForMangeForekomster {
        initMocks(this);

        sakerListe = createSaksliste();

        when(sakV1.finnSak(any(WSFinnSakRequest.class))).thenReturn(new WSFinnSakResponse().withSakListe(sakerListe));

        when(arbeidOgAktivitet.hentSakListe(any(WSHentSakListeRequest.class))).thenReturn(new WSHentSakListeResponse());
    }

    @Test
    void transformererResponseTilSaksliste() {
        List<Sak> saksliste = sakerService.hentSammensatteSaker(FNR);

        assertThat(saksliste.get(0).saksId, is(SakId_1));
    }

    @Test
    void transformererResponseTilSakslistePensjon() {
        Sak pensjon = new Sak();
        pensjon.temaKode = "PENS";
        Sak ufore = new Sak();
        ufore.temaKode = "UFO";
        List<Sak> pensjonssaker = asList(pensjon, ufore);
        when(psakService.hentSakerFor(FNR)).thenReturn(pensjonssaker);
        List<Sak> saksliste = sakerService.hentPensjonSaker(FNR);

        assertThat(saksliste.size(), is(2));
        assertThat(saksliste.get(0).temaNavn, is("PENS"));
        assertThat(saksliste.get(1).temaNavn, is("UFO"));
    }

    @Test
    void transformasjonenGenerererRelevanteFelter() {
        Sak sak = SakerServiceImpl.TIL_SAK.apply(sakerListe.get(0));

        assertThat(sak.saksId, is(SakId_1));
        assertThat(sak.fagsystemSaksId, is(FagsystemSakId_1));
        assertThat(sak.temaKode, is(GODKJENTE_TEMA_FOR_GENERELLE.get(0)));
        assertThat(sak.sakstype, is(SAKSTYPE_GENERELL));
        assertThat(sak.fagsystemKode, is(GODKJENT_FAGSYSTEM_FOR_GENERELLE));
        assertThat(sak.opprettetDato, is(FIRE_DAGER_SIDEN));
        assertThat(sak.finnesIGsak, is(true));
    }

    @Test
    void transformasjonenBrukerSaksIdForFagsystemIdOgMFSSomSakstypeOmFagsystemErVedtakslosningen(){
        WSSak wsSak = sakerListe.get(0);
        wsSak.withFagsystem(new WSFagsystemer().withValue(VEDTAKSLOSNINGEN));
        Sak sak = SakerServiceImpl.TIL_SAK.apply(wsSak);

        assertThat(sak.saksId, is(SakId_1));
        assertThat(sak.fagsystemSaksId, is(SakId_1));
        assertThat(sak.temaKode, is(GODKJENTE_TEMA_FOR_GENERELLE.get(0)));
        assertThat(sak.sakstype, is(SAKSTYPE_MED_FAGSAK));
        assertThat(sak.fagsystemKode, is(VEDTAKSLOSNINGEN));
        assertThat(sak.opprettetDato, is(FIRE_DAGER_SIDEN));
        assertThat(sak.finnesIGsak, is(true));
    }

    @Test
    void oppretterIkkeGenerellOppfolgingssakDersomFagsakerInneholderOppfolgingssak() throws FinnSakUgyldigInput, FinnSakForMangeForekomster {
        List<WSSak> wsSaker = Arrays.asList(new WSSak()
                        .withSakId("4")
                        .withFagsystemSakId("44")
                        .withFagomraade(new WSFagomraader().withValue(TEMAKODE_OPPFOLGING))
                        .withOpprettelsetidspunkt(now())
                        .withSakstype(new WSSakstyper().withValue("Fag"))
                        .withFagsystem(new WSFagsystemer().withValue(GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.get(0))),
                new WSSak()
                        .withSakId("5")
                        .withFagsystemSakId("45")
                        .withFagomraade(new WSFagomraader().withValue(TEMAKODE_OPPFOLGING))
                        .withOpprettelsetidspunkt(now())
                        .withSakstype(new WSSakstyper().withValue(SAKSTYPE_GENERELL))
                        .withFagsystem(new WSFagsystemer().withValue(GODKJENT_FAGSYSTEM_FOR_GENERELLE)));

        when(sakV1.finnSak(any(WSFinnSakRequest.class))).thenReturn(new WSFinnSakResponse().withSakListe(wsSaker));

        List<Sak> saker = sakerService.hentSammensatteSaker(FNR).stream().filter(harTemaKode(TEMAKODE_OPPFOLGING)).collect(toList());

        assertThat(saker.size(), is(1));
        assertThat(saker.get(0).sakstype, not(is(SAKSTYPE_GENERELL)));
    }

    @Test
    void oppretterIkkeGenerellOppfolgingssakDersomDenneFinnesAlleredeSelvOmFagsakerIkkeInneholderOppfolgingssak() throws FinnSakUgyldigInput, FinnSakForMangeForekomster {
        List<WSSak> wsSaker = Arrays.asList(new WSSak()
                        .withSakId("4")
                        .withFagsystemSakId("44")
                        .withFagomraade(new WSFagomraader().withValue(TEMAKODE_OPPFOLGING))
                        .withOpprettelsetidspunkt(now())
                        .withSakstype(new WSSakstyper().withValue(SAKSTYPE_GENERELL))
                        .withFagsystem(new WSFagsystemer().withValue(GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.get(0))),
                new WSSak()
                        .withSakId("5")
                        .withFagsystemSakId("45")
                        .withFagomraade(new WSFagomraader().withValue(TEMAKODE_OPPFOLGING))
                        .withOpprettelsetidspunkt(now())
                        .withSakstype(new WSSakstyper().withValue(SAKSTYPE_GENERELL))
                        .withFagsystem(new WSFagsystemer().withValue(GODKJENT_FAGSYSTEM_FOR_GENERELLE)));

        when(sakV1.finnSak(any(WSFinnSakRequest.class))).thenReturn(new WSFinnSakResponse().withSakListe(wsSaker));

        List<Sak> saker = sakerService.hentSammensatteSaker(FNR).stream().filter(harTemaKode(TEMAKODE_OPPFOLGING)).collect(toList());

        assertThat(saker.size(), is(1));
        assertThat(saker.get(0).sakstype, is(SAKSTYPE_GENERELL));
    }

    @Test
    void fjernerGenerellOppfolgingssakDersomDenneFinnesOgOppfolgingssakFinnesIFagsaker() throws FinnSakUgyldigInput, FinnSakForMangeForekomster {
        List<WSSak> wsSaker = Arrays.asList(new WSSak()
                        .withSakId("4")
                        .withFagsystemSakId("44")
                        .withFagomraade(new WSFagomraader().withValue(TEMAKODE_OPPFOLGING))
                        .withOpprettelsetidspunkt(now())
                        .withSakstype(new WSSakstyper().withValue(SAKSTYPE_GENERELL))
                        .withFagsystem(new WSFagsystemer().withValue(GODKJENT_FAGSYSTEM_FOR_GENERELLE)),
                new WSSak()
                        .withSakId("5")
                        .withFagsystemSakId("55")
                        .withFagomraade(new WSFagomraader().withValue(TEMAKODE_OPPFOLGING))
                        .withOpprettelsetidspunkt(now())
                        .withSakstype(new WSSakstyper().withValue("Fag"))
                        .withFagsystem(new WSFagsystemer().withValue(FAGSYSTEMKODE_ARENA)));

        when(sakV1.finnSak(any(WSFinnSakRequest.class))).thenReturn(new WSFinnSakResponse().withSakListe(wsSaker));

        List<Sak> saker = sakerService.hentSammensatteSaker(FNR).stream().filter(harTemaKode(TEMAKODE_OPPFOLGING)).collect(toList());

        assertThat(saker.size(), is(1));
        assertThat(saker.get(0).sakstype, not(is(SAKSTYPE_GENERELL)));
    }

    @Test
    void leggerTilOppfolgingssakFraArenaDersomDenneIkkeFinnesIGsak() {
        String saksId = "123456";
        LocalDate dato = LocalDate.now().minusDays(1);

        when(arbeidOgAktivitet.hentSakListe(any(WSHentSakListeRequest.class))).thenReturn(new WSHentSakListeResponse().withSakListe(
                new no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak()
                        .withFagomradeKode(new Fagomradekode().withKode(TEMAKODE_OPPFOLGING))
                        .withSaksId(saksId)
                        .withEndringsInfo(new EndringsInfo().withOpprettetDato(dato))
                        .withSakstypeKode(new Sakstypekode().withKode("ARBEID"))
        ));

        List<Sak> saker = sakerService.hentSammensatteSaker(FNR).stream().filter(harTemaKode(TEMAKODE_OPPFOLGING)).collect(toList());

        assertThat(saker.size(), is(1));
        assertThat(saker.get(0).getSaksIdVisning(), is(saksId));
        assertThat(saker.get(0).opprettetDato, is(dato.toDateTimeAtStartOfDay()));
        assertThat(saker.get(0).fagsystemKode, is(FAGSYSTEMKODE_ARENA));
        assertThat(saker.get(0).finnesIGsak, is(false));
    }

    @Test
    void knytterBehandlingsKjedeTilSakUavhengigOmDenFinnesIGsak() throws Exception {
        Sak sak = lagSak();
        String valgtNavEnhet = "0219";

        WSOpprettSakResponse opprettSakResponse = new WSOpprettSakResponse();
        opprettSakResponse.setSakId(SAKS_ID);

        when(behandleSak.opprettSak(any(WSOpprettSakRequest.class))).thenReturn(opprettSakResponse);
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn(valgtNavEnhet);

        sakerService.knyttBehandlingskjedeTilSak(FNR, BEHANDLINGSKJEDEID, sak);
        verify(behandleHenvendelsePortType, times(1)).knyttBehandlingskjedeTilSak(BEHANDLINGSKJEDEID, SAKS_ID, sak.temaKode, valgtNavEnhet);
    }

    @Test
    void knyttBehandlingskjedeTilSakKasterFeilHvisEnhetIkkeErSatt() throws JournalforingFeilet, OpprettSakUgyldigInput, OpprettSakSakEksistererAllerede {
        when(behandleSak.opprettSak(any(WSOpprettSakRequest.class))).thenReturn(new WSOpprettSakResponse().withSakId(SAKS_ID));

        assertThrows(IllegalArgumentException.class, () -> sakerService.knyttBehandlingskjedeTilSak(FNR, BEHANDLINGSKJEDEID, lagSak(), ""));
    }

    @Test
    void knyttBehandlingskjedeTilSakKasterFeilHvisBehandlingskjedeIkkeErSatt() throws JournalforingFeilet, OpprettSakUgyldigInput, OpprettSakSakEksistererAllerede {
        when(behandleSak.opprettSak(any(WSOpprettSakRequest.class))).thenReturn(new WSOpprettSakResponse().withSakId(SAKS_ID));

        assertThrows(IllegalArgumentException.class, () -> sakerService.knyttBehandlingskjedeTilSak(FNR, null, lagSak(), "1337"));
    }

    @Test
    void knyttBehandlingskjedeTilSakKasterFeilHvisFnrIkkeErSatt() throws JournalforingFeilet, OpprettSakUgyldigInput, OpprettSakSakEksistererAllerede {
        when(behandleSak.opprettSak(any(WSOpprettSakRequest.class))).thenReturn(new WSOpprettSakResponse().withSakId(SAKS_ID));

        assertThrows(IllegalArgumentException.class, () -> sakerService.knyttBehandlingskjedeTilSak("", BEHANDLINGSKJEDEID, lagSak(), "1337"));
    }

    private ArrayList<WSSak> createSaksliste() {
        return new ArrayList<>(asList(
                new WSSak()
                        .withSakId(SakId_1)
                        .withFagsystemSakId(FagsystemSakId_1)
                        .withFagomraade(new WSFagomraader().withValue(GODKJENTE_TEMA_FOR_GENERELLE.get(0)))
                        .withOpprettelsetidspunkt(FIRE_DAGER_SIDEN)
                        .withSakstype(new WSSakstyper().withValue(SAKSTYPE_GENERELL))
                        .withFagsystem(new WSFagsystemer().withValue(GODKJENT_FAGSYSTEM_FOR_GENERELLE)),
                new WSSak()
                        .withSakId(SakId_2)
                        .withFagsystemSakId(FagsystemSakId_2)
                        .withFagomraade(new WSFagomraader().withValue(GODKJENTE_TEMA_FOR_GENERELLE.get(1)))
                        .withOpprettelsetidspunkt(now().minusDays(3))
                        .withSakstype(new WSSakstyper().withValue(SAKSTYPE_GENERELL))
                        .withFagsystem(new WSFagsystemer().withValue(GODKJENT_FAGSYSTEM_FOR_GENERELLE)),
                new WSSak()
                        .withSakId(SakId_3)
                        .withFagsystemSakId(FagsystemSakId_3)
                        .withFagomraade(new WSFagomraader().withValue("AAP"))
                        .withOpprettelsetidspunkt(now().minusDays(5))
                        .withSakstype(new WSSakstyper().withValue("Fag"))
                        .withFagsystem(new WSFagsystemer().withValue(GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.get(0)))
        ));
    }

    private Sak lagSak() {
        Sak sak = new Sak();
        sak.temaKode = "GEN";
        sak.finnesIGsak = false;
        sak.fagsystemKode = GODKJENT_FAGSYSTEM_FOR_GENERELLE;
        sak.sakstype = SAKSTYPE_GENERELL;
        sak.opprettetDato = now();
        return sak;
    }

}