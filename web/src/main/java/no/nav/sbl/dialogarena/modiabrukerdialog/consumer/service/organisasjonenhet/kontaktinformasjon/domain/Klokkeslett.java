package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain;

public class Klokkeslett {

    public final String time;
    public final String minutt;
    public final String sekund;

    public Klokkeslett(int time, int minutt, int sekund) {
        this.time = String.valueOf(time);
        this.minutt = String.valueOf(minutt);
        this.sekund = String.valueOf(sekund);
    }

    public String getTime() {
        return time;
    }

    public String getMinutt() {
        return minutt;
    }

    public String getSekund() {
        return sekund;
    }
}
