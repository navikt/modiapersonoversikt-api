package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning;

import java.io.Serializable;
import java.util.Comparator;

import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class YtelseVM implements Serializable {

    private final String ytelse;
    private final Double sats;
    private final Double antall;
    private final Double belop;
    private final String satsType;

    public YtelseVM(String ytelse, Double belop) {
        this(ytelse, belop, null, null, null);
    }

    public YtelseVM(String ytelse, Double belop, Double antall, Double sats, String satsType) {
        this.ytelse = ytelse;
        this.sats = sats;
        this.antall = antall;
        this.belop = belop;
        this.satsType = satsType;
    }

    public String getYtelse() {
        return ytelse;
    }

    public String getSats() {
        return sats != null ? conditionalPercentage(getBelopString(sats, 2)) : "";
    }

    public String getAntall() {
        return antall != null ? getBelopString(antall, 1) : "";
    }

    public String getBelop() {
        return belop != null ? getBelopString(belop, 2) : "";
    }

    private String conditionalPercentage(String number) {
        if(satsType != null && satsType.equalsIgnoreCase("prosent")) {
            return number + "%";
        }
        return number;
    }

    public static final Comparator<YtelseVM> DESC_BELOP = new Comparator<YtelseVM>() {
        @Override
        public int compare(YtelseVM o1, YtelseVM o2) {
            if(o1.belop == null) {
                return 1;
            } else if(o2.belop == null) {
                return -1;
            }
            return o2.belop.compareTo(o1.belop);
        }
    };
}
