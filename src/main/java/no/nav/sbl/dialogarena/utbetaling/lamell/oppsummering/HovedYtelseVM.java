package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;

import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static no.nav.sbl.dialogarena.time.Datoformat.kortUtenLiteral;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class HovedYtelseVM implements Serializable {

    private final String hovedYtelsesBeskrivelse;
    private final Double brutto;
    private final Double trekk;
    private final Double utbetalt;
    private final List<UnderYtelseVM> underYtelsesBeskrivelser;
    private final DateTime startPeriode;
    private final DateTime sluttPeriode;

    public HovedYtelseVM(String beskrivelse, List<Underytelse> underytelser, Double brutto, Double trekk, Double utbetalt, DateTime startPeriode, DateTime sluttPeriode) {
        hovedYtelsesBeskrivelse = beskrivelse;
        this.brutto = brutto;
        this.trekk = trekk;
        this.utbetalt = utbetalt;
        this.startPeriode = startPeriode;
        this.sluttPeriode = sluttPeriode;
        underYtelsesBeskrivelser = new ArrayList<>();
        for (Underytelse underytelse : underytelser) {
            underYtelsesBeskrivelser.add(new UnderYtelseVM(underytelse.getTittel(), underytelse.getBelop()));
        }
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

    public DateTime getStartPeriode() {
        return startPeriode;
    }

    public DateTime getSluttPeriode() {
        return sluttPeriode;
    }

    public String getHovedYtelsePeriode() {
       return kortUtenLiteral(startPeriode) + " - " + kortUtenLiteral(sluttPeriode);
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