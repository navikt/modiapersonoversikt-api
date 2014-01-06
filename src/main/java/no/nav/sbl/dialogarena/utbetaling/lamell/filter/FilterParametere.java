package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.apache.commons.collections15.Predicate;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class FilterParametere implements Serializable, Predicate<Utbetaling> {

    public static final String FILTER_ENDRET = "filterParametere.endret";
    public static final String HOVEDYTELSER_ENDRET = "hovedytelser.endret";

    private LocalDate startDato;
    private LocalDate sluttDato;

    private Map<String, Boolean> mottakere;

    public Set<String> alleYtelser;
    public Set<String> uonskedeYtelser;

    public FilterParametere(LocalDate startDato, LocalDate sluttDato, Map<String, Boolean> mottakere, Set<String> hovedYtelser) {
        this.startDato = startDato;
        this.sluttDato = sluttDato;

        this.mottakere = mottakere;

        this.alleYtelser = hovedYtelser;
        this.uonskedeYtelser = new HashSet<>();
    }

    public LocalDate getSluttDato() {
        return sluttDato;
    }

    public void setSluttDato(LocalDate sluttDato) {
        if (sluttDato != null) {
            this.sluttDato = sluttDato;
        }
    }

    public LocalDate getStartDato() {
        return startDato;
    }

    public void setStartDato(LocalDate startDato) {
        if (startDato != null) {
            this.startDato = startDato;
        }
    }

    public void setYtelser(Set<String> hovedYtelser) {
        alleYtelser = hovedYtelser;
    }

    public void toggleMottaker(String mottaker) {
        mottakere.put(mottaker, !viseMottaker(mottaker));
    }

    public boolean viseMottaker(String mottakerkode) {
        if (mottakere.containsKey(mottakerkode)) {
            return mottakere.get(mottakerkode);
        }
        return false;
    }

    @Override
    public boolean evaluate(Utbetaling utbetaling) {
        boolean innenforDatoer = filtrerPaaDatoer(utbetaling.getUtbetalingsdato().toLocalDate());
        boolean mottakerSkalVises = viseMottaker(utbetaling.getMottakerkode());
        boolean harYtelse = filtrerPaaYtelser(utbetaling);
        return innenforDatoer
                && mottakerSkalVises
                && harYtelse;
    }

    private boolean filtrerPaaDatoer(LocalDate utbetalingsDato) {
        return utbetalingsDato.isAfter(startDato.minusDays(1)) && utbetalingsDato.isBefore(sluttDato.plusDays(1));
    }

    private boolean filtrerPaaYtelser(Utbetaling utbetaling) {
        return !uonskedeYtelser.contains(utbetaling.getHovedytelse());
    }

}
