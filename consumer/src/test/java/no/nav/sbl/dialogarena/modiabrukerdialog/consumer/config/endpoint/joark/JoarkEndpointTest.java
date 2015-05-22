package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.joark;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.JoarkPortTypeMock;
import no.nav.tjeneste.virksomhet.journal.v1.binding.HentDokumentDokumentErSlettet;
import no.nav.tjeneste.virksomhet.journal.v1.binding.HentDokumentDokumentIkkeFunnet;
import no.nav.tjeneste.virksomhet.journal.v1.binding.HentDokumentSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.journal.v1.binding.JournalV1;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.Variantformater;
import no.nav.tjeneste.virksomhet.journal.v1.meldinger.HentDokumentRequest;
import no.nav.tjeneste.virksomhet.journal.v1.meldinger.HentDokumentResponse;
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
    private JournalV1 joarkPortType;

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
        HentDokumentRequest req = new HentDokumentRequest();
        req.setDokumentId("dokumentId");
        req.setJournalpostId("journalpostId");
        req.setVariantformat(new Variantformater());

        HentDokumentResponse res1 = new HentDokumentResponse();
        res1.setDokument("%PDF-1".getBytes());

        HentDokumentResponse res2 = new HentDokumentResponse();
        res2.setDokument("%PDF-2".getBytes());

        when(joarkPortType.hentDokument(any(HentDokumentRequest.class))).thenReturn(
                res1,
                res2
        );

        byte[] resp1 = joarkPortType.hentDokument(req).getDokument();
        byte[] resp2 = joarkPortType.hentDokument(req).getDokument();

        //Her ender jeg opp med a sjekke at om man kaller hentDokument med samme input to ganger sa
        //far man resultatet fra det forste kallet uansett. Om man f. eks. gar inn i cacheconfig og kommenterer ut
        //joarkAdvice sa feiler denne testen. Da kalles hentDokument 2 ganger og man far 2 forskjellig responser.
        assertThat(resp1, is(resp2));
    }
}
