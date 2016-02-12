package no.nav.sbl.dialogarena.sak.service;

import no.nav.tjeneste.virksomhet.innsynjournal.v1.InnsynJournalV1;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;

//@RunWith(MockitoJUnitRunner.class)
public class InnsynJournalServiceTest {

    @Mock
    private InnsynJournalV1 innsynJournalV1;

    @Mock
    private TilgangskontrollService tilgangskontrollService;

    @InjectMocks
    private InnsynJournalServiceImpl joarkService;


    @Before
    public void setup() {
    }


}