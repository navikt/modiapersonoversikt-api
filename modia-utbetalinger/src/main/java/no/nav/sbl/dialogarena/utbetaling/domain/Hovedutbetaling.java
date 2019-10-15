package no.nav.sbl.dialogarena.utbetaling.domain;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collector;

import static java.util.stream.Collectors.summingDouble;
import static java.util.stream.Collectors.toList;

public class Hovedutbetaling implements Serializable {

    private String id;

    private DateTime hovedytelsesdato;
    private Double nettoUtbetalt;
    private List<Hovedytelse> hovedytelser;
    private boolean skalVises;
    private List<Hovedytelse> synligeHovedytelser;
    private String status;
    private boolean erUtbetalt;

    private static Collector<Double, ?, Double> sumDouble = summingDouble((d) -> d);

    public Hovedutbetaling() {
        this.skalVises = true;
    }

    public Hovedutbetaling settUtbetaltSum() {
        this.nettoUtbetalt = synligeHovedytelser
                .stream()
                .map( hovedytelse -> hovedytelse.getNettoUtbetalt() )
                .collect(sumDouble);

        return this;
    }

    public void skalViseHovedutbetaling(boolean skalVises) {
        this.skalVises = skalVises;
    }

    public DateTime getHovedytelsesdato() {
        return hovedytelsesdato;
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

    public String getId() {
        return id;
    }

    public Hovedutbetaling withHovedytelsesdato(DateTime hovedytelsesdato) {
        this.hovedytelsesdato = hovedytelsesdato;
        return this;
    }

    public Hovedutbetaling withHovedytelser(List<Hovedytelse> hovedytelser) {
        this.hovedytelser = hovedytelser;
        this.synligeHovedytelser = hovedytelser;
        return this;
    }

    public Hovedutbetaling withId(String id) {
        this.id = id;
        return this;
    }

    public Hovedutbetaling withUtbetalingStatus(String status) {
        this.status = status;
        return this;
    }

    public Hovedutbetaling withIsUtbetalt(boolean erUtbetalt) {
        this.erUtbetalt = erUtbetalt;
        return this;
    }

    public boolean skalViseHovedutbetaling() {
        return skalVises && synligeHovedytelser.size() != 0 && hovedytelser.size() != 1;
    }

    public boolean isUtbetalt() {
        return erUtbetalt;
    }

    public String getStatus() {
        return status;
    }
}
