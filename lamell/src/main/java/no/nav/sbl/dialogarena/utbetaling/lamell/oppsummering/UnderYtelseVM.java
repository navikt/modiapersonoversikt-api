package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;

import java.io.Serializable;
import java.util.Comparator;

import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class UnderytelseVM implements Serializable {

    public static class UnderYtelseComparator {
        public static final Comparator<UnderytelseVM> NAVN = new Comparator<UnderytelseVM>() {
            @Override
            public int compare(UnderytelseVM o1, UnderytelseVM o2) {
                return o1.getUnderYtelsesBeskrivelse().compareTo(o2.getUnderYtelsesBeskrivelse());
            }
        };
    }

    private String underYtelsesBeskrivelse;
    private Double ytelsesBelop;
    private boolean trekk = false;

    UnderytelseVM(String beskrivelse, Double belop) {
        underYtelsesBeskrivelse = beskrivelse;
        ytelsesBelop = belop;
        trekk = ytelsesBelop != null && ytelsesBelop < 0;
    }

    public String getTrekkBelop() {
        return trekk ? getBelopString(ytelsesBelop) : "";
    }

    public String getYtelsesBelop() {
        return trekk ? "" : getBelopString(ytelsesBelop);
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