package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;

import java.io.Serializable;

import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class UnderYtelseVM implements Serializable {

    private String underYtelsesBeskrivelse;
    private Double ytelsesBelop;
    private boolean trekk = false;

    public UnderYtelseVM(String beskrivelse, Double belop) {
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