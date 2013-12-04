package no.nav.sbl.dialogarena.utbetaling.domain;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class Oppsummering implements Serializable {

    public double utbetalt;
    public double trekk;
    public double brutto;
    public String valuta = "kr";
    public Map<String, Map<String, Double>> ytelserUtbetalt;

    private List<HovedBeskrivelse> hovedYtelsesBeskrivelser;

    public String getUtbetalt() {
        return getBelopString(this.utbetalt, this.valuta);
    }

    public String getTrekk() {
        return getBelopString(this.trekk, this.valuta);
    }

    public String getBrutto() {
        return getBelopString(this.brutto, this.valuta);
    }

    public List<HovedBeskrivelse> getHovedYtelsesBeskrivelser() {
        if(hovedYtelsesBeskrivelser == null) {
            hovedYtelsesBeskrivelser = new ArrayList<>();
            for (Map.Entry<String, Map<String, Double>> entry : ytelserUtbetalt.entrySet()) {
                hovedYtelsesBeskrivelser.add(new Oppsummering.HovedBeskrivelse(entry, this.valuta));
            }
        }
        return this.hovedYtelsesBeskrivelser;
    }

    public class HovedBeskrivelse implements Serializable {
        private String hovedYtelsesBeskrivelse;
        private List<UnderBeskrivelse> underYtelsesBeskrivelser;

        HovedBeskrivelse(Map.Entry<String, Map<String, Double>> ytelseUtbetalt, String valuta) {
            hovedYtelsesBeskrivelse = ytelseUtbetalt.getKey();
            underYtelsesBeskrivelser = new ArrayList<>();
            for (Map.Entry<String, Double> indreEntry : ytelseUtbetalt.getValue().entrySet()) {
                underYtelsesBeskrivelser.add(new UnderBeskrivelse(indreEntry, valuta));
            }
        }

        public String getHovedYtelsesBeskrivelse() {
            return hovedYtelsesBeskrivelse;
        }

        public List<UnderBeskrivelse> getUnderYtelsesBeskrivelser() {
            return underYtelsesBeskrivelser;
        }
    }

    public class UnderBeskrivelse implements  Serializable {
        private String underYtelsesBeskrivelse;
        private Double ytelsesBelop;
        private String valuta;

        UnderBeskrivelse(Map.Entry<String, Double> indreEntry, String valuta) {
            this.valuta = valuta;
            underYtelsesBeskrivelse = indreEntry.getKey();
            ytelsesBelop = indreEntry.getValue();
        }
        public String getYtelsesBelop() {
            return getBelopString(ytelsesBelop, this.valuta);
        }

        public String getUnderYtelsesBeskrivelse() {
            return underYtelsesBeskrivelse;
        }
    }
}
