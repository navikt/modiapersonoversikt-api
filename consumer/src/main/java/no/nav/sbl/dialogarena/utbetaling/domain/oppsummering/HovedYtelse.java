package no.nav.sbl.dialogarena.utbetaling.domain.oppsummering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.Collections.sort;
import static no.nav.sbl.dialogarena.utbetaling.domain.oppsummering.UnderYtelse.UnderYtelseComparator.NAVN;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class HovedYtelse implements Serializable {

    public static class HovedYtelseComparator {
        public static final Comparator<HovedYtelse> NAVN = new Comparator<HovedYtelse>() {
            @Override
            public int compare(HovedYtelse o1, HovedYtelse o2) {
                return o1.getHovedYtelsesBeskrivelse().compareTo(o2.getHovedYtelsesBeskrivelse());
            }
        };
    }

    private String hovedYtelsesBeskrivelse;
    private List<UnderYtelse> underYtelsesBeskrivelser;

    public HovedYtelse(String beskrivelse, Map<String, Double> underytelser) {
        hovedYtelsesBeskrivelse = beskrivelse;
        underYtelsesBeskrivelser = new ArrayList<>();
        for (String underytelse : underytelser.keySet()) {
            underYtelsesBeskrivelser.add(new UnderYtelse(underytelse, underytelser.get(underytelse)));
        }
        sort(underYtelsesBeskrivelser, NAVN);
    }

    public String getHovedYtelsesBeskrivelse() {
        return hovedYtelsesBeskrivelse;
    }

    public List<UnderYtelse> getUnderYtelsesBeskrivelser() {
        return underYtelsesBeskrivelser;
    }

    public String getBruttoUnderytelser(){
        return getBelopString(lagBrutto());
    }

    public String getTrekkUnderytelser(){
        return getBelopString(lagTrekk());
    }

    public String getNettoUnderytelser(){
        return getBelopString(lagBrutto() + lagTrekk());
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
}