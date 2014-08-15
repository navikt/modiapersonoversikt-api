package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;

import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.SAKSTYPE_GENERELL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createSak;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JournalfortSakVMTest {

    private final static String TEMA_1 = "Dagpenger";
    private final static String TEMA_2 = "Arbeidsstønad";
    private final static String TEMA_3 = "Oppfølging";
    private final static String TEMA_4 = "Dagpenger";

    private final static String SAKS_ID_1 = "111111111";
    private final static String SAKS_ID_2 = "222222222";
    private final static String SAKS_ID_3 = "333333333";
    private final static String SAKS_ID_4 = "444444444";

    @Mock
    private GsakService gsakService;
    @Mock
    private InnboksVM innboksVM;

    private ArrayList<Sak> saksliste;

    @Before
    public void setUp() {
        TraadVM traadVM = mock(TraadVM.class);
        when(innboksVM.getValgtTraad()).thenReturn(traadVM);
        saksliste = createSaksliste();
        when(gsakService.hentSakerForBruker(anyString())).thenReturn(saksliste);
        MeldingVM eldsteMeldingVM = opprettMeldingVMogSetJournalfortSaksId(SAKS_ID_3);
        when(traadVM.getEldsteMelding()).thenReturn(eldsteMeldingVM);
    }

    @Test
    public void sjekkAtGetSakMetodenReturnererKorrektSakBasertPaJournalforingsId() {
        JournalfortSakVM journalfortSakVM = new JournalfortSakVM(innboksVM, gsakService);
        journalfortSakVM.oppdater();

        assertThat(journalfortSakVM.getSak().saksId, is(SAKS_ID_3));
    }

    private MeldingVM opprettMeldingVMogSetJournalfortSaksId(String journalfortSaksId) {
        Melding melding = new Melding("", Meldingstype.SPORSMAL, DateTime.now());
        melding.journalfortSaksId = journalfortSaksId;
        return new MeldingVM(melding, 1);
    }


    private ArrayList<Sak> createSaksliste() {
        return new ArrayList<>(Arrays.asList(
                createSak(SAKS_ID_1, TEMA_1, "Fagsystem1", SAKSTYPE_GENERELL, DateTime.now().minusDays(4)),
                createSak(SAKS_ID_2, TEMA_2, "Fagsystem2", SAKSTYPE_GENERELL, DateTime.now().minusDays(3)),
                createSak(SAKS_ID_3, TEMA_3, "Fagsystem3", SAKSTYPE_GENERELL, DateTime.now().minusDays(9)),
                createSak(SAKS_ID_4, TEMA_4, "Fagsystem2", SAKSTYPE_GENERELL, DateTime.now().minusDays(2))
        ));
    }

}