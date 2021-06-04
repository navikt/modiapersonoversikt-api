package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.FeilendeBaksystemException;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.InnsynJournalV2Service;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.binding.*;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.informasjon.Dokument;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.informasjon.InnsynDokument;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.meldinger.IdentifiserJournalpostResponse;
import org.junit.Test;

import java.util.Collections;

import static junit.framework.TestCase.fail;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Baksystem.JOARK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InnsynJournalV2ServiceImplTest {


    public static final String JOURNALPOST_ID = "123";
    public static final String HOVEDDOKUMENT_ID = "333";
    public static final String VEDLEGG_TITTEL = "Vedlegg tittel";
    public static final String VEDLEGG_ID = "555";
    public static final String BEHANDLINGS_ID = "777";

    private InnsynJournalV2 innsynJournalV2 = mock(InnsynJournalV2.class);
    private InnsynJournalV2Service innsynJournalV2Service = new InnsynJournalV2ServiceImpl(innsynJournalV2);


    @Test
    public void identifiserJournalpostHappyCase()
            throws IdentifiserJournalpostJournalpostIkkeInngaaende,
            IdentifiserJournalpostObjektIkkeFunnet,
            IdentifiserJournalpostUgyldigAntallJournalposter,
            IdentifiserJournalpostUgyldingInput {

        when(innsynJournalV2.identifiserJournalpost(any())).thenReturn(defaultIdentifiserJournalpostResponse());

        DokumentMetadata resultat = innsynJournalV2Service.identifiserJournalpost(BEHANDLINGS_ID).resultat;

        assertThat(resultat.getJournalpostId(), is(JOURNALPOST_ID));
        assertThat(resultat.getHoveddokument().getDokumentreferanse(), is(HOVEDDOKUMENT_ID));
        assertThat(resultat.getHoveddokument().isKanVises(), is(true));
        assertThat(resultat.getVedlegg().get(0).getTittel(), is(VEDLEGG_TITTEL));
        assertThat(resultat.getVedlegg().get(0).isKanVises(), is(true));
    }

    @Test
    public void feilendeBaksystemExceptionOmRuntimeException() {
        try {
            when(innsynJournalV2.identifiserJournalpost(any())).thenThrow(new RuntimeException());

            innsynJournalV2Service.identifiserJournalpost(BEHANDLINGS_ID);
        } catch (FeilendeBaksystemException e) {
            assertThat(e.getBaksystem(), is(JOARK));
        } catch (Exception e) {
            fail("En uventet exception ble kastet");
        }
    }

    @Test
    public void feilendeBaksystemResultatOmJournalpostIkkeInngaende() {
        assertJoarkFeilendeBaksystemForException(new IdentifiserJournalpostJournalpostIkkeInngaaende());
    }

    @Test
    public void feilendeBaksystemResultatOmObjektIkkeFunnet() {
        assertJoarkFeilendeBaksystemForException(new IdentifiserJournalpostObjektIkkeFunnet());
    }

    @Test
    public void feilendeBaksystemResultatOmUgyldigAntallJournalposter() {
        assertJoarkFeilendeBaksystemForException(new IdentifiserJournalpostUgyldigAntallJournalposter());
    }

    @Test
    public void feilendeBaksystemResultatOmUgyldigInput() {
        assertJoarkFeilendeBaksystemForException(new IdentifiserJournalpostUgyldingInput());
    }

    private void assertJoarkFeilendeBaksystemForException(Exception exception) {
        ResultatWrapper<DokumentMetadata> wrapper;

        try {
            when(innsynJournalV2.identifiserJournalpost(any())).thenThrow(exception);

            wrapper = innsynJournalV2Service.identifiserJournalpost(BEHANDLINGS_ID);
        } catch (Exception e) {
            fail("En uventet exception ble kastet");
            return;
        }

        assertThat(wrapper.resultat, nullValue());
        assertThat(wrapper.feilendeSystemer, contains(JOARK));
    }

    private IdentifiserJournalpostResponse defaultIdentifiserJournalpostResponse() {
        return new IdentifiserJournalpostResponse()
                .withJournalpostId(JOURNALPOST_ID)
                .withHoveddokument(defaultHovedDokument())
                .withVedleggListe(Collections.singleton(defaultVedlegg()));
    }

    private Dokument defaultHovedDokument() {
        return new Dokument()
                .withDokumentId(HOVEDDOKUMENT_ID)
                .withInnsynDokument(InnsynDokument.JA);
    }

    private Dokument defaultVedlegg() {
        return new Dokument()
                .withTittel(VEDLEGG_TITTEL)
                .withInnsynDokument(InnsynDokument.JA)
                .withDokumentId(VEDLEGG_ID);
    }
}
