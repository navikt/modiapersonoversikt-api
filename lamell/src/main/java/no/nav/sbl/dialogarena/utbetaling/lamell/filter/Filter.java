package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.joda.time.LocalDate;

import java.io.Serializable;

public class Filter implements Serializable {

    public static final String ENDRET = "filter.endret";

    private IModel<LocalDate> startDato;
    private IModel<LocalDate> sluttDato;
    private Boolean visBruker = true;
    private Boolean visArbeidsgiver = true;

    public Filter(LocalDate startDato, LocalDate sluttDato, Boolean brukerCheckbox, Boolean arbeidsgiverCheckbox) {
        this.visBruker = brukerCheckbox;
        this.visArbeidsgiver = arbeidsgiverCheckbox;
        this.startDato = new Model<>(startDato);
        this.sluttDato = new Model<>(sluttDato);
    }

    public Boolean getVisBruker() {
        return visBruker;
    }

    public Boolean getVisArbeidsgiver() {
        return visArbeidsgiver;
    }

    public IModel<LocalDate> getStartDato() {
        return startDato;
    }

    public IModel<LocalDate> getSluttDato() {
        return sluttDato;
    }

}
