package no.nav.sbl.dialogarena.utbetaling.domain;

@SuppressWarnings("all")
public class Underytelse {
    String ytelsesType;
    Double satsBeloep;
    String satsType;
    Double satsAntall;
    Double ytelseBeloep;

    public String getYtelsesType() {
        return ytelsesType;
    }

    public Double getSatsBeloep() {
        return satsBeloep;
    }

    public String getSatsType() {
        return satsType;
    }

    public Double getSatsAntall() {
        return satsAntall;
    }

    public Double getYtelseBeloep() {
        return ytelseBeloep;
    }
}
