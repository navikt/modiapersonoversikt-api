package no.nav.sbl.dialogarena.utbetaling.domain.oppsummering;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;

import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class UnderYtelse implements Serializable {

    public static class UnderYtelseComparator {
        public static final Comparator<UnderYtelse> NAVN = new Comparator<UnderYtelse>() {
            @Override
            public int compare(UnderYtelse o1, UnderYtelse o2) {
                return o1.getUnderYtelsesBeskrivelse().compareTo(o2.getUnderYtelsesBeskrivelse());
            }
        };
    }

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

    public String getTrekkBelop() {
        return trekk ? getBelopString(ytelsesBelop, this.valuta) : "";
    }

    public String getYtelsesBelop() {
        return trekk ? "" : getBelopString(ytelsesBelop, this.valuta);
    }

    public String getUnderYtelsesBeskrivelse() {
        return underYtelsesBeskrivelse;
    }

    public boolean isTrekk() {
        return trekk;
    }

    Double getBelop() {
        return ytelsesBelop;
    }
}