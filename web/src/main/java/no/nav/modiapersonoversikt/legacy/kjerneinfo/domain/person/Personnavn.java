package no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person;

import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class Personnavn implements Serializable {

    private static final long serialVersionUID = 1L;
    private String fornavn;
    private String mellomnavn;
    private String etternavn;
    private String sammensattNavn;

    private Endringsinformasjon sistEndret;

    public String getFornavn() {
        return fornavn;
    }

    public void setFornavn(String fornavn) {
        this.fornavn = fornavn;
    }

    public String getEtternavn() {
        return etternavn;
    }

    public void setEtternavn(String etternavn) {
        this.etternavn = etternavn;
    }

    public String getMellomnavn() {
        return mellomnavn;
    }

    public void setMellomnavn(String mellomnavn) {
        this.mellomnavn = mellomnavn;
    }

    public Endringsinformasjon getSistEndret() {
        return sistEndret;
    }

    public void setSistEndret(Endringsinformasjon sistEndret) {
        this.sistEndret = sistEndret;
    }

    @Override
    public String toString() {
        StringBuilder navn = new StringBuilder();
        if (isNotBlank(etternavn)) {
            if (isNotBlank(fornavn)) {
                navn = navn.append(fornavn);
                navn = navn.append(" ");
            }
            if (isNotBlank(mellomnavn)) {
                navn = navn.append(mellomnavn);
                navn = navn.append(" ");
            }
            navn = navn.append(etternavn);
        } else if (isNotBlank(sammensattNavn)) {
            navn = navn.append(sammensattNavn);
        }
        return navn.toString();
    }

    public String getSammensattNavn() {
        return sammensattNavn;
    }

    public void setSammensattNavn(String sammensattNavn) {
        this.sammensattNavn = sammensattNavn;
    }

    public static class With {

        private final Personnavn personnavn = new Personnavn();

        public With fornavn(String fornavn) {
            personnavn.fornavn = fornavn;
            return this;
        }

        public With mellomnavn(String mellomnavn) {
            personnavn.mellomnavn = mellomnavn;
            return this;
        }

        public With etternavn(String etternavn) {
            personnavn.etternavn = etternavn;
            return this;
        }

        public Personnavn done() {
            return personnavn;
        }
    }
}
