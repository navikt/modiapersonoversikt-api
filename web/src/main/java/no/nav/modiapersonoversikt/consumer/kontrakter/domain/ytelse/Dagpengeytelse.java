package no.nav.modiapersonoversikt.consumer.kontrakter.domain.ytelse;

public class Dagpengeytelse extends Ytelse {
    private Integer antallDagerIgjen;
    private Integer antallUkerIgjen;
    private Integer antallDagerIgjenPermittering;
    private Integer antallUkerIgjenPermittering;

    public Integer getAntallDagerIgjen() {
        return antallDagerIgjen;
    }

    public void setAntallDagerIgjen(Integer antallDagerIgjen) {
        this.antallDagerIgjen = antallDagerIgjen;
    }

    public Integer getAntallUkerIgjen() {
        return antallUkerIgjen;
    }

    public void setAntallUkerIgjen(Integer antallUkerIgjen) {
        this.antallUkerIgjen = antallUkerIgjen;
    }

    public Integer getAntallDagerIgjenPermittering() {
        return antallDagerIgjenPermittering;
    }

    public void setAntallDagerIgjenPermittering(Integer antallDagerIgjenPermittering) {
        this.antallDagerIgjenPermittering = antallDagerIgjenPermittering;
    }

    public Integer getAntallUkerIgjenPermittering() {
        return antallUkerIgjenPermittering;
    }

    public void setAntallUkerIgjenPermittering(Integer antallUkerIgjenPermittering) {
        this.antallUkerIgjenPermittering = antallUkerIgjenPermittering;
    }
}
