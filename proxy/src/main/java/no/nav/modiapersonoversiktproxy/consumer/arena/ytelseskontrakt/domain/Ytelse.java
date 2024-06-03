package no.nav.modiapersonoversiktproxy.consumer.arena.ytelseskontrakt.domain;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ytelse implements Serializable {

    private String type;
    private String status;
    private LocalDate datoKravMottat;
    private List<Vedtak> vedtak;
    private LocalDate fom;
    private LocalDate tom;
    private Integer dagerIgjenMedBortfall;
    private Integer ukerIgjenMedBortfall;

    public Ytelse() {
    }

    public List<Vedtak> getVedtak() {
        if (vedtak == null) {
            vedtak = new ArrayList<>();
        }
        return vedtak;
    }

    public void setVedtak(List<Vedtak> vedtak) {
        this.vedtak = vedtak;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getDatoKravMottat() {
        return datoKravMottat;
    }

    public void setDatoKravMottat(LocalDate datoKravMottat) {
        this.datoKravMottat = datoKravMottat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getFom() {
        return fom;
    }

    public void setFom(LocalDate fom) {
        this.fom = fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public void setTom(LocalDate tom) {
        this.tom = tom;
    }

    public Integer getDagerIgjenMedBortfall() {
        return dagerIgjenMedBortfall;
    }

    public void setDagerIgjenMedBortfall(Integer dagerIgjenMedBortfall) {
        this.dagerIgjenMedBortfall = dagerIgjenMedBortfall;
    }

    public Integer getUkerIgjenMedBortfall() {
        return ukerIgjenMedBortfall;
    }

    public void setUkerIgjenMedBortfall(Integer ukerIgjenMedBortfall) {
        this.ukerIgjenMedBortfall = ukerIgjenMedBortfall;
    }
}
