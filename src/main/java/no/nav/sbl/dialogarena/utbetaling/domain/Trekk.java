package no.nav.sbl.dialogarena.utbetaling.domain;

import java.io.Serializable;

@SuppressWarnings("all")
public class Trekk implements Serializable {

    String trekksType;
    Double trekkBeloep;
    String kreditor;

    public String getTrekksType() {
        return trekksType;
    }

    public Double getTrekkBeloep() {
        return trekkBeloep;
    }

    public String getKreditor() {
        return kreditor;
    }

    public Trekk withTrekksType(String trekksType) {
        this.trekksType = trekksType;
        return this;
    }

    public Trekk withTrekkBeloep(double trekkbeloep) {
        this.trekkBeloep = trekkbeloep;
        return this;
    }

    public Trekk withKreditor(String kreditor) {
        this.kreditor = kreditor;
        return this;
    }
}
