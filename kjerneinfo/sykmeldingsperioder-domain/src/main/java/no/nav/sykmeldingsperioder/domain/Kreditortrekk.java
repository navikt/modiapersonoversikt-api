package no.nav.sykmeldingsperioder.domain;

import java.io.Serializable;

public class Kreditortrekk implements Serializable {
    private String kreditorsNavn;
    private Double belop;

    public Kreditortrekk() {
    }

    public Kreditortrekk(String kreditorsNavn, Double belop) {
        this.kreditorsNavn = kreditorsNavn;
        this.belop = belop;
    }

    public Kreditortrekk(Kreditortrekk other) {
        this.kreditorsNavn = other.kreditorsNavn;
        this.belop = other.belop;
    }

    public String getKreditorsNavn() {
        return kreditorsNavn;
    }

    public void setKreditorsNavn(String kreditorsNavn) {
        this.kreditorsNavn = kreditorsNavn;
    }

    public Double getBelop() {
        return belop;
    }

    public void setBelop(Double belop) {
        this.belop = belop;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }
        final Kreditortrekk kreditortrekk = (Kreditortrekk) obj;
        if (this.kreditorsNavn.equals(kreditortrekk.kreditorsNavn)) {
            return this.belop.doubleValue() == kreditortrekk.belop.doubleValue();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.kreditorsNavn.hashCode() + this.belop.hashCode();
    }
}
