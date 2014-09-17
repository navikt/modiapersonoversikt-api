package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import org.joda.time.DateTime;
import org.junit.Before;

import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createMelding;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createSak;


public abstract class TestDataJournalforing {

    protected static final String JOURNALFORENDE_ENHET_ID = "enhetid";
    protected static final String journalfortPostId = "journalfort postid";
    protected Sak sak;
    protected Melding melding;

    @Before
    public void setUp() {
        sak = createSak("saksid", "tema", "fagsak", "sakstype", DateTime.now());
        melding = createMelding("meldingid", Meldingstype.SAMTALEREFERAT_OPPMOTE, DateTime.now().minusDays(1), "temagruppe", "traadid");
        melding.kanal = JournalforingNotat.KANAL_TYPE_TELEFON;
    }

}
