package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;

import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.sort;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class HovedYtelseVM implements Serializable {

    private final String hovedYtelsesBeskrivelse;
    private final Double brutto;
    private final Double trekk;
    private final Double utbetalt;
    private final List<UnderYtelseVM> underYtelsesBeskrivelser;

    public HovedYtelseVM(String beskrivelse, List<Underytelse> underytelser, Double brutto, Double trekk, Double utbetalt) {
        hovedYtelsesBeskrivelse = beskrivelse;
        this.brutto = brutto;
        this.trekk = trekk;
        this.utbetalt = utbetalt;
        underYtelsesBeskrivelser = new ArrayList<>();
        for (Underytelse underytelse : underytelser) {
            underYtelsesBeskrivelser.add(new UnderYtelseVM(underytelse.getTittel(), underytelse.getBelop()));
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
        return getBelopString(brutto);
    }

    public String getTrekkUnderytelser(){
        return getBelopString(trekk);
    }

    public String getNettoUnderytelser(){
        return getBelopString(utbetalt);
    }


    public static class HovedYtelseComparator {
        public static final Comparator<HovedYtelseVM> HOVEDYTELSE_NAVN = new Comparator<HovedYtelseVM>() {
            @Override
            public int compare(HovedYtelseVM o1, HovedYtelseVM o2) {
                return o1.getHovedYtelsesBeskrivelse().compareTo(o2.getHovedYtelsesBeskrivelse());
            }
        };
    }
}