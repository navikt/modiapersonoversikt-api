package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.joark;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.JoarkPortTypeMock;
import no.nav.tjeneste.virksomhet.journal.v1.HentDokumentDokumentErSlettet;
import no.nav.tjeneste.virksomhet.journal.v1.HentDokumentDokumentIkkeFunnet;
import no.nav.tjeneste.virksomhet.journal.v1.HentDokumentSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.journal.v1.Journal_v1PortType;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.WSVariantformater;
import no.nav.tjeneste.virksomhet.journal.v1.meldinger.WSHentDokumentRequest;
import no.nav.tjeneste.virksomhet.journal.v1.meldinger.WSHentDokumentResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.inject.Named;

import static java.lang.System.setProperty;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.joark.JoarkEndpointConfig.JOARK_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JoarkPortTypeMock.class})
public class JoarkEndpointTest extends CacheTest {

    @Inject
    @Named("joarkPortType")
    private Journal_v1PortType joarkPortType;

    public JoarkEndpointTest() {
        super("joarkCache");
    }

    @Before
    public void setup() {
        setProperty(JOARK_KEY, "true");
        setProperty(TILLATMOCKSETUP_PROPERTY, "true");
    }

    @Test
    public void kalletTilJoarkCaches() throws HentDokumentSikkerhetsbegrensning, HentDokumentDokumentIkkeFunnet, HentDokumentDokumentErSlettet {
        WSHentDokumentRequest req1 = new WSHentDokumentRequest()
                .withDokumentId("dokumentId")
                .withJournalpostId("journalpostId")
                .withVariantformat(new WSVariantformater().withValue("ARKIV"));

        WSHentDokumentRequest req2 = new WSHentDokumentRequest()
                .withDokumentId("dokumentId")
                .withJournalpostId("journalpostId")
                .withVariantformat(new WSVariantformater().withValue("ARKIV"));

        WSHentDokumentResponse res1 = new WSHentDokumentResponse()
                .withDokument("%PDF-1".getBytes());

        WSHentDokumentResponse res2 = new WSHentDokumentResponse()
                .withDokument("%PDF-2".getBytes());

        when(joarkPortType.hentDokument(any(WSHentDokumentRequest.class))).thenReturn(
                res1,
                res2
        );

        byte[] resp1 = joarkPortType.hentDokument(req1).getDokument();
        byte[] resp2 = joarkPortType.hentDokument(req2).getDokument();

        //Her ender jeg opp med a sjekke at om man kaller hentDokument med samme input to ganger sa
        //far man resultatet fra det forste kallet uansett. Om man f. eks. gar inn i cacheconfig og kommenterer ut
        //joarkAdvice sa feiler denne testen. Da kalles hentDokument 2 ganger og man far 2 forskjellig responser.
        assertThat(resp1, is(resp2));
    }
}
