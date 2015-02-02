package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;


import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype;
import no.nav.sbl.dialogarena.pdf.HandleBarHtmlGenerator;
import no.nav.sbl.dialogarena.pdf.PDFFabrikk;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.*;

import static java.util.Arrays.asList;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SVAR_SBL_INNGAAENDE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.PdfUtils.MeldingsTypeMapping.SAMTALEREFERAT;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class PdfUtils {

    public static byte[] genererPdf(Melding melding) {
        Map<String, Helper<?>> helpers = generateHelpers();
        try {
            PDFMelding innhold = new PDFMelding(melding);
            String html = HandleBarHtmlGenerator.fyllHtmlMalMedInnhold(innhold, "html/melding", helpers);
            return PDFFabrikk.lagPdfFil(html);
        } catch (IOException e) {
            throw new ApplicationException("Kunne ikke lage markup av melding", e);
        }
    }

    public static byte[] genererPdfForPrint(List<MeldingVM> meldinger) {
        Map<String, Helper<?>> helpers = generateHelpers();
        List<PDFMelding> pdfMeldinger = new ArrayList<>();
        try {
            for (MeldingVM melding : meldinger) {
                pdfMeldinger.add(new PDFMelding(melding.melding));
            }
            PdfMeldingerWrapper innhold = new PdfMeldingerWrapper(pdfMeldinger);

            String html = HandleBarHtmlGenerator.fyllHtmlMalMedInnhold(innhold, "html/print", helpers);
            return PDFFabrikk.lagPdfFil(html);
        } catch (IOException e) {
            throw new ApplicationException("Kunne ikke lage markup av melding for print", e);
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

    protected static final class PDFMelding {
        public final String fnrBruker, meldingstype, typeBeskrivelse, temagruppeBeskrivelse, avBruker, fritekst, journalfortTema, kontorsperretEnhet, markertSomFeilsendtAv;
        public final DateTime opprettetDato, journalFortDato;


        public PDFMelding(Melding melding) {
            this.fnrBruker = melding.fnrBruker;
            this.meldingstype = lagPDFMeldingstype(melding);
            this.avBruker = erMeldingInngaaende(melding.meldingstype) ? melding.fnrBruker : melding.navIdent;
            this.typeBeskrivelse = lagTypeBeskrivelse(melding);
            this.temagruppeBeskrivelse = lagTemagruppeBeskrivelse(melding.temagruppe);
            this.fritekst = melding.fritekst;
            this.opprettetDato = melding.opprettetDato;
            this.journalFortDato = melding.journalfortDato;
            this.journalfortTema = melding.journalfortTema;
            this.kontorsperretEnhet = melding.kontorsperretEnhet;
            this.markertSomFeilsendtAv = melding.markertSomFeilsendtAv;
        }

        private String lagPDFMeldingstype(Melding melding) {
            return getMeldingsTypeMapping(melding).beskrivendeNavn;
        }

        private boolean erMeldingInngaaende(Meldingstype meldingstype) {
            return asList(SPORSMAL_SKRIFTLIG, SVAR_SBL_INNGAAENDE).contains(meldingstype);
        }

        private String lagTypeBeskrivelse(Melding melding) {
            MeldingsTypeMapping typeMapping = getMeldingsTypeMapping(melding);

            if (typeMapping == SAMTALEREFERAT) {
                return "Type: samtalereferat (N)";
            }
            if (erMeldingInngaaende(melding.meldingstype)) {
                return "Type: innkommende henvendelse (I)";
            } else {
                return "Type: utgående henvendelse (U)";
            }
        }

        private String lagTemagruppeBeskrivelse(String temagruppe) {
            if (isBlank(temagruppe)) {
                return "";
            }
            return "Temagruppe: " + TemagruppeMapping.valueOf(temagruppe).beskrivendeNavn;
        }
    }

    private static MeldingsTypeMapping getMeldingsTypeMapping(Melding melding) {
        String meldingstype = melding.meldingstype.name();
        String type = meldingstype.substring(0, meldingstype.indexOf('_'));
        return MeldingsTypeMapping.valueOf(type);
    }

    static class PdfMeldingerWrapper {

        public List<PDFMelding> pdfMeldinger;

        PdfMeldingerWrapper(List<PDFMelding> pdfMeldinger) {
            this.pdfMeldinger = pdfMeldinger;
        }
    }

    static enum TemagruppeMapping {
        ARBD("Arbeid"),
        FMLI("Familie"),
        HJLPM("Hjelpemidler"),
        BIL("Hjelpemidler Bil"),
        ORT_HJE("Helsetjenester og ortopediske hjelpemidler"),
        OVRG("Øvrig");

        public final String beskrivendeNavn;

        TemagruppeMapping(String beskrivendeNavn) {
            this.beskrivendeNavn = beskrivendeNavn;
        }
    }

    static enum MeldingsTypeMapping {
        SAMTALEREFERAT("Samtalereferat"),
        SPORSMAL("Spørsmål"),
        SVAR("Svar");

        public final String beskrivendeNavn;

        MeldingsTypeMapping(String beskrivendeNavn) {
            this.beskrivendeNavn = beskrivendeNavn;
        }
    }
}
