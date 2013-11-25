package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.Serializable;

public class Filter implements Serializable {

    public static final String ENDRET = "filter.endret";

    private static final String MOTTAKERKODE_ARBEIDSGIVER = "arbeidsgiver";
    private static final String MOTTAKERKODE_BRUKER = "bruker";

    private IModel<LocalDate> startDato;
    private IModel<LocalDate> sluttDato;
    private Boolean visBruker;
    private Boolean visArbeidsgiver;

    public Filter(LocalDate startDato, LocalDate sluttDato, Boolean brukerCheckbox, Boolean arbeidsgiverCheckbox) {
        this.visBruker = brukerCheckbox;
        this.visArbeidsgiver = arbeidsgiverCheckbox;
        this.startDato = new Model<>(startDato);
        this.sluttDato = new Model<>(sluttDato);
    }

    public IModel<LocalDate> getStartDato() {
        return startDato;
    }

    public DateTime getStartDate() {
        return startDato.getObject().toDateTimeAtCurrentTime();
    }

    public IModel<LocalDate> getSluttDato() {
        return sluttDato;
    }

    public DateTime getSluttDate() {
        return sluttDato.getObject().toDateTimeAtCurrentTime();
    }

    public boolean filtrerPaaDatoer(LocalDate utbetalingsDato) {
        return utbetalingsDato.isAfter(this.getStartDato().getObject()) &&
                utbetalingsDato.isBefore(this.getSluttDato().getObject());
    }

    public boolean filtrerPaaMottaker(String mottakerkode) {
        boolean visArbeidsgiver = this.visArbeidsgiver && MOTTAKERKODE_ARBEIDSGIVER.equalsIgnoreCase(mottakerkode);
        boolean visBruker = this.visBruker && MOTTAKERKODE_BRUKER.equalsIgnoreCase(mottakerkode);
        return visArbeidsgiver || visBruker;
    }

}
