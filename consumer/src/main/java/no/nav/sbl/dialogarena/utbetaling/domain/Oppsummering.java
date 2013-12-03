package no.nav.sbl.dialogarena.utbetaling.domain;


import no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil;

import java.io.Serializable;
import java.util.Map;

public class Oppsummering implements Serializable {

    public double utbetalt;
    public double trekk;
    public double brutto;
    public String valuta = "kr";
    public Map<String, Double> ytelserUtbetalt;


    public Map<String, Double> getYtelserUtbetalt() {
        return ytelserUtbetalt;
    }

    public String getUtbetalt() {
        return ValutaUtil.getBelopString(this.utbetalt, this.valuta);
    }

    public String getTrekk() {
        return ValutaUtil.getBelopString(this.trekk, this.valuta);
    }

    public String getBrutto() {
        return ValutaUtil.getBelopString(this.brutto, this.valuta);
    }
}
