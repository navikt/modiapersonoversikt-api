package no.nav.sbl.dialogarena.utbetaling.domain.oppsummering;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.sort;
import static no.nav.sbl.dialogarena.utbetaling.domain.oppsummering.HovedYtelse.HovedYtelseComparator.NAVN;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class Oppsummering implements Serializable {

    public final double utbetalt;
    public final double trekk;
    public final double brutto;
    public final Map<String, Map<String, Double>> ytelserUtbetalt;

    public Oppsummering(double utbetalt, double trekk, double brutto, Map<String, Map<String, Double>> ytelserUtbetalt) {
        this.utbetalt = utbetalt;
        this.trekk = trekk;
        this.brutto = brutto;
        this.ytelserUtbetalt = ytelserUtbetalt;
    }

    public String getUtbetalt() {
        return getBelopString(this.utbetalt);
    }

    public String getTrekk() {
        return getBelopString(this.trekk);
    }

    public String getBrutto() {
        return getBelopString(this.brutto);
    }

    public List<HovedYtelse> getHovedYtelsesBeskrivelser() {
        List<HovedYtelse> hovedYtelsesBeskrivelser = new ArrayList<>();
        for (String hovedytelse : ytelserUtbetalt.keySet()) {
            hovedYtelsesBeskrivelser.add(new HovedYtelse(hovedytelse, ytelserUtbetalt.get(hovedytelse)));
        }
        sort(hovedYtelsesBeskrivelser, NAVN);
        return hovedYtelsesBeskrivelser;
    }

}
