package no.nav.sbl.dialogarena.utbetaling.domain.oppsummering;

import java.io.Serializable;
import java.util.Map;

import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class UnderYtelse implements Serializable, Comparable<UnderYtelse> {
    private String underYtelsesBeskrivelse;
    private Double ytelsesBelop;
    private String valuta;
    private boolean trekk = false;

    UnderYtelse(Map.Entry<String, Double> indreEntry, String valuta) {
        this.valuta = valuta;
        underYtelsesBeskrivelse = indreEntry.getKey();
        ytelsesBelop = indreEntry.getValue();
        trekk = ytelsesBelop != null && ytelsesBelop < 0;
    }

    @Override
    public int compareTo(UnderYtelse ytelse) {
        return underYtelsesBeskrivelse.compareTo(ytelse.underYtelsesBeskrivelse);
    }

    public String getTrekkBelop() {
        return trekk? getBelopString(ytelsesBelop, this.valuta) : "";
    }

    public String getYtelsesBelop() {
        return trekk? "" : getBelopString(ytelsesBelop, this.valuta);
    }

    public String getUnderYtelsesBeskrivelse() {
        return underYtelsesBeskrivelse;
    }

    public boolean isTrekk() {
        return trekk;
    }

    Double getBelop(){
        return ytelsesBelop;
    }
}