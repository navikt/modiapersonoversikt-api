package no.nav.brukerprofil.domain;

import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public class Navn implements Serializable {

    private String navn;
    private LocalDateTime endringstidspunkt;
    private String endretAv;

    public Navn(String navn) {
        setNavn(navn);
    }

    public String getNavn() {
        return Objects.isNull(navn) ? "" : navn;
    }

    public void setNavn(String navn) {
        this.navn = Optional.ofNullable(navn).orElse("").toUpperCase();
    }

    public LocalDateTime getEndringstidspunkt() {
        return endringstidspunkt;
    }

    public void setEndringstidspunkt(LocalDateTime endringstidspunkt) {
        this.endringstidspunkt = endringstidspunkt;
    }

    public String getEndretAv() {
        return endretAv;
    }

    public void setEndretAv(String endretAv) {
        this.endretAv = endretAv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Navn navn = (Navn) o;
        return getNavn().equalsIgnoreCase(navn.getNavn());
    }
}
