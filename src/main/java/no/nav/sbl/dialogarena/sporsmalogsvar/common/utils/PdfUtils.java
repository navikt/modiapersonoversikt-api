package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;


import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.pdf.HandleBarHtmlGenerator;
import no.nav.sbl.dialogarena.pdf.PDFFabrikk;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PdfUtils {

    public static byte[] genererPdf(Melding melding) {
        Map<String, Helper<?>> helpers = generateHelpers();
        try {
            PDFMelding innhold = new PDFMelding(melding.fnrBruker, melding.meldingstype.name(), melding.navIdent, melding.fritekst, melding.opprettetDato);
            String html = HandleBarHtmlGenerator.fyllHtmlMalMedInnhold(innhold, "html/melding", helpers);
            return PDFFabrikk.lagPdfFil(html);
        } catch (IOException e) {
            throw new ApplicationException("Kunne ikke lage markup av melding", e);
        }
    }

    private static Map<String, Helper<?>> generateHelpers() {
        HashMap<String, Helper<?>> result = new HashMap<>();

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
                return dato.toString("d. MMMM yyyy HH:mm", locale);
            }
        };

        result.put("hentMeldingHelper", hentMeldingHelper);
        result.put("formaterDato", formaterDatoHelper);

        return result;
    }

    private static Melding finnMelding(Context context) {
        if (context == null) {
            return null;
        } else if (context.model() instanceof Melding) {
            return (Melding) context.model();
        } else {
            return finnMelding(context.parent());
        }
    }

    private static class PDFMelding {
        public final String fnrBruker, meldingstype, navIdent, fritekst;
        public final DateTime opprettetDato;

        private PDFMelding(String fnrBruker, String meldingstype, String navIdent, String fritekst, DateTime opprettetDato) {
            this.fnrBruker = fnrBruker;
            this.meldingstype = lagPDFMeldingstype(meldingstype);
            this.navIdent = navIdent;
            this.fritekst = fritekst;
            this.opprettetDato = opprettetDato;
        }

        private String lagPDFMeldingstype(String meldingstype) {
            return meldingstype.substring(0, meldingstype.indexOf("_"));
        }
    }
}
