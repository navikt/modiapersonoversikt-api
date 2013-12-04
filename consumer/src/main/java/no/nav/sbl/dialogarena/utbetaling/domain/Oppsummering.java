package no.nav.sbl.dialogarena.utbetaling.domain;


import java.io.Serializable;
import java.util.Map;

import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class Oppsummering implements Serializable {

    public double utbetalt;
    public double trekk;
    public double brutto;
    public String valuta = "kr";
    public Map<String, Map<String, Double>> ytelserUtbetalt;


    public String getUtbetalt() {
        return getBelopString(this.utbetalt, this.valuta);
    }

    public String getTrekk() {
        return getBelopString(this.trekk, this.valuta);
    }

    public String getBrutto() {
        return getBelopString(this.brutto, this.valuta);
    }
}
