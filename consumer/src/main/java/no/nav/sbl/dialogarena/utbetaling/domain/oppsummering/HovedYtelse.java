package no.nav.sbl.dialogarena.utbetaling.domain.oppsummering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class HovedYtelse implements Serializable {
    private String hovedYtelsesBeskrivelse;
    private List<UnderYtelse> underYtelsesBeskrivelser;
    private String valuta;


    HovedYtelse(Map.Entry<String, Map<String, Double>> ytelseUtbetalt, String valuta) {
        this.valuta = valuta;
        hovedYtelsesBeskrivelse = ytelseUtbetalt.getKey();
        underYtelsesBeskrivelser = new ArrayList<>();
        for (Map.Entry<String, Double> indreEntry : ytelseUtbetalt.getValue().entrySet()) {
            underYtelsesBeskrivelser.add(new UnderYtelse(indreEntry, valuta));
        }
        Collections.sort(underYtelsesBeskrivelser, UnderYtelse.UnderYtelseComparator.NAVN);
    }

    public String getHovedYtelsesBeskrivelse() {
        return hovedYtelsesBeskrivelse;
    }

    public List<UnderYtelse> getUnderYtelsesBeskrivelser() {
        return underYtelsesBeskrivelser;
    }

    public String getBruttoUnderytelser(){
        return getBelopString(lagBrutto(), valuta);
    }

    public String getTrekkUnderytelser(){
        return getBelopString(lagTrekk(), valuta);
    }

    public String getNettoUnderytelser(){
        return getBelopString(lagBrutto() + lagTrekk(), valuta);
    }

    private Double lagBrutto() {
        Double sum = 0.0;
        for (UnderYtelse ytelse : underYtelsesBeskrivelser) {
            if(ytelse.isTrekk()) { continue; }
            sum += ytelse.getBelop();
        }
        return sum;
    }

    private Double lagTrekk() {
        Double sum = 0.0;
        for (UnderYtelse ytelse : underYtelsesBeskrivelser) {
            if(!ytelse.isTrekk()) { continue; }
            sum += ytelse.getBelop();
        }
        return sum;
    }

    public static class HovedYtelseComparator {
        public static Comparator<HovedYtelse> NAVN = new Comparator<HovedYtelse>() {
            @Override
            public int compare(HovedYtelse o1, HovedYtelse o2) {
                return o1.getHovedYtelsesBeskrivelse().compareTo(o2.getHovedYtelsesBeskrivelse());
            }
        };
    }
}