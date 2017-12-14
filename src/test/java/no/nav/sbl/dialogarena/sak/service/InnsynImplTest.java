package no.nav.sbl.dialogarena.sak.service;

import no.nav.sbl.dialogarena.sak.transformers.JournalpostTransformer;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sak;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.TjenesteResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.FeilendeBaksystemException;
import no.nav.tjeneste.virksomhet.journal.v2.HentDokumentDokumentIkkeFunnet;
import no.nav.tjeneste.virksomhet.journal.v2.HentDokumentSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.journal.v2.HentJournalpostListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.journal.v2.JournalV2;
import no.nav.tjeneste.virksomhet.journal.v2.meldinger.WSHentDokumentRequest;
import no.nav.tjeneste.virksomhet.journal.v2.meldinger.WSHentDokumentResponse;
import no.nav.tjeneste.virksomhet.journal.v2.meldinger.WSHentJournalpostListeResponse;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.sak.mock.JoarkMock.navMottattDokumentFraBruker;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem.JOARK;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Feilmelding.DOKUMENT_IKKE_FUNNET;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Feilmelding.SIKKERHETSBEGRENSNING;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InnsynImplTest {

    @Mock
    private JournalV2 journalV2;

    @Mock
    private JournalpostTransformer journalpostTransformer;

    @InjectMocks
    private InnsynImpl innsynImpl;

    @Test
    public void hentDokumentGirDokumentNaarAltGaaBra() throws HentDokumentSikkerhetsbegrensning, HentDokumentDokumentIkkeFunnet {
        byte[] bytes = new byte[2];
        when(journalV2.hentDokument(any(WSHentDokumentRequest.class))).thenReturn(new WSHentDokumentResponse().withDokument(bytes));
        final TjenesteResultatWrapper resultatWrapper = innsynImpl.hentDokument("1", "2");
        assertThat(resultatWrapper.result.get(), is(bytes));
    }

    @Test
    public void hentDokumentGirDokumentIkkeFunnet() throws HentDokumentSikkerhetsbegrensning, HentDokumentDokumentIkkeFunnet {
        when(journalV2.hentDokument(any(WSHentDokumentRequest.class))).thenThrow(new HentDokumentDokumentIkkeFunnet("Ikke funnet"));
        final TjenesteResultatWrapper resultatWrapper = innsynImpl.hentDokument("1", "2");
        assertThat(resultatWrapper.feilmelding, is(DOKUMENT_IKKE_FUNNET));
    }

    @Test
    public void hentDokumentGirSikkerhetsbegrensning() throws HentDokumentSikkerhetsbegrensning, HentDokumentDokumentIkkeFunnet {
        when(journalV2.hentDokument(any(WSHentDokumentRequest.class))).thenThrow(new HentDokumentSikkerhetsbegrensning("Du har ikke tilgang"));
        final TjenesteResultatWrapper resultatWrapper = innsynImpl.hentDokument("1", "2");
        assertThat(resultatWrapper.feilmelding, is(SIKKERHETSBEGRENSNING));
    }

    //TODO når feilhåndtering kommer inn kan denne fikses
    @Test
    @Ignore
    public void hentDokumentGirJoarkSomFeilendeBaksystemVedUkjentFeil() throws HentDokumentSikkerhetsbegrensning, HentDokumentDokumentIkkeFunnet {
        when(journalV2.hentDokument(any(WSHentDokumentRequest.class))).thenThrow(new RuntimeException("tjenesten er nede!!"));
        Baksystem feilendeBaksystem = null;
        try {
            innsynImpl.hentDokument("1", "2");
        } catch (FeilendeBaksystemException e) {
            feilendeBaksystem = e.getBaksystem();
        }
        assertThat(feilendeBaksystem, is(JOARK));
    }

    @Test
    public void hentTilgjengeligJournalpostListeOK() throws HentJournalpostListeSikkerhetsbegrensning, DatatypeConfigurationException {
        when(journalV2.hentJournalpostListe(any())).thenReturn(new WSHentJournalpostListeResponse().withJournalpostListe(
                asList(
                        navMottattDokumentFraBruker()
                )
        ));

        when(journalpostTransformer.dokumentMetadataFraJournalPost(any(), anyString(), any(String.class))).thenReturn(new ResultatWrapper<>(new DokumentMetadata().withTilhorendeSakid("1").withTilhorendeFagsakId("fagsak1")));
        ResultatWrapper<List<DokumentMetadata>> wrapper = innsynImpl.hentTilgjengeligJournalpostListe(asList(new Sak().withSaksId("1").withFagsaksnummer("fagsak1")), "12345678901");

        List<DokumentMetadata> dokumentmetadata = wrapper.resultat;

        assertEquals(dokumentmetadata.size(), 1);
    }

    @Test(expected = FeilendeBaksystemException.class)
    public void hentTilgjengeligJournalpostRuntimeExceptionGirFeilendeBaksystemJoark() throws HentJournalpostListeSikkerhetsbegrensning, DatatypeConfigurationException {
        when(journalV2.hentJournalpostListe(any())).thenThrow(new RuntimeException());

        innsynImpl.hentTilgjengeligJournalpostListe(asList(new Sak().withSaksId("123")), "12345678901");
    }

    @Test
    public void hentTilgjengeligJournalpostSikkerhetsbegrensningGirTomtResultatOgFeilendeSystem() throws HentJournalpostListeSikkerhetsbegrensning, DatatypeConfigurationException {
        when(journalV2.hentJournalpostListe(any())).thenThrow(new HentJournalpostListeSikkerhetsbegrensning());

        ResultatWrapper<List<DokumentMetadata>> wrapper = innsynImpl.hentTilgjengeligJournalpostListe(asList(new Sak().withSaksId("123")), "12345678901");
        assertThat(wrapper.resultat.size(), is(0));
        assertThat(wrapper.feilendeSystemer.size(), is(1));
    }


    @Test
    public void feilWrappesiResultObjektet() throws HentJournalpostListeSikkerhetsbegrensning {
        when(journalV2.hentJournalpostListe(any())).thenThrow(new HentJournalpostListeSikkerhetsbegrensning());

        ResultatWrapper<List<DokumentMetadata>> wrapper = innsynImpl.hentTilgjengeligJournalpostListe(asList(new Sak().withSaksId("123")), "12345678901");
        assertThat(wrapper.resultat.size(),is(0));
    }

}
