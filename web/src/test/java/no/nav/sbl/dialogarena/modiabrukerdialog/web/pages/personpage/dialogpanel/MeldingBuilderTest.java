package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SVAR_OPPMOTE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MeldingBuilderTest {

    private static final String FNR = "fnr";
    private static final String NAVIDENT = "navident";
    private static final String VALGT_ENHET = "valgtEnhet";

    @Test
    public void lagerMeldingMedRiktigeFelterFraHenvendelseVMOgBuilderverdierVedEnkeltstaaendeHenvendelse() {
        HenvendelseVM henvendelseVM = new HenvendelseVM();
        henvendelseVM.kanal = Kanal.TEKST;
        henvendelseVM.temagruppe = Temagruppe.ARBD;
        Whitebox.setInternalState(henvendelseVM, "text", "tekst");

        Melding melding = new MeldingBuilder()
                .withHenvendelseVM(henvendelseVM)
                .withMeldingstype(SVAR_OPPMOTE)
                .withFnr(FNR)
                .withNavident(NAVIDENT)
                .withValgtEnhet(VALGT_ENHET)
                .build();

        assertThat(melding.fnrBruker, is(FNR));
        assertThat(melding.navIdent, is(NAVIDENT));
        assertThat(melding.eksternAktor, is(NAVIDENT));
        assertThat(melding.meldingstype, is(SVAR_OPPMOTE));
        assertThat(melding.tilknyttetEnhet, is(VALGT_ENHET));

        assertThat(melding.kanal, is(henvendelseVM.kanal.name()));
        assertThat(melding.fritekst, is(henvendelseVM.getFritekst()));
        assertThat(melding.temagruppe, is(henvendelseVM.temagruppe.name()));
    }

    @Test
    public void lagerMeldingMedRiktigeFelterFraHenvendelseVMOgBuilderverdierVedHenvendelseITraad() {
        HenvendelseVM henvendelseVM = new HenvendelseVM();
        henvendelseVM.kanal = Kanal.TEKST;
        henvendelseVM.temagruppe = Temagruppe.ARBD;
        Whitebox.setInternalState(henvendelseVM, "text", "tekst");

        Melding eldsteMeldingITraad = new Melding()
                .withTemagruppe(Temagruppe.BIL.name())
                .withTraadId("traadId")
                .withKontorsperretEnhet("kontorsperretEnhet");

        Melding melding = new MeldingBuilder()
                .withHenvendelseVM(henvendelseVM)
                .withEldsteMeldingITraad(Optional.optional(eldsteMeldingITraad))
                .withMeldingstype(SVAR_OPPMOTE)
                .withFnr(FNR)
                .withNavident(NAVIDENT)
                .withValgtEnhet(VALGT_ENHET)
                .build();

        assertThat(melding.fnrBruker, is(FNR));
        assertThat(melding.navIdent, is(NAVIDENT));
        assertThat(melding.eksternAktor, is(NAVIDENT));
        assertThat(melding.meldingstype, is(SVAR_OPPMOTE));
        assertThat(melding.tilknyttetEnhet, is(VALGT_ENHET));

        assertThat(melding.kanal, is(henvendelseVM.kanal.name()));
        assertThat(melding.fritekst, is(henvendelseVM.getFritekst()));
        assertThat(melding.temagruppe, is(eldsteMeldingITraad.temagruppe));
        assertThat(melding.traadId, is(eldsteMeldingITraad.id));
        assertThat(melding.kontorsperretEnhet, is(eldsteMeldingITraad.kontorsperretEnhet));
    }

    @Test
    public void setterTilknyttetAnsattFlaggTilTrue() {
        HenvendelseVM henvendelseVM = new HenvendelseVM();
        henvendelseVM.kanal = Kanal.TEKST;
        henvendelseVM.temagruppe = Temagruppe.ARBD;
        henvendelseVM.oppgaveTilknytning = HenvendelseVM.OppgaveTilknytning.SAKSBEHANDLER;

        Melding melding = new MeldingBuilder().withHenvendelseVM(henvendelseVM).build();

        assertThat(melding.erTilknyttetAnsatt, is(true));
    }

    @Test
    public void setterTilknyttetAnsattFlaggTilFalse() {
        HenvendelseVM henvendelseVM = new HenvendelseVM();
        henvendelseVM.kanal = Kanal.TEKST;
        henvendelseVM.temagruppe = Temagruppe.ARBD;
        henvendelseVM.oppgaveTilknytning = HenvendelseVM.OppgaveTilknytning.ENHET;

        Melding melding = new MeldingBuilder().withHenvendelseVM(henvendelseVM).build();

        assertThat(melding.erTilknyttetAnsatt, is(false));
    }

}