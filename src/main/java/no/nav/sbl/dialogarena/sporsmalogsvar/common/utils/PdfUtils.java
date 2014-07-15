package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;


import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.pdf.HtmlGenerator;
import no.nav.sbl.dialogarena.pdf.HtmlToPdf;
import no.nav.sbl.dialogarena.pdf.PDFFabrikk;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class PdfUtils {

    @Inject
    private HtmlGenerator pdfTemplate;

    private HtmlToPdf pdfGenerator = new PDFFabrikk();

    public PdfUtils() {
    }

    public PdfUtils(HtmlGenerator pdfTemplate) {
        this.pdfTemplate = pdfTemplate;
    }

    public byte[] genererPdf(Melding melding) {
        HashMap<String, Helper<?>> helpers = generateHelpers();
        try {
            String html = pdfTemplate.fyllHtmlMalMedInnhold(melding, "html/melding", helpers);
            return pdfGenerator.lagPdfFil(html);
        } catch (IOException e) {
            throw new ApplicationException("Kunne ikke lage markup av melding", e);
        }
    }

    private HashMap<String, Helper<?>> generateHelpers() {
        HashMap<String, Helper<?>> result = new HashMap<String, Helper<?>>();

        Helper hentMeldingHelper = new Helper<Melding>() {
            @Override
            public CharSequence apply(Melding o, Options options) throws IOException {
                Melding melding = finnMelding(options.context);
                return melding.navIdent;
            }
        };

        Helper formaterDatoHelper = new Helper<DateTime>() {
            @Override
            public CharSequence apply(DateTime dato, Options options) throws IOException {
                Locale locale = new Locale("nb", "no");
                return dato.toString("d. MMMM yyyy", locale);
            }
        };

        result.put("hentMeldingHelper", hentMeldingHelper);
        result.put("formaterDato", formaterDatoHelper);

        return result;
    }

    private Melding finnMelding(Context context) {
        if (context == null) {
            return null;
        } else if (context.model() instanceof Melding) {
            return (Melding) context.model();
        } else {
            return finnMelding(context.parent());
        }
    }
}
