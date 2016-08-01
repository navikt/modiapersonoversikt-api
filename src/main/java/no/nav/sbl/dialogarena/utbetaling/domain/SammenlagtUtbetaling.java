package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class SammenlagtUtbetaling implements Serializable {

    private String id;

    private DateTime utbetalingsdato;
    private Double nettoUtbetalt;
    private List<Hovedytelse> hovedytelser;
    private boolean skalVises;
    private List<Hovedytelse> synligeHovedytelser;

    public SammenlagtUtbetaling() {
        this.skalVises = true;
    }

    public void skalViseSammenlagtUtbetaling(boolean skalVises) {
        this.skalVises = skalVises;
    }

    public DateTime getUtbetalingsdato() {
        return utbetalingsdato;
    }

    public Double getNettoUtbetalt() {
        return nettoUtbetalt;
    }

    public List<Hovedytelse> getHovedytelser() {
        return hovedytelser;
    }

    public List<Hovedytelse> getSynligeHovedytelser() {
        return synligeHovedytelser;
    }

    public SammenlagtUtbetaling withUtbetaltSum(double nettoUtbetalt) {
        this.nettoUtbetalt = nettoUtbetalt;
        return this;
    }

    public SammenlagtUtbetaling withUtbetalingsdato(DateTime utbetalingsdato) {
        this.utbetalingsdato = utbetalingsdato;
        return this;
    }

    public SammenlagtUtbetaling withHovedytelser(List<Hovedytelse> hovedytelser) {
        this.hovedytelser = hovedytelser;
        this.synligeHovedytelser = hovedytelser;
        return this;
    }

    public SammenlagtUtbetaling withId(String id) {
        this.id = id;
        return this;
    }

    public List<Hovedytelse> finnSynligeHovedytelser(FilterParametere filterParametere) {
        synligeHovedytelser = hovedytelser.stream()
                .filter(hovedytelse -> filterParametere.test(hovedytelse))
                .collect(toList());

        return synligeHovedytelser;
    }

    public boolean skalViseSammenlagtUtbetaling() {
        return skalVises && synligeHovedytelser.size() != 0 && hovedytelser.size() != 1;
    }
}
