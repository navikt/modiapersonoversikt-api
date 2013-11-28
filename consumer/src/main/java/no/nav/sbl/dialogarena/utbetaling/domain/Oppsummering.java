package no.nav.sbl.dialogarena.utbetaling.domain;


import java.io.Serializable;

public class Oppsummering implements Serializable {
    public double utbetalt;
    public double trekk;
    public double brutto;

    @Override
    public String toString() {
        return "Oppsummering{" +
                "utbetalt=" + utbetalt +
                ", trekk=" + trekk +
                ", brutto=" + brutto +
                '}';
    }
}
