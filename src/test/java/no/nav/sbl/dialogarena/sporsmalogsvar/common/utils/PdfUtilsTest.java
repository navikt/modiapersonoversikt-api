package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;


import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

public class PdfUtilsTest {

    @Test
    public void skalKunneLageNotatPdf() {
        DateTime opprettetDato = DateTime.now();
        Melding melding = new Melding("ID", Meldingstype.SAMTALEREFERAT, opprettetDato);
        melding.fritekst = "Bruker ringte inn og sa ... Jeg svarte ... Ca 400 ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord";
        melding.navIdent = "EN_NAV_IDENT";
        melding.fnrBruker = "10111212345";
        byte[] bytes = PdfUtils.genererPdf(melding);

        Assert.assertTrue(bytes.length > 0);
    }

    @Test
    public void skalKunneLageUtgaaendePdf() {
        DateTime opprettetDato = DateTime.now();
        Melding melding = new Melding("ID", Meldingstype.SVAR, opprettetDato);
        melding.fritekst = "Dersom du lurer på hvor mye du har rett på ... Ca 400 ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord ord";
        melding.navIdent = "EN_NAV_IDENT";
        melding.fnrBruker = "10111212345";
        byte[] bytes = PdfUtils.genererPdf(melding);

        Assert.assertTrue(bytes.length > 0);
    }

    @Test
    public void skalKunneLageInngaaendePdf() {
        DateTime opprettetDato = DateTime.now();
        Melding melding = new Melding("ID", Meldingstype.SPORSMAL, opprettetDato);
        melding.fritekst = "Jeg lurer på hvor mye jeg har rett på i forbindelse med ... Bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla ?";
        melding.fnrBruker = "10111212345";
        byte[] bytes = PdfUtils.genererPdf(melding);

        Assert.assertTrue(bytes.length > 0);
    }
}