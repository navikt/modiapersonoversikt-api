package no.nav.sbl.dialogarena.sak.service;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.HentDokumentResultat;
import no.nav.tjeneste.virksomhet.journal.v1.HentDokumentDokumentErSlettet;
import no.nav.tjeneste.virksomhet.journal.v1.HentDokumentDokumentIkkeFunnet;
import no.nav.tjeneste.virksomhet.journal.v1.HentDokumentSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.journal.v1.Journal_v1PortType;
import no.nav.tjeneste.virksomhet.journal.v1.feil.WSDokumentErSlettet;
import no.nav.tjeneste.virksomhet.journal.v1.feil.WSDokumentIkkeFunnet;
import no.nav.tjeneste.virksomhet.journal.v1.feil.WSSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.journal.v1.meldinger.WSHentDokumentRequest;
import no.nav.tjeneste.virksomhet.journal.v1.meldinger.WSHentDokumentResponse;
import org.apache.wicket.util.crypt.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.HentDokumentResultat.Feilmelding.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JoarkServiceTest {

    @Mock
    private Journal_v1PortType joarkPortType;

    @Mock
    private TilgangskontrollService tilgangskontrollService;

    @InjectMocks
    private JoarkServiceImpl joarkService;

    private final String journalpostId = "journalpostid";
    private final String dokumentId = "dokumentid";
    private final String fnr = "12345";
    private final String sakstemakode = "FOR";

    private final byte[] pdfSomBytes = StringUtils.getBytesUtf8("%PDF-1.5");

    @Before
    public void setup() {
        when(tilgangskontrollService.harSaksbehandlerTilgangTilDokument(anyString(), anyString(), anyString())).thenReturn(new HentDokumentResultat(true));
    }

    @Test
    public void skalReturnerePdfSomBytesOgIngenFeilmelding() throws HentDokumentSikkerhetsbegrensning, HentDokumentDokumentIkkeFunnet, HentDokumentDokumentErSlettet {
        when(joarkPortType.hentDokument(any(WSHentDokumentRequest.class))).thenReturn(new WSHentDokumentResponse().withDokument(pdfSomBytes));

        HentDokumentResultat resultat = joarkService.hentDokument(journalpostId, dokumentId, fnr, sakstemakode);

        assertThat(resultat.pdfSomBytes.isSome(), is(true));
        assertThat(resultat.feilmelding, is(nullValue()));
    }

    @Test
    public void skalReturnereKorrektFeilmeldingForSikkerhetsbegrensning() throws HentDokumentSikkerhetsbegrensning, HentDokumentDokumentIkkeFunnet, HentDokumentDokumentErSlettet {
        when(joarkPortType.hentDokument(any(WSHentDokumentRequest.class))).thenThrow(new HentDokumentSikkerhetsbegrensning("", new WSSikkerhetsbegrensning()));

        HentDokumentResultat resultat = joarkService.hentDokument(journalpostId, dokumentId, fnr, sakstemakode);

        assertThat(resultat.pdfSomBytes.isSome(), is(false));
        assertThat(resultat.feilmelding, is(SIKKERHETSBEGRENSNING));
    }

    @Test
    public void skalReturnereKorrektFeilmeldingForDokumentIkkeFunnet() throws HentDokumentSikkerhetsbegrensning, HentDokumentDokumentIkkeFunnet, HentDokumentDokumentErSlettet {
        when(joarkPortType.hentDokument(any(WSHentDokumentRequest.class))).thenThrow(new HentDokumentDokumentIkkeFunnet("", new WSDokumentIkkeFunnet()));

        HentDokumentResultat resultat = joarkService.hentDokument(journalpostId, dokumentId, fnr, sakstemakode);

        assertThat(resultat.pdfSomBytes.isSome(), is(false));
        assertThat(resultat.feilmelding, is(DOKUMENT_IKKE_FUNNET));
    }

    @Test
    public void skalReturnereKorrektFeilmeldingForDokumentErSlettet() throws HentDokumentSikkerhetsbegrensning, HentDokumentDokumentIkkeFunnet, HentDokumentDokumentErSlettet {
        when(joarkPortType.hentDokument(any(WSHentDokumentRequest.class))).thenThrow(new HentDokumentDokumentErSlettet("", new WSDokumentErSlettet()));

        HentDokumentResultat resultat = joarkService.hentDokument(journalpostId, dokumentId, fnr, sakstemakode);

        assertThat(resultat.pdfSomBytes.isSome(), is(false));
        assertThat(resultat.feilmelding, is(DOKUMENT_SLETTET));
    }
}