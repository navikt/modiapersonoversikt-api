package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;

import java.io.Serializable;
import java.util.Comparator;

import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class UnderYtelseVM implements Serializable {

    public static class UnderYtelseComparator {
        public static final Comparator<UnderYtelseVM> NAVN = new Comparator<UnderYtelseVM>() {
            @Override
            public int compare(UnderYtelseVM o1, UnderYtelseVM o2) {
                return o1.getUnderYtelsesBeskrivelse().compareTo(o2.getUnderYtelsesBeskrivelse());
            }
        };
        /**
         * Sorterer basert på beløpet, slik at negative tall havner nedenfor de positive
         */
        public static final Comparator<UnderYtelseVM> BELOP_SORT = new Comparator<UnderYtelseVM>() {
            @Override
            public int compare(UnderYtelseVM o1, UnderYtelseVM o2) {
                return Double.compare(o2.getBelop(), o1.getBelop());
            }
        };

        /**
         * Sorterer underytelser med tittel som inneholder "skatt", og er negativt, nederst.
         */
        public static final Comparator<UnderYtelseVM> SKATT_NEDERST_SORT = new Comparator<UnderYtelseVM>(){

            @Override
            public int compare(UnderYtelseVM o1, UnderYtelseVM o2) {
                if(o1.getUnderYtelsesBeskrivelse().toLowerCase().contains("skatt") && o1.getBelop() <= 0) {
                    return 1;
                } else if(o2.getUnderYtelsesBeskrivelse().toLowerCase().contains("skatt") && o2.getBelop() <= 0) {
                    return -1;
                }
                return 0;
            }
        };
    }

    private String underYtelsesBeskrivelse;
    private Double ytelsesBelop;
    private boolean trekk = false;

    UnderYtelseVM(String beskrivelse, Double belop) {
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