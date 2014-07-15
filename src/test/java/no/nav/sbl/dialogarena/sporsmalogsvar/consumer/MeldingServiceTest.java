package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.modig.core.context.StaticSubjectHandler;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.binding.BehandleJournalV2;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.*;
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
    ArgumentCaptor<JournalfoerInngaaendeHenvendelseRequest> journalfoerInngaaendeHenvendelseRequestArgumentCaptor;

    @Captor
    ArgumentCaptor<JournalfoerNotatRequest> journalfoerNotatRequestArgumentCaptor;

    @Captor
    ArgumentCaptor<JournalfoerUtgaaendeHenvendelseRequest> journalfoerUtgaaendeHenvendelseRequestArgumentCaptor;

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

    @InjectMocks
    private MeldingService meldingService;

    private List<MeldingVM> meldinger;
    private TraadVM traadVM;
    private  Sak sak;

    private static final String FODSELSNUMMER = "12341234123";
    private static final String SPORSMAL_POST_ID = "7863478648";
    private static final String GENERELL_POST_ID = "623674836846";

    @Before
    public void setUp(){
        System.setProperty(StaticSubjectHandler.SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
        sak = createSak("542747214621", "Dagpenger", "Fagsystem", "Generell", DateTime.now());

        when(behandleJournalV2.journalfoerInngaaendeHenvendelse(any(JournalfoerInngaaendeHenvendelseRequest.class))).thenReturn(journalfoerInngaaendeHenvendelseResponseMock);
        when(journalfoerInngaaendeHenvendelseResponseMock.getJournalpostId()).thenReturn(SPORSMAL_POST_ID);

        when(behandleJournalV2.journalfoerNotat(any(JournalfoerNotatRequest.class))).thenReturn(journalfoerNotatResponseMock);
        when(journalfoerNotatResponseMock.getJournalpostId()).thenReturn(GENERELL_POST_ID);

        when(behandleJournalV2.journalfoerUtgaaendeHenvendelse(any(JournalfoerUtgaaendeHenvendelseRequest.class))).thenReturn(journalfoerUtgaaendeHenvendelseResponseMock);
        when(journalfoerUtgaaendeHenvendelseResponseMock.getJournalpostId()).thenReturn(GENERELL_POST_ID);
    }

    @Test
    public void sjekkAtRiktigJounalforHenvendelseKallBlirUtfortGittTraadMedEttSporsmalOgEttSamtaleReferat(){
        meldinger = new ArrayList<>(Arrays.asList(createMeldingVM(Meldingstype.SAMTALEREFERAT, 2),
                                                  createMeldingVM(Meldingstype.SPORSMAL,2)));
        traadVM = new TraadVM(meldinger);

        meldingService.journalforTraad(traadVM, sak, FODSELSNUMMER);

        verify(behandleJournalV2).journalfoerInngaaendeHenvendelse(journalfoerInngaaendeHenvendelseRequestArgumentCaptor.capture());
        verify(behandleJournalV2).journalfoerNotat(journalfoerNotatRequestArgumentCaptor.capture());
    }

    @Test
    public void sjekkAtRiktigJournalforHenvendelseKallBlirUtfortGittTraadMedKunEttReferat(){
        meldinger = new ArrayList<>(Arrays.asList(createMeldingVM(Meldingstype.SAMTALEREFERAT, 1)));
        traadVM = new TraadVM(meldinger);

        meldingService.journalforTraad(traadVM, sak, FODSELSNUMMER);

        verify(behandleJournalV2).journalfoerNotat(journalfoerNotatRequestArgumentCaptor.capture());
    }

    @Test
    public void sjekkAtRiktigJournalforHenvendelseKallBlirUtfortGittTraadMedSporsmalOgEttSvar(){
        meldinger = new ArrayList<>(Arrays.asList(createMeldingVM(Meldingstype.SVAR, 2),
                                                  createMeldingVM(Meldingstype.SPORSMAL,2)));
        traadVM = new TraadVM(meldinger);

        meldingService.journalforTraad(traadVM, sak, FODSELSNUMMER);

        verify(behandleJournalV2).journalfoerInngaaendeHenvendelse(journalfoerInngaaendeHenvendelseRequestArgumentCaptor.capture());
        verify(behandleJournalV2).journalfoerUtgaaendeHenvendelse(journalfoerUtgaaendeHenvendelseRequestArgumentCaptor.capture());
    }

    @Test
    public void sjekkAtRiktigKryssReferanseForSvarSettesTilKorresponderendeSporsmaalVedJournalforingAvHenvendelse(){
        meldinger = new ArrayList<>(Arrays.asList(createMeldingVM(Meldingstype.SVAR, 2),
                                                  createMeldingVM(Meldingstype.SPORSMAL,2)));
        traadVM = new TraadVM(meldinger);

        meldingService.journalforTraad(traadVM, sak, FODSELSNUMMER);

        verify(behandleJournalV2).journalfoerUtgaaendeHenvendelse(journalfoerUtgaaendeHenvendelseRequestArgumentCaptor.capture());
        assertThat(journalfoerUtgaaendeHenvendelseRequestArgumentCaptor.getValue().getJournalpost().getKryssreferanseListe().get(0).getReferanseId(),is(SPORSMAL_POST_ID));
    }

    @Test
    public void sjekkAtRiktigKryssReferanseForReferatSettesTilKorresponderendeSporsmalVedJournalforingAvHenvendelse(){
        meldinger = new ArrayList<>(Arrays.asList(createMeldingVM(Meldingstype.SAMTALEREFERAT, 2),
                                                  createMeldingVM(Meldingstype.SPORSMAL,2)));
        traadVM = new TraadVM(meldinger);

        meldingService.journalforTraad(traadVM, sak, FODSELSNUMMER);

        verify(behandleJournalV2).journalfoerNotat(journalfoerNotatRequestArgumentCaptor.capture());
        assertThat(journalfoerNotatRequestArgumentCaptor.getValue().getJournalpost().getKryssreferanseListe().get(0).getReferanseId(),is(SPORSMAL_POST_ID));
    }

    private MeldingVM createMeldingVM(Meldingstype meldingstype, int traadlengde){
        return new MeldingVM(createMelding("ID 2", meldingstype, DateTime.now(), "Temagruppe", "ID"), traadlengde);
    }

}