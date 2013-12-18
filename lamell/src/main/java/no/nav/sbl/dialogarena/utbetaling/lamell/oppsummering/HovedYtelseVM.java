package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;

import org.apache.commons.collections15.Transformer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.sort;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class HovedYtelseVM implements Serializable {

    private String hovedYtelsesBeskrivelse;
    private List<UnderYtelseVM> underYtelsesBeskrivelser;

    public static final Transformer<HovedYtelseVM, String> NAVN = new Transformer<HovedYtelseVM, String>() {
        @Override
        public String transform(HovedYtelseVM hovedYtelseVM) {
            return hovedYtelseVM.hovedYtelsesBeskrivelse;
        }
    };

    public HovedYtelseVM(String beskrivelse, Map<String, Double> underytelser) {
        hovedYtelsesBeskrivelse = beskrivelse;
        underYtelsesBeskrivelser = new ArrayList<>();
        for (Map.Entry<String, Double> underytelse : underytelser.entrySet()) {
            underYtelsesBeskrivelser.add(new UnderYtelseVM(underytelse.getKey(), underytelse.getValue()));
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