package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.virksomhet.okonomi.utbetaling.v2.WSPosteringsdetaljer;
import org.apache.commons.collections15.Transformer;

import java.io.Serializable;

public class PosteringsDetalj implements Serializable {

    private String hovedBeskrivelse;
    private String underBeskrivelse = "-";
    private String kontoNr;
    private Double sats;
    private Integer antall;
    private Double belop;

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
        this.underBeskrivelse = underBeskrivelse;
        this.kontoNr = kontoNr;
        this.sats = sats;
        this.antall = antall;
        this.belop = belop;
    }

    public PosteringsDetalj(WSPosteringsdetaljer wsPosteringsdetaljer) {
        this.hovedBeskrivelse = wsPosteringsdetaljer.getKontoBeskrHoved();
        this.underBeskrivelse = wsPosteringsdetaljer.getKontoBeskrUnder();
        this.kontoNr = wsPosteringsdetaljer.getKontonr();
        this.sats = wsPosteringsdetaljer.getSats();
        this.antall = wsPosteringsdetaljer.getAntall();
        this.belop = wsPosteringsdetaljer.getBelop();
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

    public String getUnderBeskrivelse() {
        return underBeskrivelse;
    }

    public String getKontoNr() {
        return kontoNr;
    }
}
