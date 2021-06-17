package no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.sykepenger;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Periode;

import java.io.Serializable;

public class Gradering implements Serializable {
    private Periode gradert;
    private Double sykmeldingsgrad;

    public Gradering() {
    }

    public Gradering(Double grad, Periode periode) {
        this.sykmeldingsgrad = grad;
        this.gradert = periode;
    }

    public Double getSykmeldingsgrad() {
        return sykmeldingsgrad;
    }

    public void setSykmeldingsgrad(Double sykmeldingsgrad) {
        this.sykmeldingsgrad = sykmeldingsgrad;
    }

    public Periode getGradert() {
        return gradert;
    }

    public void setGradert(Periode gradert) {
        this.gradert = gradert;
    }
}
