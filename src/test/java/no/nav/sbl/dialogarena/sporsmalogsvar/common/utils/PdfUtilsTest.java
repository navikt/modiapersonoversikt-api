package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;


import no.nav.sbl.dialogarena.pdf.HandleBarKjoerer;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.MeldingServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@ContextConfiguration(classes = MeldingServiceTestContext.class)
public class PdfUtilsTest {

    @Inject
    PdfUtils pdfUtils;

    @Test
    public void skalKunneLageNotatPdf(){
        pdfUtils = new PdfUtils(new HandleBarKjoerer());
        DateTime opprettetDato = DateTime.now();
        Melding melding = new Melding("ID", Meldingstype.SAMTALEREFERAT, opprettetDato);
        melding.fritekst = "Bruker ringte inn og sa ... Jeg svarte ...";
        melding.navIdent = "EN_NAV_IDENT";
        byte[] bytes = pdfUtils.genererPdf(melding);

        OutputStream out;
        try {
            out = new FileOutputStream("journalforing.pdf");
            out.write(bytes);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }


        Assert.assertTrue(bytes.length > 0);
    }

}
