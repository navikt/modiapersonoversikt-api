package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.Collections.sort;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class HovedYtelseVM implements Serializable {

    public static class HovedYtelseComparator {
        public static final Comparator<HovedYtelseVM> NAVN = new Comparator<HovedYtelseVM>() {
            @Override
            public int compare(HovedYtelseVM o1, HovedYtelseVM o2) {
                return o1.getHovedYtelsesBeskrivelse().compareTo(o2.getHovedYtelsesBeskrivelse());
            }
        };
    }

    private String hovedYtelsesBeskrivelse;
    private List<UnderYtelseVM> underYtelsesBeskrivelser;

    public HovedYtelseVM(String beskrivelse, Map<String, Double> underytelser) {
        hovedYtelsesBeskrivelse = beskrivelse;
        underYtelsesBeskrivelser = new ArrayList<>();
        for (String underytelse : underytelser.keySet()) {
            underYtelsesBeskrivelser.add(new UnderYtelseVM(underytelse, underytelser.get(underytelse)));
        }
        sort(underYtelsesBeskrivelser, UnderYtelseVM.UnderYtelseComparator.NAVN);
    }

    public String getHovedYtelsesBeskrivelse() {
        return hovedYtelsesBeskrivelse;
    }

    public List<UnderYtelseVM> getUnderYtelsesBeskrivelser() {
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
        for (UnderYtelseVM ytelse : underYtelsesBeskrivelser) {
            if(ytelse.isTrekk()) { continue; }
            sum += ytelse.getBelop();
        }
        return sum;
    }

    private Double lagTrekk() {
        Double sum = 0.0;
        for (UnderYtelseVM ytelse : underYtelsesBeskrivelser) {
            if(!ytelse.isTrekk()) { continue; }
            sum += ytelse.getBelop();
        }
        return sum;
    }
}