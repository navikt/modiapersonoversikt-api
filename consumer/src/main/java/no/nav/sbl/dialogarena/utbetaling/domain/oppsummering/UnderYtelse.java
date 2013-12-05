package no.nav.sbl.dialogarena.utbetaling.domain.oppsummering;

import java.io.Serializable;
import java.util.Map;

import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class UnderYtelse implements Serializable {
    private String underYtelsesBeskrivelse;
    private Double ytelsesBelop;
    private String valuta;

    UnderYtelse(Map.Entry<String, Double> indreEntry, String valuta) {
        this.valuta = valuta;
        underYtelsesBeskrivelse = indreEntry.getKey();
        ytelsesBelop = indreEntry.getValue();
    }

    public String getYtelsesBelop() {
        return getBelopString(ytelsesBelop, this.valuta);
    }

    public String getUnderYtelsesBeskrivelse() {
        return underYtelsesBeskrivelse;
    }
}