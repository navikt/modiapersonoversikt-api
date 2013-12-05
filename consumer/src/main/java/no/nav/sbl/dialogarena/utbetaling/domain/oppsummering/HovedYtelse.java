package no.nav.sbl.dialogarena.utbetaling.domain.oppsummering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HovedYtelse implements Serializable {
    private String hovedYtelsesBeskrivelse;
    private List<UnderYtelse> underYtelsesBeskrivelser;

    HovedYtelse(Map.Entry<String, Map<String, Double>> ytelseUtbetalt, String valuta) {
        hovedYtelsesBeskrivelse = ytelseUtbetalt.getKey();
        underYtelsesBeskrivelser = new ArrayList<>();
        for (Map.Entry<String, Double> indreEntry : ytelseUtbetalt.getValue().entrySet()) {
            underYtelsesBeskrivelser.add(new UnderYtelse(indreEntry, valuta));
        }
    }

    public String getHovedYtelsesBeskrivelse() {
        return hovedYtelsesBeskrivelse;
    }

    public List<UnderYtelse> getUnderYtelsesBeskrivelser() {
        return underYtelsesBeskrivelser;
    }
}