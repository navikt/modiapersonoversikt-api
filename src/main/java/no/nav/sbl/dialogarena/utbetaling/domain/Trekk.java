package no.nav.sbl.dialogarena.utbetaling.domain;

@SuppressWarnings("all")
public class Trekk {

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

}
