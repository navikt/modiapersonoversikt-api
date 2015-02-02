package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.PdfUtils.PDFMelding;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PdfUtilsTest {

    @Test
    public void skalKunneLageNotatPdf() {
        DateTime opprettetDato = DateTime.now();
        Melding melding = new Melding("ID", Meldingstype.SAMTALEREFERAT_OPPMOTE, opprettetDato);
        melding.fritekst = "Bruker ringte inn og sa ... Jeg svarte ... Ca 400 ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord";
        melding.navIdent = "EN_NAV_IDENT";
        melding.fnrBruker = "10111212345";
        byte[] bytes = PdfUtils.genererPdf(melding);

        Assert.assertTrue(bytes.length > 0);
    }

    @Test
    public void skalKunneLageUtgaaendePdf() {
        DateTime opprettetDato = DateTime.now();
        Melding melding = new Melding("ID", Meldingstype.SVAR_SKRIFTLIG, opprettetDato);
        melding.fritekst = "Dersom du lurer på hvor mye du har rett på ... Ca 400 ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord";
        melding.navIdent = "EN_NAV_IDENT";
        melding.fnrBruker = "10111212345";
        byte[] bytes = PdfUtils.genererPdf(melding);

        Assert.assertTrue(bytes.length > 0);
    }

    @Test
    public void skalKunneLageInngaaendePdf() {
        DateTime opprettetDato = DateTime.now();
        Melding melding = new Melding("ID", Meldingstype.SPORSMAL_SKRIFTLIG, opprettetDato);
        melding.fritekst = "Jeg lurer på hvor mye jeg har rett på i forbindelse med ... Bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla ?";
        melding.fnrBruker = "10111212345";
        byte[] bytes = PdfUtils.genererPdf(melding);

        Assert.assertTrue(bytes.length > 0);
    }

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
        melding2.navIdent = "navIdent";
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
}