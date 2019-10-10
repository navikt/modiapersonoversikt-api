package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Fritekst;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.PdfUtils.PDFMelding;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PdfUtilsTest {
    @Test
    public void lagPDFMeldingstypeTest() {
        PDFMelding pdfmelding1 = new PDFMelding(new Melding("ID", Meldingstype.SAMTALEREFERAT_OPPMOTE, DateTime.now()));
        PDFMelding pdfmelding2 = new PDFMelding(new Melding("ID", Meldingstype.SPORSMAL_MODIA_UTGAAENDE, DateTime.now()));
        PDFMelding pdfmelding3 = new PDFMelding(new Melding("ID", Meldingstype.SVAR_SBL_INNGAAENDE, DateTime.now()));

        assertThat(pdfmelding1.meldingstype, is("Samtalereferat"));
        assertThat(pdfmelding2.meldingstype, is("Spørsmål"));
        assertThat(pdfmelding3.meldingstype, is("Svar"));
    }

    @Test
    public void avBrukerTest() {
        Melding melding1 = new Melding("ID", Meldingstype.SPORSMAL_SKRIFTLIG, DateTime.now());
        melding1.fnrBruker = "11111111111";
        melding1.navIdent = "navIdent";
        PDFMelding pdfmelding1 = new PDFMelding(melding1);

        Melding melding2 = new Melding("ID", Meldingstype.SPORSMAL_MODIA_UTGAAENDE, DateTime.now());
        melding2.fnrBruker = "11111111111";
        melding2.withFritekst(new Fritekst("", new Saksbehandler("", "", "navIdent"), DateTime.now()));
        PDFMelding pdfmelding2 = new PDFMelding(melding2);

        assertThat(pdfmelding1.avBruker, is("11111111111"));
        assertThat(pdfmelding2.avBruker, is("navIdent"));
    }

    @Test
    public void typeBeskrivelseTest() {
        PDFMelding pdfmelding1 = new PDFMelding(new Melding("ID", Meldingstype.SAMTALEREFERAT_OPPMOTE, DateTime.now()));
        PDFMelding pdfmelding2 = new PDFMelding(new Melding("ID", Meldingstype.SPORSMAL_MODIA_UTGAAENDE, DateTime.now()));
        PDFMelding pdfmelding3 = new PDFMelding(new Melding("ID", Meldingstype.SVAR_SBL_INNGAAENDE, DateTime.now()));

        assertThat(pdfmelding1.typeBeskrivelse, is("Type: samtalereferat (N)"));
        assertThat(pdfmelding2.typeBeskrivelse, is("Type: utgående henvendelse (U)"));
        assertThat(pdfmelding3.typeBeskrivelse, is("Type: innkommende henvendelse (I)"));
    }

    @Test
    public void lagTemagruppeBeskrivelseTest() {
        Melding melding1 = new Melding("ID", Meldingstype.SAMTALEREFERAT_OPPMOTE, DateTime.now());
        melding1.temagruppe = "ARBD";
        PDFMelding pdfmelding1 = new PDFMelding(melding1);

        Melding melding2 = new Melding("ID", Meldingstype.SPORSMAL_MODIA_UTGAAENDE, DateTime.now());
        melding2.temagruppe = "FMLI";
        PDFMelding pdfmelding2 = new PDFMelding(melding2);

        Melding melding3 = new Melding("ID", Meldingstype.SPORSMAL_MODIA_UTGAAENDE, DateTime.now());
        melding3.temagruppe = "";
        PDFMelding pdfmelding3 = new PDFMelding(melding3);

        assertThat(pdfmelding1.temagruppeBeskrivelse, is("Temagruppe: Arbeid"));
        assertThat(pdfmelding2.temagruppeBeskrivelse, is("Temagruppe: Familie"));
        assertThat(pdfmelding3.temagruppeBeskrivelse, is(""));
    }

    @Test
    public void harMappingForSamtligeTemagrupper() {
        for (Temagruppe temagruppe : Temagruppe.values()) {
            assertTrue(PdfUtils.TEMAGRUPPE_MAP.containsKey(temagruppe));
        }
    }
}
