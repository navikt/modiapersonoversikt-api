package no.nav.modiapersonoversikt.consumer.arenainfotrygdproxy.domain;

import org.joda.time.LocalDate;

import java.io.Serializable;

public class SYFOPunkt implements Serializable {
    private String status;
    private LocalDate dato;
    private String syfoHendelse;
    private boolean fastOppfolgingspunkt;

    public LocalDate getDato() {
        return dato;
    }

    public void setDato(LocalDate dato) {
        this.dato = dato;
    }

    public boolean isFastOppfolgingspunkt() {
        return fastOppfolgingspunkt;
    }

    public void setFastOppfolgingspunkt(boolean fastOppfolgingspunkt) {
        this.fastOppfolgingspunkt = fastOppfolgingspunkt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSyfoHendelse() {
        return syfoHendelse;
    }

    public void setSyfoHendelse(String syfoHendelse) {
        this.syfoHendelse = syfoHendelse;
    }
}
