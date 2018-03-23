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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak.*;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SakerServiceImplTest {

    private static final DateTime FIRE_DAGER_SIDEN = now().minusDays(4);
    private static final String FNR = "fnr";
    private static final String BEHANDLINGSKJEDEID = "behandlingsKjedeId";
    public static final String SAKS_ID = "123";

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

    @Before
    public void setUp() throws FinnSakUgyldigInput, FinnSakForMangeForekomster {
        sakerListe = createSaksliste();

        when(sakV1.finnSak(any(WSFinnSakRequest.class))).thenReturn(new WSFinnSakResponse().withSakListe(sakerListe));

        when(arbeidOgAktivitet.hentSakListe(any(WSHentSakListeRequest.class))).thenReturn(new WSHentSakListeResponse());
    }

    @Test
    public void transformererResponseTilSaksliste() {
        List<Sak> saksliste = sakerService.hentSammensatteSaker(FNR);

        assertThat(saksliste.get(0).saksId.get(), is("1"));
    }

    @Test
    public void transformererResponseTilSakslistePensjon() {
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
    public void transformasjonenGenerererRelevanteFelter() {
        Sak sak = SakerServiceImpl.TIL_SAK.transform(sakerListe.get(0));

        assertThat(sak.saksId.get(), is("1"));
        assertThat(sak.fagsystemSaksId.get(), is("11"));
        assertThat(sak.temaKode, is(GODKJENTE_TEMA_FOR_GENERELLE.get(0)));
        assertThat(sak.sakstype, is(SAKSTYPE_GENERELL));
        assertThat(sak.fagsystemKode, is(GODKJENT_FAGSYSTEM_FOR_GENERELLE));
        assertThat(sak.opprettetDato, is(FIRE_DAGER_SIDEN));
        assertThat(sak.finnesIGsak, is(true));
    }

    @Test
    public void oppretterIkkeGenerellOppfolgingssakDersomFagsakerInneholderOppfolgingssak() throws FinnSakUgyldigInput, FinnSakForMangeForekomster {
        List<WSSak> wsSaker = Arrays.asList(createWSGenerellSak("4", "44", TEMAKODE_OPPFOLGING, now(), "Fag", GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.get(0)),
                createWSGenerellSak("5", "45", TEMAKODE_OPPFOLGING, now(), SAKSTYPE_GENERELL, GODKJENT_FAGSYSTEM_FOR_GENERELLE));

        when(sakV1.finnSak(any(WSFinnSakRequest.class))).thenReturn(new WSFinnSakResponse().withSakListe(wsSaker));

        List<Sak> saker = on(sakerService.hentSammensatteSaker(FNR)).filter(harTemaKode(TEMAKODE_OPPFOLGING)).collect();

        assertThat(saker.size(), is(1));
        assertThat(saker.get(0).sakstype, not(is(SAKSTYPE_GENERELL)));
    }

    @Test
    public void oppretterIkkeGenerellOppfolgingssakDersomDenneFinnesAlleredeSelvOmFagsakerIkkeInneholderOppfolgingssak() throws FinnSakUgyldigInput, FinnSakForMangeForekomster {
        List<WSSak> wsSaker = Arrays.asList(createWSGenerellSak("4", "44", TEMAKODE_OPPFOLGING, now(), SAKSTYPE_GENERELL, GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.get(0)),
                createWSGenerellSak("5", "45", TEMAKODE_OPPFOLGING, now(), SAKSTYPE_GENERELL, GODKJENT_FAGSYSTEM_FOR_GENERELLE));

        when(sakV1.finnSak(any(WSFinnSakRequest.class))).thenReturn(new WSFinnSakResponse().withSakListe(wsSaker));

        List<Sak> saker = on(sakerService.hentSammensatteSaker(FNR)).filter(harTemaKode(TEMAKODE_OPPFOLGING)).collect();

        assertThat(saker.size(), is(1));
        assertThat(saker.get(0).sakstype, is(SAKSTYPE_GENERELL));
    }

    @Test
    public void fjernerGenerellOppfolgingssakDersomDenneFinnesOgOppfolgingssakFinnesIFagsaker() throws FinnSakUgyldigInput, FinnSakForMangeForekomster {
        List<WSSak> wsSaker = Arrays.asList(createWSGenerellSak("4", "44", TEMAKODE_OPPFOLGING, now(), SAKSTYPE_GENERELL, GODKJENT_FAGSYSTEM_FOR_GENERELLE),
                createWSGenerellSak("5", "55", TEMAKODE_OPPFOLGING, now(), "Fag", FAGSYSTEMKODE_ARENA));

        when(sakV1.finnSak(any(WSFinnSakRequest.class))).thenReturn(new WSFinnSakResponse().withSakListe(wsSaker));

        List<Sak> saker = on(sakerService.hentSammensatteSaker(FNR)).filter(harTemaKode(TEMAKODE_OPPFOLGING)).collect();

        assertThat(saker.size(), is(1));
        assertThat(saker.get(0).sakstype, not(is(SAKSTYPE_GENERELL)));
    }

    @Test
    public void leggerTilOppfolgingssakFraArenaDersomDenneIkkeFinnesIGsak() {
        String saksId = "123456";
        LocalDate dato = LocalDate.now().minusDays(1);

        when(arbeidOgAktivitet.hentSakListe(any(WSHentSakListeRequest.class))).thenReturn(new WSHentSakListeResponse().withSakListe(
                new no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak()
                        .withFagomradeKode(new Fagomradekode().withKode(TEMAKODE_OPPFOLGING))
                        .withSaksId(saksId)
                        .withEndringsInfo(new EndringsInfo().withOpprettetDato(dato))
                        .withSakstypeKode(new Sakstypekode().withKode("ARBEID"))
        ));

        List<Sak> saker = on(sakerService.hentSammensatteSaker(FNR)).filter(harTemaKode(TEMAKODE_OPPFOLGING)).collect();

        assertThat(saker.size(), is(1));
        assertThat(saker.get(0).getSaksIdVisning(), is(saksId));
        assertThat(saker.get(0).opprettetDato, is(dato.toDateTimeAtStartOfDay()));
        assertThat(saker.get(0).fagsystemKode, is(FAGSYSTEMKODE_ARENA));
        assertThat(saker.get(0).finnesIGsak, is(false));
    }

    @Test
    public void knytterBehandlingsKjedeTilSakUavhengigOmDenFinnesIGsak() throws Exception {
        Sak sak = lagSak();
        String valgtNavEnhet = "0219";

        WSOpprettSakResponse opprettSakResponse = new WSOpprettSakResponse();
        opprettSakResponse.setSakId(SAKS_ID);

        when(behandleSak.opprettSak(any(WSOpprettSakRequest.class))).thenReturn(opprettSakResponse);
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn(valgtNavEnhet);

        sakerService.knyttBehandlingskjedeTilSak(FNR, BEHANDLINGSKJEDEID, sak);
        verify(behandleHenvendelsePortType, times(1)).knyttBehandlingskjedeTilSak(BEHANDLINGSKJEDEID, SAKS_ID, sak.temaKode, valgtNavEnhet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void knyttBehandlingskjedeTilSakKasterFeilHvisEnhetIkkeErSatt() throws JournalforingFeilet, OpprettSakUgyldigInput, OpprettSakSakEksistererAllerede {
        when(behandleSak.opprettSak(any(WSOpprettSakRequest.class))).thenReturn(new WSOpprettSakResponse().withSakId(SAKS_ID));

        sakerService.knyttBehandlingskjedeTilSak(FNR, BEHANDLINGSKJEDEID, lagSak(), "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void knyttBehandlingskjedeTilSakKasterFeilHvisBehandlingskjedeIkkeErSatt() throws JournalforingFeilet, OpprettSakUgyldigInput, OpprettSakSakEksistererAllerede {
        when(behandleSak.opprettSak(any(WSOpprettSakRequest.class))).thenReturn(new WSOpprettSakResponse().withSakId(SAKS_ID));

        sakerService.knyttBehandlingskjedeTilSak(FNR, null, lagSak(), "1337");
    }

    @Test(expected = IllegalArgumentException.class)
    public void knyttBehandlingskjedeTilSakKasterFeilHvisFnrIkkeErSatt() throws JournalforingFeilet, OpprettSakUgyldigInput, OpprettSakSakEksistererAllerede {
        when(behandleSak.opprettSak(any(WSOpprettSakRequest.class))).thenReturn(new WSOpprettSakResponse().withSakId(SAKS_ID));

        sakerService.knyttBehandlingskjedeTilSak("", BEHANDLINGSKJEDEID, lagSak(), "1337");
    }

    private ArrayList<WSSak> createSaksliste() {
        return new ArrayList<>(asList(
                createWSGenerellSak("1", "11", GODKJENTE_TEMA_FOR_GENERELLE.get(0), FIRE_DAGER_SIDEN, SAKSTYPE_GENERELL, GODKJENT_FAGSYSTEM_FOR_GENERELLE),
                createWSGenerellSak("2", "22", GODKJENTE_TEMA_FOR_GENERELLE.get(1), now().minusDays(3), SAKSTYPE_GENERELL, GODKJENT_FAGSYSTEM_FOR_GENERELLE),
                createWSGenerellSak("3", "33", "AAP", now().minusDays(5), "Fag", GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.get(0))
        ));
    }

    private static WSSak createWSGenerellSak(String id, String fagsystemSaksId, String tema, DateTime opprettet, String sakstype, String fagsystem) {
        return new WSSak()
                .withSakId(id)
                .withFagsystemSakId(fagsystemSaksId)
                .withFagomraade(new WSFagomraader().withValue(tema))
                .withOpprettelsetidspunkt(opprettet)
                .withSakstype(new WSSakstyper().withValue(sakstype))
                .withFagsystem(new WSFagsystemer().withValue(fagsystem));
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