package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.JournalforingNotat;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.binding.BehandleJournalV2;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerInngaaendeHenvendelseRequest;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerInngaaendeHenvendelseResponse;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerNotatRequest;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerNotatResponse;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerUtgaaendeHenvendelseRequest;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerUtgaaendeHenvendelseResponse;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.JoarkJournalforingService.MODIA_SYSTEM_ID;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createMelding;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createSak;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.innloggetBrukerEr;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JoarkJournalforingServiceTest {

    @Captor
    private ArgumentCaptor<JournalfoerInngaaendeHenvendelseRequest> journalfoerInngaaendeRequestCaptor;
    @Captor
    private ArgumentCaptor<JournalfoerNotatRequest> journalfoerNotatRequestCaptor;
    @Captor
    private ArgumentCaptor<JournalfoerUtgaaendeHenvendelseRequest> journalfoerUtgaaendeHenvendelseRequestCaptor;

    @Mock
    private JournalfoerInngaaendeHenvendelseResponse journalfoerInngaaendeHenvendelseResponseMock;
    @Mock
    private JournalfoerUtgaaendeHenvendelseResponse journalfoerUtgaaendeHenvendelseResponseMock;
    @Mock
    private JournalfoerNotatResponse journalfoerNotatResponseMock;

    @Mock
    private BehandleHenvendelsePortType behandleHenvendelsePortType;
    @Mock
    private BehandleJournalV2 behandleJournalV2;
    @Mock
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Mock
    private HenvendelseBehandlingService henvendelseBehandlingService;

    @InjectMocks
    private JoarkJournalforingService joarkJournalforingService;

    private List<MeldingVM> meldinger;
    private TraadVM traadVM;
    private Sak sak;

    private static final String SPORSMAL_POST_ID = "7863478648";
    private static final String GENERELL_POST_ID = "623674836846";
    private static final String JOURNALFOERENDE_ENHET_ID = "1234";
    private static final String KANAL_TELEFON = "TELEFON";
    private static final String NAVIDENT = "navident";

    @Before
    public void setUp() {
        innloggetBrukerEr(NAVIDENT);

        sak = createSak("542747214621", "Dagpenger", "Fagsystem", Sak.SAKSTYPE_GENERELL, DateTime.now());

        when(journalfoerInngaaendeHenvendelseResponseMock.getJournalpostId()).thenReturn(SPORSMAL_POST_ID);
        when(behandleJournalV2.journalfoerInngaaendeHenvendelse(any(JournalfoerInngaaendeHenvendelseRequest.class))).thenReturn(journalfoerInngaaendeHenvendelseResponseMock);

        when(journalfoerNotatResponseMock.getJournalpostId()).thenReturn(GENERELL_POST_ID);
        when(behandleJournalV2.journalfoerNotat(any(JournalfoerNotatRequest.class))).thenReturn(journalfoerNotatResponseMock);

        when(journalfoerUtgaaendeHenvendelseResponseMock.getJournalpostId()).thenReturn(GENERELL_POST_ID);
        when(behandleJournalV2.journalfoerUtgaaendeHenvendelse(any(JournalfoerUtgaaendeHenvendelseRequest.class))).thenReturn(journalfoerUtgaaendeHenvendelseResponseMock);

        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn(JOURNALFOERENDE_ENHET_ID);
    }

    @Test
    public void sjekkAtRiktigJounalforHenvendelseKallBlirUtfortGittTraadMedEttSporsmalOgEttSamtaleReferat() {
        meldinger = createMeldingListeMedEttSporsmaalOgEttSamtalereferat();
        traadVM = new TraadVM(meldinger);

        joarkJournalforingService.journalforTraad(traadVM, sak);

        verify(behandleJournalV2).journalfoerInngaaendeHenvendelse(any(JournalfoerInngaaendeHenvendelseRequest.class));
        verify(behandleJournalV2).journalfoerNotat(any(JournalfoerNotatRequest.class));
    }

    @Test
    public void sjekkAtRiktigJournalforHenvendelseKallBlirUtfortGittTraadMedKunEttReferat() {
        meldinger = new ArrayList<>(asList(createMeldingVM(Meldingstype.SAMTALEREFERAT_OPPMOTE, 1, KANAL_TELEFON)));
        traadVM = new TraadVM(meldinger);

        joarkJournalforingService.journalforTraad(traadVM, sak);

        verify(behandleJournalV2).journalfoerNotat(any(JournalfoerNotatRequest.class));
    }

    @Test
    public void sjekkAtRiktigJournalforHenvendelseKallBlirUtfortGittTraadMedSporsmalOgEttSvar() {
        meldinger = new ArrayList<>(asList(createMeldingVM(Meldingstype.SVAR_SKRIFTLIG, 2, ""),
                createMeldingVM(Meldingstype.SPORSMAL_SKRIFTLIG, 2, "")));
        traadVM = new TraadVM(meldinger);

        joarkJournalforingService.journalforTraad(traadVM, sak);

        verify(behandleJournalV2).journalfoerInngaaendeHenvendelse(any(JournalfoerInngaaendeHenvendelseRequest.class));
        verify(behandleJournalV2).journalfoerUtgaaendeHenvendelse(any(JournalfoerUtgaaendeHenvendelseRequest.class));
    }

    @Test
    public void sjekkAtRiktigKryssReferanseForSvarSettesTilKorresponderendeSporsmaalVedJournalforingAvHenvendelse() {
        meldinger = new ArrayList<>(asList(createMeldingVM(Meldingstype.SVAR_SKRIFTLIG, 2, ""),
                createMeldingVM(Meldingstype.SPORSMAL_SKRIFTLIG, 2, "")));
        traadVM = new TraadVM(meldinger);

        joarkJournalforingService.journalforTraad(traadVM, sak);

        verify(behandleJournalV2).journalfoerUtgaaendeHenvendelse(journalfoerUtgaaendeHenvendelseRequestCaptor.capture());
        assertThat(journalfoerUtgaaendeHenvendelseRequestCaptor.getValue().getJournalpost().getKryssreferanseListe().get(0).getReferanseId(), is(SPORSMAL_POST_ID));
    }

    @Test
    public void sjekkAtRiktigKryssReferanseForReferatSettesTilKorresponderendeSporsmalVedJournalforingAvHenvendelse() {
        meldinger = createMeldingListeMedEttSporsmaalOgEttSamtalereferat();
        traadVM = new TraadVM(meldinger);

        joarkJournalforingService.journalforTraad(traadVM, sak);

        verify(behandleJournalV2).journalfoerNotat(journalfoerNotatRequestCaptor.capture());
        assertThat(journalfoerNotatRequestCaptor.getValue().getJournalpost().getKryssreferanseListe().get(0).getReferanseId(), is(SPORSMAL_POST_ID));
    }

    @Test
    public void skalKalleOppdaterJournalfortInformasjonIHenvendelse() {
        meldinger = createMeldingListeMedEttSporsmaalOgEttSamtalereferat();
        traadVM = new TraadVM(meldinger);

        joarkJournalforingService.journalforTraad(traadVM, sak);

        verify(henvendelseBehandlingService, atLeast(2)).oppdaterJournalfortInformasjonIHenvendelse(any(Sak.class), anyString(), any(Melding.class));
    }

    @Test
    public void skalSetteRiktigeFelterIJournalforingInngaaendeRequest() {
        MeldingVM sporsmaal = new MeldingVM(new Melding("id", Meldingstype.SPORSMAL_SKRIFTLIG, DateTime.now()), 1);
        traadVM = new TraadVM(new ArrayList<>(Arrays.asList(sporsmaal)));

        joarkJournalforingService.journalforTraad(traadVM, sak);

        verify(behandleJournalV2).journalfoerInngaaendeHenvendelse(journalfoerInngaaendeRequestCaptor.capture());
        JournalfoerInngaaendeHenvendelseRequest request = journalfoerInngaaendeRequestCaptor.getValue();

        assertThat(request.getPersonFornavn(), is(NAVIDENT));
        assertThat(request.getPersonEtternavn(), is(NAVIDENT));
        assertThat(request.getApplikasjonsID(), is(MODIA_SYSTEM_ID));
        assertNotNull(request.getJournalpost());
    }

    @Test
    public void skalSetteRiktigeFelterIJournalforingUtgaaendeRequest() {
        MeldingVM sporsmaal = new MeldingVM(new Melding("id", Meldingstype.SVAR_SKRIFTLIG, DateTime.now()), 1);
        traadVM = new TraadVM(new ArrayList<>(Arrays.asList(sporsmaal)));

        joarkJournalforingService.journalforTraad(traadVM, sak);

        verify(behandleJournalV2).journalfoerUtgaaendeHenvendelse(journalfoerUtgaaendeHenvendelseRequestCaptor.capture());
        JournalfoerUtgaaendeHenvendelseRequest request = journalfoerUtgaaendeHenvendelseRequestCaptor.getValue();

        assertThat(request.getPersonFornavn(), is(NAVIDENT));
        assertThat(request.getPersonEtternavn(), is(NAVIDENT));
        assertThat(request.getApplikasjonsID(), is(MODIA_SYSTEM_ID));
        assertNotNull(request.getJournalpost());
    }

    @Test
    public void skalSetteRiktigeFelterIJournalforingNotatRequest() {
        MeldingVM sporsmaal = new MeldingVM(new Melding("id", Meldingstype.SAMTALEREFERAT_OPPMOTE, DateTime.now()), 1);
        sporsmaal.melding.kanal = JournalforingNotat.KANAL_TYPE_TELEFON;
        traadVM = new TraadVM(new ArrayList<>(Arrays.asList(sporsmaal)));

        joarkJournalforingService.journalforTraad(traadVM, sak);

        verify(behandleJournalV2).journalfoerNotat(journalfoerNotatRequestCaptor.capture());
        JournalfoerNotatRequest request = journalfoerNotatRequestCaptor.getValue();

        assertThat(request.getPersonFornavn(), is(NAVIDENT));
        assertThat(request.getPersonEtternavn(), is(NAVIDENT));
        assertThat(request.getApplikasjonsID(), is(MODIA_SYSTEM_ID));
        assertNotNull(request.getJournalpost());
    }

    private ArrayList<MeldingVM> createMeldingListeMedEttSporsmaalOgEttSamtalereferat() {
        return new ArrayList<>(asList(createMeldingVM(Meldingstype.SAMTALEREFERAT_OPPMOTE, 2, KANAL_TELEFON),
                createMeldingVM(Meldingstype.SPORSMAL_SKRIFTLIG, 2, "")));
    }

    private MeldingVM createMeldingVM(Meldingstype meldingstype, int traadlengde, String kanal) {
        Melding melding = createMelding("ID 2", meldingstype, DateTime.now(), "Temagruppe", "ID");
        melding.kanal = kanal;
        return new MeldingVM(melding, traadlengde);
    }

}
