package no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person;

import org.joda.time.LocalDateTime;

import java.io.Serializable;

public class Endringsinformasjon implements Serializable {

    private static final long serialVersionUID = 1L;
    private String endretAv;
    private LocalDateTime sistOppdatert;

    public String getEndretAv() {
        return endretAv;
    }

    public void setEndretAv(String endretAv) {
        this.endretAv = endretAv;
    }

    public LocalDateTime getSistOppdatert() {
        return sistOppdatert;
    }

    public void setSistOppdatert(LocalDateTime sistOppdatert) {
        this.sistOppdatert = sistOppdatert;
    }

    @Override
    public String toString() {
        return "Endringsinformasjon [endretAv = " + endretAv + ", sistOppdatert = " + sistOppdatert + "]";
    }

    public static class With {

        private final Endringsinformasjon endringsinformasjon = new Endringsinformasjon();

        public With endretAv(String endretAv) {
            endringsinformasjon.endretAv = endretAv;
            return this;
        }

        public With sistOppdatert(LocalDateTime date) {
            endringsinformasjon.sistOppdatert = date;
            return this;
        }

        public Endringsinformasjon done() {
            return endringsinformasjon;
        }
    }
}
