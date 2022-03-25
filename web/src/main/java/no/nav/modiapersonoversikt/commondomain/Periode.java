package no.nav.modiapersonoversikt.commondomain;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.io.Serializable;

public class Periode implements Serializable {

    private LocalDate from;
    private LocalDate to;

    public Periode() {
    }

    public Periode(LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
    }

    public Periode(LocalDateTime from, LocalDateTime toan) {//Ikke rename toan til to, da brekker plutselig orika mapping
        this.from = new LocalDate(from);
        this.to = new LocalDate(toan);
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public boolean erGyldig() {
        LocalDate today = new LocalDate();

        if (from == null) {
            return false;
        }

        if (to == null) {
            return (today.isEqual(from) || today.isAfter(from));
        }

        return (today.isEqual(from) || today.isAfter(from)) && (today.isEqual(to) || today.isBefore(to));
    }
}
