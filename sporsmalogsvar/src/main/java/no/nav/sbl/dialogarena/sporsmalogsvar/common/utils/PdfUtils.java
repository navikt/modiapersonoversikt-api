package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;


import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Helper;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;
import no.nav.sbl.dialogarena.pdf.HandleBarHtmlGenerator;
import no.nav.sbl.dialogarena.pdf.PDFFabrikk;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableMap;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.*;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.PdfUtils.MeldingsTypeMapping.SAMTALEREFERAT;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class PdfUtils {

    public static final Map<Temagruppe, String> TEMAGRUPPE_MAP;

    static {
        HashMap<Temagruppe, String> map = new HashMap<>();
        map.put(ARBD, "Arbeid");
        map.put(FMLI, "Familie");
        map.put(PLEIEPENGERSY, "Pleiepenger sykt barn");
        map.put(HJLPM, "Hjelpemidler");
        map.put(BIL, "Hjelpemidler Bil");
        map.put(UTLAND, "Utland");
        map.put(ORT_HJE, "Helsetjenester og ortopediske hjelpemidler");
        map.put(OVRG, "Øvrig");
        map.put(PENS, "Pensjon");
        map.put(UFRT, "Uføretrygd");
        map.put(OKSOS, "Økonomisk sosialhjelp");
        map.put(ANSOS, "Andre sosiale tjenester");
        TEMAGRUPPE_MAP = unmodifiableMap(map);
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

        Helper hentMeldingHelper = (Helper<Melding>) (o, options) -> finnMelding(options.context).navIdent;

        Helper formaterDatoHelper = (dato, options) -> {
            Locale locale = new Locale("nb", "no");
            return ((DateTime) dato).toString("d. MMMM yyyy HH:mm", locale);
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
            this.fritekst = escapeHtml(melding.getFritekst());
            this.opprettetDato = melding.opprettetDato;
            this.journalFortDato = melding.journalfortDato;
            this.journalfortTema = melding.journalfortTema;
            this.kontorsperretEnhet = melding.kontorsperretEnhet;
            this.markertSomFeilsendtAv = melding.markertSomFeilsendtAvNavIdent;
        }

        private String lagPDFMeldingstype(Melding melding) {
            return getMeldingsTypeMapping(melding).beskrivendeNavn;
        }

        private boolean erMeldingInngaaende(Meldingstype meldingstype) {
            return asList(SPORSMAL_SKRIFTLIG, SPORSMAL_SKRIFTLIG_DIREKTE, SVAR_SBL_INNGAAENDE).contains(meldingstype);
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
            return "Temagruppe: " + TEMAGRUPPE_MAP.get(Temagruppe.valueOf(temagruppe));
        }
    }

    private static String escapeHtml(String s) {
        return s == null ? null : s.replaceAll("&", "&amp;").replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("\"", "&quot;").replaceAll("'", "&apos;");
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

    enum MeldingsTypeMapping {
        SAMTALEREFERAT("Samtalereferat"),
        SPORSMAL("Spørsmål"),
        DELVIS("Delsvar"),
        SVAR("Svar");

        public final String beskrivendeNavn;

        MeldingsTypeMapping(String beskrivendeNavn) {
            this.beskrivendeNavn = beskrivendeNavn;
        }
    }
}
