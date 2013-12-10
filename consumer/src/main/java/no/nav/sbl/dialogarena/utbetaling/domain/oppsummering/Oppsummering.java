package no.nav.sbl.dialogarena.utbetaling.domain.oppsummering;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.sort;
import static no.nav.sbl.dialogarena.utbetaling.domain.oppsummering.HovedYtelse.HovedYtelseComparator.NAVN;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class Oppsummering implements Serializable {

    public double utbetalt;
    public double trekk;
    public double brutto;
    public String valuta = "kr";
    public Map<String, Map<String, Double>> ytelserUtbetalt;
    private List<HovedYtelse> hovedYtelsesBeskrivelser;

    public String getUtbetalt() {
        return getBelopString(this.utbetalt, this.valuta);
    }

    public String getTrekk() {
        return getBelopString(this.trekk, this.valuta);
    }

    public String getBrutto() {
        return getBelopString(this.brutto, this.valuta);
    }

    public List<HovedYtelse> getHovedYtelsesBeskrivelser() {
        if (hovedYtelsesBeskrivelser == null) {
            hovedYtelsesBeskrivelser = new ArrayList<>();
            for (Map.Entry<String, Map<String, Double>> entry : ytelserUtbetalt.entrySet()) {
                hovedYtelsesBeskrivelser.add(new HovedYtelse(entry, this.valuta));
            }
            sort(hovedYtelsesBeskrivelser, NAVN);
        }
        return this.hovedYtelsesBeskrivelser;
    }

}
