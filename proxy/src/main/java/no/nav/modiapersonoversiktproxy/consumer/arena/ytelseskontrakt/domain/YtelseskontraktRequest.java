package no.nav.modiapersonoversiktproxy.consumer.arena.ytelseskontrakt.domain;

import org.joda.time.LocalDate;

import java.io.Serializable;

public class YtelseskontraktRequest implements Serializable {
    private String fodselsnummer;
    /** Fra og med */
    private LocalDate from;
    /** Til og med */
    private LocalDate to;

    public YtelseskontraktRequest() {
    }

    public String getFodselsnummer() {
        return fodselsnummer;
    }

    public void setFodselsnummer(String fodselsnummer) {
        this.fodselsnummer = fodselsnummer;
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

    /**
     * Definisjon av equals og hashCode er nødvendig for å at cachen skal virke riktig
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof YtelseskontraktRequest that)) {
            return false;
        }

        if (fodselsnummerNotEquals(that)) {
            return false;
        }
        if (fromNotEquals(that)) {
            return false;
        }
        return !toNotEquals(that);
    }

    @Override
    public int hashCode() {
        int result;
        if (fodselsnummer != null) {
            result = fodselsnummer.hashCode();
        } else {
            result = 0;
        }
        if (from != null) {
            result = 31 * result + from.hashCode();
        } else {
            result = 31 * result;
        }
        if (to != null) {
            result = 31 * result + to.hashCode();
        } else {
            result = 31 * result;
        }
        return result;
    }

    private boolean toNotEquals(YtelseskontraktRequest that) {
        if (to != null) {
            return !to.equals(that.to);
        } else {
            return that.to != null;
        }
    }

    private boolean fromNotEquals(YtelseskontraktRequest that) {
        if (from != null) {
            return !from.equals(that.from);
        } else {
            return that.from != null;
        }
    }

    private boolean fodselsnummerNotEquals(YtelseskontraktRequest that) {
        if (fodselsnummer != null) {
            return !fodselsnummer.equals(that.fodselsnummer);
        } else {
            return that.fodselsnummer != null;
        }
    }
}
