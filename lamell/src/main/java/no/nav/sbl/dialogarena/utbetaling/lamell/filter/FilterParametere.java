package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.apache.commons.collections15.Predicate;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.ARBEIDSGIVER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.BRUKER;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.hentYtelser;


public class FilterParametere implements Serializable, Predicate<Utbetaling> {

    public static final String ENDRET = "filterParametere.endret";
    public static final String FEIL = "filter.feil";
    public static final String HOVEDYTELSER_ENDRET = "hovedytelser.endret";

    private LocalDate startDato;
    private LocalDate sluttDato;

    private Boolean visBruker;
    private Boolean visArbeidsgiver;

    public Set<String> alleYtelser;
    public Set<String> uonskedeYtelser;

    public FilterParametere(LocalDate startDato, LocalDate sluttDato, Boolean visBruker, Boolean visArbeidsgiver, Set<String> hovedYtelser) {
        this.startDato = startDato;
        this.sluttDato = sluttDato;

        this.visBruker = visBruker;
        this.visArbeidsgiver = visArbeidsgiver;

        this.alleYtelser = hovedYtelser;
        this.uonskedeYtelser = new HashSet<>();
    }

    private void oppdaterValgteYtelser(Set<String> hovedYtelser) {
        alleYtelser = hovedYtelser;
    }

    public Boolean getVisArbeidsgiver() {
        return visArbeidsgiver;
    }

    public Boolean getVisBruker() {
        return visBruker;
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
        oppdaterValgteYtelser(hovedYtelser);
    }

    @Override
    public boolean evaluate(Utbetaling utbetaling) {
        boolean innenforDatoer = filtrerPaaDatoer(utbetaling.getUtbetalingsDato().toLocalDate());
        boolean brukerSkalVises = filtrerPaaMottaker(utbetaling.mottakertype);
        boolean harYtelse = filtrerPaaYtelser(utbetaling);
        return innenforDatoer && brukerSkalVises && harYtelse;
    }

    private boolean filtrerPaaDatoer(LocalDate utbetalingsDato) {
        return utbetalingsDato.isAfter(startDato.minusDays(1)) && utbetalingsDato.isBefore(sluttDato.plusDays(1));
    }

    private boolean filtrerPaaMottaker(String mottakerkode) {
        boolean arbeidsgiverVises = visArbeidsgiver && ARBEIDSGIVER.equalsIgnoreCase(mottakerkode);
        boolean brukerVises = visBruker && BRUKER.equalsIgnoreCase(mottakerkode);
        return arbeidsgiverVises || brukerVises;
    }

    private boolean filtrerPaaYtelser(Utbetaling utbetaling) {
        for (String ytelse : hentYtelser(utbetaling)) {
            if (!uonskedeYtelser.contains(ytelse)) {
                return true;
            }
        }
        return false;
    }

}
