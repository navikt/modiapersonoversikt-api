package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.modig.core.context.StaticSubjectHandler;
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
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.TestUtils.createMelding;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.TestUtils.createSak;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MeldingServiceTest {

    @Captor
    ArgumentCaptor<JournalfoerNotatRequest> journalfoerNotatRequestCaptor;
    @Captor
    ArgumentCaptor<JournalfoerUtgaaendeHenvendelseRequest> journalfoerUtgaaendeHenvendelseRequestCaptor;

    @Mock
    JournalfoerInngaaendeHenvendelseResponse journalfoerInngaaendeHenvendelseResponseMock;
    @Mock
    JournalfoerUtgaaendeHenvendelseResponse journalfoerUtgaaendeHenvendelseResponseMock;
    @Mock
    JournalfoerNotatResponse journalfoerNotatResponseMock;

    @Mock
    BehandleHenvendelsePortType behandleHenvendelsePortType;
    @Mock
    BehandleJournalV2 behandleJournalV2;
    @Mock
    ValgtEnhetService valgtEnhetService;

    @InjectMocks
    private MeldingService meldingService;

    private List<MeldingVM> meldinger;
    private TraadVM traadVM;
    private Sak sak;

    private static final String SPORSMAL_POST_ID = "7863478648";
    private static final String GENERELL_POST_ID = "623674836846";
    private static final String JOURNALFOERENDE_ENHET_ID = "1234";
    private static final String KANAL_TELEFON = "TELEFON";

    @Before
    public void setUp() {
        System.setProperty(StaticSubjectHandler.SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
        sak = createSak("542747214621", "Dagpenger", "Fagsystem", "Generell", DateTime.now());

        when(journalfoerInngaaendeHenvendelseResponseMock.getJournalpostId()).thenReturn(SPORSMAL_POST_ID);
        when(behandleJournalV2.journalfoerInngaaendeHenvendelse(any(JournalfoerInngaaendeHenvendelseRequest.class))).thenReturn(journalfoerInngaaendeHenvendelseResponseMock);

        when(journalfoerNotatResponseMock.getJournalpostId()).thenReturn(GENERELL_POST_ID);
        when(behandleJournalV2.journalfoerNotat(any(JournalfoerNotatRequest.class))).thenReturn(journalfoerNotatResponseMock);

        when(journalfoerUtgaaendeHenvendelseResponseMock.getJournalpostId()).thenReturn(GENERELL_POST_ID);
        when(behandleJournalV2.journalfoerUtgaaendeHenvendelse(any(JournalfoerUtgaaendeHenvendelseRequest.class))).thenReturn(journalfoerUtgaaendeHenvendelseResponseMock);

        when(valgtEnhetService.getEnhetId()).thenReturn(JOURNALFOERENDE_ENHET_ID);
    }

    @Test
    public void sjekkAtRiktigJounalforHenvendelseKallBlirUtfortGittTraadMedEttSporsmalOgEttSamtaleReferat() {
        meldinger = new ArrayList<>(asList(
                createMeldingVM(Meldingstype.SAMTALEREFERAT, 2, KANAL_TELEFON),
                createMeldingVM(Meldingstype.SPORSMAL, 2, "")));
        traadVM = new TraadVM(meldinger);

        meldingService.journalforTraad(traadVM, sak);

        verify(behandleJournalV2).journalfoerInngaaendeHenvendelse(any(JournalfoerInngaaendeHenvendelseRequest.class));
        verify(behandleJournalV2).journalfoerNotat(any(JournalfoerNotatRequest.class));
    }

    @Test
    public void sjekkAtRiktigJournalforHenvendelseKallBlirUtfortGittTraadMedKunEttReferat() {
        meldinger = new ArrayList<>(asList(createMeldingVM(Meldingstype.SAMTALEREFERAT, 1, KANAL_TELEFON)));
        traadVM = new TraadVM(meldinger);

        meldingService.journalforTraad(traadVM, sak);

        verify(behandleJournalV2).journalfoerNotat(any(JournalfoerNotatRequest.class));
    }

    @Test
    public void sjekkAtRiktigJournalforHenvendelseKallBlirUtfortGittTraadMedSporsmalOgEttSvar() {
        meldinger = new ArrayList<>(asList(createMeldingVM(Meldingstype.SVAR, 2, ""),
                createMeldingVM(Meldingstype.SPORSMAL, 2, "")));
        traadVM = new TraadVM(meldinger);

        meldingService.journalforTraad(traadVM, sak);

        verify(behandleJournalV2).journalfoerInngaaendeHenvendelse(any(JournalfoerInngaaendeHenvendelseRequest.class));
        verify(behandleJournalV2).journalfoerUtgaaendeHenvendelse(any(JournalfoerUtgaaendeHenvendelseRequest.class));
    }

    @Test
    public void sjekkAtRiktigKryssReferanseForSvarSettesTilKorresponderendeSporsmaalVedJournalforingAvHenvendelse() {
        meldinger = new ArrayList<>(asList(createMeldingVM(Meldingstype.SVAR, 2, ""),
                createMeldingVM(Meldingstype.SPORSMAL, 2, "")));
        traadVM = new TraadVM(meldinger);

        meldingService.journalforTraad(traadVM, sak);

        verify(behandleJournalV2).journalfoerUtgaaendeHenvendelse(journalfoerUtgaaendeHenvendelseRequestCaptor.capture());
        assertThat(journalfoerUtgaaendeHenvendelseRequestCaptor.getValue().getJournalpost().getKryssreferanseListe().get(0).getReferanseId(), is(SPORSMAL_POST_ID));
    }

    @Test
    public void sjekkAtRiktigKryssReferanseForReferatSettesTilKorresponderendeSporsmalVedJournalforingAvHenvendelse() {
        meldinger = new ArrayList<>(asList(createMeldingVM(Meldingstype.SAMTALEREFERAT, 2, KANAL_TELEFON),
                createMeldingVM(Meldingstype.SPORSMAL, 2, "")));
        traadVM = new TraadVM(meldinger);

        meldingService.journalforTraad(traadVM, sak);

        verify(behandleJournalV2).journalfoerNotat(journalfoerNotatRequestCaptor.capture());
        assertThat(journalfoerNotatRequestCaptor.getValue().getJournalpost().getKryssreferanseListe().get(0).getReferanseId(), is(SPORSMAL_POST_ID));
    }

    private MeldingVM createMeldingVM(Meldingstype meldingstype, int traadlengde, String kanal) {
        Melding melding = createMelding("ID 2", meldingstype, DateTime.now(), "Temagruppe", "ID");
        melding.kanal ="kanal";
        return new MeldingVM(melding, traadlengde);
    }

}