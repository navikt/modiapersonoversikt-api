package no.nav.modiapersonoversikt.service.utbetaling.domain;

import java.io.Serializable;

@SuppressWarnings("all")
public class Underytelse implements Serializable {
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

    public Underytelse withYtelseBeloep(Double ytelsesBeloep) {
        this.ytelseBeloep = ytelsesBeloep;
        return this;
    }

    public Underytelse withYtelsesType(String ytelseskomponenttype) {
        this.ytelsesType = ytelseskomponenttype;
        return this;
    }

    public Underytelse withSatsBeloep(Double satsbeloep) {
        this.satsBeloep = satsbeloep;
        return this;
    }

    public Underytelse withSatsType(String satstype) {
        this.satsType = satstype;
        return this;
    }

    public Underytelse withSatsAntall(Double satsantall) {
        this.satsAntall = satsantall;
        return this;
    }
}
