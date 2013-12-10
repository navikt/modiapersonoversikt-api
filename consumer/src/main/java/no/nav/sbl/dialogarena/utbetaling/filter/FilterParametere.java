package no.nav.sbl.dialogarena.utbetaling.filter;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class FilterParametere implements Serializable {

    public static final String ENDRET = "filterParametere.endret";

    private LocalDate startDato;
    private LocalDate sluttDato;
    private Boolean visBruker;
    private Boolean visArbeidsgiver;

    private List<ValgtYtelse> valgteYtelser;

    public FilterParametere(LocalDate startDato, LocalDate sluttDato, Boolean visBruker, Boolean visArbeidsgiver, Set<String> hovedYtelser) {
        this.startDato = startDato;
        this.sluttDato = sluttDato;
        this.visBruker = visBruker;
        this.visArbeidsgiver = visArbeidsgiver;
        this.valgteYtelser = lagValgteYtelser(hovedYtelser);
    }

    private List<ValgtYtelse> lagValgteYtelser(Set<String> hovedYtelser) {
        List<ValgtYtelse> list = new ArrayList<>();
        for (String ytelse : hovedYtelser) {
            list.add(new ValgtYtelse(true, ytelse));
        }
        return list;
    }

    public Boolean getVisArbeidsgiver() {
        return visArbeidsgiver;
    }

    public Boolean getVisBruker() {
        return visBruker;
    }

    public List<ValgtYtelse> getValgteYtelser() {
        return valgteYtelser;
    }

    public LocalDate getSluttDato() {
        return sluttDato;
    }

    public LocalDate getStartDato() {
        return startDato;
    }

    public void setStartDato(LocalDate startDato) {
        if (startDato != null) {
            this.startDato = startDato;
        }
    }

    public void setSluttDato(LocalDate sluttDato) {
        if (sluttDato != null) {
            this.sluttDato = sluttDato;
        }
    }

    public static class ValgtYtelse implements Serializable {
        private Boolean valgt;
        private String ytelse;

        public ValgtYtelse(Boolean valgt, String ytelse) {
            this.valgt = valgt;
            this.ytelse = ytelse;
        }

        public void setValgt(Boolean valgt) {
            this.valgt = valgt;
        }

        public Boolean getValgt() {
            return valgt;
        }

        public String getYtelse() {
            return ytelse;
        }
    }
}
