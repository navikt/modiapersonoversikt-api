package no.nav.kontrakter.consumer.fim.ytelseskontrakt.to;

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

        if (!(o instanceof YtelseskontraktRequest)) {
            return false;
        }

        YtelseskontraktRequest that = (YtelseskontraktRequest) o;

        if (fodselsnummerNotEquals(that)) {
            return false;
        }
        if (fromNotEquals(that)) {
            return false;
        }
        if (toNotEquals(that)) {
            return false;
        }

        return true;
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
            if (!to.equals(that.to)) {
                return true;
            }
        } else {
            if (that.to != null) {
                return true;
            }
        }
        return false;
    }

    private boolean fromNotEquals(YtelseskontraktRequest that) {
        if (from != null) {
            if (!from.equals(that.from)) {
                return true;
            }
        } else {
            if (that.from != null) {
                return true;
            }
        }
        return false;
    }

    private boolean fodselsnummerNotEquals(YtelseskontraktRequest that) {
        if (fodselsnummer != null) {
            if (!fodselsnummer.equals(that.fodselsnummer)) {
                return true;
            }
        } else {
            if (that.fodselsnummer != null) {
                return true;
            }
        }
        return false;
    }
}
