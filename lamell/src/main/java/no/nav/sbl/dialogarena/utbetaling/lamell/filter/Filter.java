package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.joda.time.LocalDate;

import java.io.Serializable;

public class Filter implements Serializable {

    private IModel<LocalDate> startDato;
    private IModel<LocalDate> sluttDato;
    private Boolean brukerCheckbox = true;
    private Boolean arbeidsgiverCheckbox = true;

    public Filter(LocalDate startDato, LocalDate sluttDato, Boolean brukerCheckbox, Boolean arbeidsgiverCheckbox) {
        this.brukerCheckbox = brukerCheckbox;
        this.arbeidsgiverCheckbox = arbeidsgiverCheckbox;
        this.startDato = new Model<>(startDato);
        this.sluttDato = new Model<>(sluttDato);
    }

    public Boolean getBrukerCheckbox() {
        return brukerCheckbox;
    }

    public Boolean getArbeidsgiverCheckbox() {
        return arbeidsgiverCheckbox;
    }

    public IModel<LocalDate> getStartDato() {
        return startDato;
    }

    public IModel<LocalDate> getSluttDato() {
        return sluttDato;
    }

}
