package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.virksomhet.okonomi.utbetaling.v2.WSPosteringsdetaljer;
import org.apache.commons.collections15.Transformer;

import java.io.Serializable;

public class PosteringsDetalj implements Serializable {

    private static final String SKATT = "Skatt";
    private static final String UTBETALT = "Utbetalt";

    private String hovedBeskrivelse;
    private String underBeskrivelse;
    private String kontoNr;
    private Double sats;
    private Integer antall;
    private Double belop;
    private boolean skatt = false;

    public static final Transformer<PosteringsDetalj, String> POSTERINGS_DETALJ_HOVEDBESKRIVELSE_TRANSFORMER = new Transformer<PosteringsDetalj, String>() {
        @Override
        public String transform(PosteringsDetalj posteringsDetalj) {
            return posteringsDetalj.getHovedBeskrivelse();
        }
    };

    public static final Transformer<PosteringsDetalj, String> POSTERINGS_DETALJ_KONTONR_TRANSFORMER = new Transformer<PosteringsDetalj, String>() {
        @Override
        public String transform(PosteringsDetalj posteringsDetalj) {
            return posteringsDetalj.getKontoNr();
        }
    };

    PosteringsDetalj(String hovedBeskrivelse, String underBeskrivelse, String kontoNr, Double sats, Integer antall, Double belop) {
        this.hovedBeskrivelse = hovedBeskrivelse;
        this.underBeskrivelse = transformUnderBeskrivelse(underBeskrivelse, hovedBeskrivelse);
        this.kontoNr = kontoNr;
        this.sats = sats;
        this.antall = antall;
        this.belop = belop;
        this.skatt = SKATT.equalsIgnoreCase(hovedBeskrivelse);
    }

    public PosteringsDetalj(WSPosteringsdetaljer wsPosteringsdetaljer) {
        this.hovedBeskrivelse = wsPosteringsdetaljer.getKontoBeskrHoved();
        this.underBeskrivelse = transformUnderBeskrivelse(wsPosteringsdetaljer.getKontoBeskrUnder(), hovedBeskrivelse);
        this.kontoNr = wsPosteringsdetaljer.getKontonr();
        this.sats = wsPosteringsdetaljer.getSats();
        this.antall = wsPosteringsdetaljer.getAntall();
        this.belop = wsPosteringsdetaljer.getBelop();
        this.skatt = "Skatt".equalsIgnoreCase(hovedBeskrivelse);
    }
    private String transformUnderBeskrivelse(String beskrUnder, String hovedBeskrivelse) {
        String beskr = beskrUnder != null && !beskrUnder.isEmpty() ? beskrUnder : hovedBeskrivelse;
        return beskr != null && !beskr.isEmpty() ? beskr : UTBETALT;
    }

    public boolean isSkatt() {
        return skatt;
    }

    public Double getBelop() {
        return belop;
    }

    public Double getSats() {
        return sats;
    }

    public Integer getAntall() {
        return antall;
    }

    public String getHovedBeskrivelse() {
        return hovedBeskrivelse;
    }

    public void setHovedBeskrivelse(String hovedBeskrivelse) {
        this.hovedBeskrivelse = hovedBeskrivelse;
    }

    public String getUnderBeskrivelse() {
        return underBeskrivelse;
    }

    public String getKontoNr() {
        return kontoNr;
    }
}
