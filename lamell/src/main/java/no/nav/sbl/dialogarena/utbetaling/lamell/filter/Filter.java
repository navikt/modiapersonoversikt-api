package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.sbl.dialogarena.utbetaling.domain.Mottaker;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.joda.time.LocalDate;

public class Filter {

    private IModel<LocalDate> startDato;
    private IModel<LocalDate> sluttDato;
    private IModel<Mottaker> mottaker;

    public Filter(LocalDate startDato, LocalDate sluttDato, Mottaker mottaker) {
        this.startDato = new Model<>(startDato);
        this.sluttDato = new Model<>(sluttDato);
        this.mottaker = new Model<>(mottaker);
    }

    public IModel<LocalDate> getStartDato() {
        return startDato;
    }

    public IModel<LocalDate> getSluttDato() {
        return sluttDato;
    }

    public IModel<Mottaker> getMottaker() {
        return mottaker;
    }
}
