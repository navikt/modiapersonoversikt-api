package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.virksomhet.okonomi.utbetaling.v2.WSPosteringsdetaljer;
import org.apache.commons.collections15.Transformer;

import java.io.Serializable;

public class PosteringsDetalj implements Serializable {

    private String hovedBeskrivelse;
    private String underBeskrivelse;
    private String kontoNr;

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

    PosteringsDetalj(String hovedBeskrivelse, String underBeskrivelse, String kontoNr) {
        this.hovedBeskrivelse = hovedBeskrivelse;
        this.underBeskrivelse = underBeskrivelse;
        this.kontoNr = kontoNr;
    }

    public PosteringsDetalj(WSPosteringsdetaljer wsPosteringsdetaljer) {
        this.hovedBeskrivelse = wsPosteringsdetaljer.getKontoBeskrHoved();
        this.underBeskrivelse = wsPosteringsdetaljer.getKontoBeskrUnder();
        this.kontoNr = wsPosteringsdetaljer.getKontonr();
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
