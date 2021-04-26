package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.joark;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.binding.InnsynJournalV2;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.meldinger.IdentifiserJournalpostRequest;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.meldinger.IdentifiserJournalpostResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.joark.InnsynJournalEndpointConfig.INNSYN_JOURNAL_V2_URL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.*;


public class InnsynJournalCacheTest extends CacheTest {

    private static final String INNSYN_CACHE = "innsynJournalCache";
    public static final String JOURNALPOST_ID_1 = "3333";
    public static final String JOURNALPOST_ID_2 = "4444";
    public static final String BEHANDLINGS_ID_1 = "1111";
    public static final String BEHANDLINGS_ID_2 = "2222";


    public InnsynJournalCacheTest() {
        super(INNSYN_CACHE);
    }

    @Autowired
    private InnsynJournalV2 innsynJournalV2;

    @BeforeAll
    public static void setUp() {
        System.setProperty(INNSYN_JOURNAL_V2_URL, "url");
    }

    @BeforeEach
    void setUpMock() throws Exception {
        InnsynJournalV2 unwrapped = (InnsynJournalV2) unwrapProxy(innsynJournalV2);
        reset(unwrapped);

        IdentifiserJournalpostResponse response1 = new IdentifiserJournalpostResponse().withJournalpostId(JOURNALPOST_ID_1);
        IdentifiserJournalpostResponse response2 = new IdentifiserJournalpostResponse().withJournalpostId(JOURNALPOST_ID_2);
        when(unwrapped.identifiserJournalpost(any())).thenReturn(response1, response2);

    }


    @Test
    void toKallTilIdentifiserJournalPostMedSammeDataGirBareEttKall() throws Exception {
        IdentifiserJournalpostRequest identifiserJournalpostRequest = new IdentifiserJournalpostRequest().withKanalReferanseId(BEHANDLINGS_ID_1);

        IdentifiserJournalpostResponse response1 = innsynJournalV2.identifiserJournalpost(identifiserJournalpostRequest);
        IdentifiserJournalpostResponse response2 = innsynJournalV2.identifiserJournalpost(identifiserJournalpostRequest);

        InnsynJournalV2 unwrapped = (InnsynJournalV2) unwrapProxy(innsynJournalV2);
        verify(unwrapped, times(1)).identifiserJournalpost(any());

        assertThat(response1.getJournalpostId(), is(response2.getJournalpostId()));

    }

    @Test
    void toKallTilIdentifiserJournalPostMedForskjelligDataGirToKall() throws Exception {
        IdentifiserJournalpostRequest identifiserJournalpostRequest1 = new IdentifiserJournalpostRequest().withKanalReferanseId(BEHANDLINGS_ID_1);
        IdentifiserJournalpostRequest identifiserJournalpostRequest2 = new IdentifiserJournalpostRequest().withKanalReferanseId(BEHANDLINGS_ID_2);

        IdentifiserJournalpostResponse response1 = innsynJournalV2.identifiserJournalpost(identifiserJournalpostRequest1);
        IdentifiserJournalpostResponse response2 = innsynJournalV2.identifiserJournalpost(identifiserJournalpostRequest2);

        InnsynJournalV2 unwrapped = (InnsynJournalV2) unwrapProxy(innsynJournalV2);
        verify(unwrapped, times(2)).identifiserJournalpost(any());

        assertThat(response1.getJournalpostId(), is(not(response2.getJournalpostId())));
    }
}
