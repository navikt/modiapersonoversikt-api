package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning;

import java.io.Serializable;

import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class YtelseVM implements Serializable {

    private final String ytelse;
    private final String sats;
    private final String antall;
    private final String belop;

    public YtelseVM(String ytelse, Double belop) {
        this(ytelse, null, null, belop);
    }

    public YtelseVM(String ytelse, Double sats, Double antall, Double belop) {
        this.ytelse = ytelse;
        this.sats = sats != null ? String.valueOf(sats) : "";
        this.antall = antall != null ? String.valueOf(antall) : "";
        this.belop = belop != null ? getBelopString(belop) : "";
    }

    public String getYtelse() {
        return ytelse;
    }

    public String getSats() {
        return sats;
    }

    public String getAntall() {
        return antall;
    }

    public String getBelop() {
        return belop;
    }
}
